package no.sandramoen.blipblop.actors

import com.badlogic.gdx.Application
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input.Keys
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.VertexAttributes
import com.badlogic.gdx.graphics.g3d.Material
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.scenes.scene2d.Stage
import no.sandramoen.blipblop.utils.BaseActor3D
import no.sandramoen.blipblop.utils.Stage3D
import kotlin.math.abs

open class Player(x: Float = 0f, y: Float = 0f, z: Float = 0f, s: Stage3D, f: Stage, bottomPlayer: Boolean) : BaseActor3D(x, y, z, s) {
    private val tag = "Player"
    private val touchDeadZone = .01f
    private val speedPlayerAndroid = 40f
    private val speedPlayerDesktop = 3f
    private var accelerationPlayerDesktop = 1f
    private val speedAI = 1.5f
    private var accelerationAI = 1f
    private val leftWorldBounds = -5.7f
    private val rightWorldBounds = 5.7f
    private var enableAIWithDelay = false
    private var enableAiWithDelayCount = 0f
    private var enableAiWithDelayFrequency = 5f
    private var topPlayerYPosition = 5.3f
    private var bottomPlayerYPosition = -5.3f
    private var normalizedXPosition = .5f
    private var shadowBall: Ball
    private var shouldRunBallImpactAnimation = false
    private var ballImpactAnimationPercent = 0f
    private var label: PlayerLabel
    private var turnInterpolation = 0f

    val bottomPlayer: Boolean = bottomPlayer
    val width = 1.875f
    var enableAI = true
    var normalizedTouchX = .5f
    var score = 0
    var miss = 0
    var hit = 0
    var aiShouldMoveToX = 0f
    var pause = false

    init {
        // 3D model
        val modelBuilder = ModelBuilder()
        val boxMaterial = Material()
        val usageCode = VertexAttributes.Usage.Position + VertexAttributes.Usage.ColorPacked + VertexAttributes.Usage.Normal + VertexAttributes.Usage.TextureCoordinates
        val boxModel = modelBuilder.createBox(width, .225f, .225f, boxMaterial, usageCode.toLong())
        val position = Vector3(0f, 0f, 0f)
        setModelInstance(ModelInstance(boxModel, position))
        setBaseRectangle()

        // shadow ball
        shadowBall = Ball(0f, 0f, 0f, this.stage, isShadowBall = true)
        shadowBall.setVelocity(Vector2(0f, 0f))
        shadowBall.setPosition(Vector3(-10f, -10f, 0f))
        shadowBall.inPlay = false

        // miscellaneous
        loadTexture("player")
        setColor(Color.GREEN)
        if (bottomPlayer) setPosition(Vector3(0f, bottomPlayerYPosition, 0f))
        else setPosition(Vector3(0f, topPlayerYPosition, 0f))
        label = PlayerLabel(x, y, f, bottomPlayer)
    }

    override fun act(dt: Float) {
        if (pause) return
        super.act(dt)

        // controls
        normalizedXPosition = (getPosition().x - leftWorldBounds) / (rightWorldBounds - leftWorldBounds)
        if (enableAI) {
            when {
                aiShouldMoveToX > getPosition().x + width / 4 -> moveRight()
                aiShouldMoveToX < getPosition().x - width / 4 -> moveLeft()
                else -> standStill()
            }
        } else if (Gdx.app.type == Application.ApplicationType.Android) {
            when {
                normalizedTouchX > normalizedXPosition + touchDeadZone -> moveRight()
                normalizedTouchX < normalizedXPosition - touchDeadZone -> moveLeft()
                else -> standStill()
            }
        } else if (Gdx.app.type == Application.ApplicationType.Desktop) {
            when {
                bottomPlayer && Gdx.input.isKeyPressed(Keys.LEFT) -> moveLeft()
                bottomPlayer && Gdx.input.isKeyPressed(Keys.RIGHT) -> moveRight()
                !bottomPlayer && Gdx.input.isKeyPressed(Keys.A) -> moveLeft()
                !bottomPlayer && Gdx.input.isKeyPressed(Keys.D) -> moveRight()
                else -> standStill()
            }
        }

        // shadow ball
        if (bottomPlayer && shadowBall.inPlay && shadowBall.getPosition().y < bottomPlayerYPosition) {
            aiShouldMoveToX = shadowBall.getPosition().x
            shadowBall.setVelocity(Vector2(0f, 0f))
            shadowBall.setPosition(Vector3(-10f, -10f, 0f))
            shadowBall.inPlay = false
        } else if (!bottomPlayer && shadowBall.inPlay && shadowBall.getPosition().y > topPlayerYPosition) {
            aiShouldMoveToX = shadowBall.getPosition().x
            shadowBall.setVelocity(Vector2(0f, 0f))
            shadowBall.setPosition(Vector3(-10f, -10f, 0f))
            shadowBall.inPlay = false
        }

        // animation
        if (shouldRunBallImpactAnimation) ballImpactAnimation()

        // miscellaneous
        if (enableAIWithDelay) {
            enableAiWithDelayCount += dt
            if (enableAiWithDelayCount > enableAiWithDelayFrequency) {
                enableAIWithDelay = false
                enableAI = true
                enableAiWithDelayCount = 0f
            }
        }

        label.calculatePosition(normalizedXPosition, bottomPlayer)
        label.isVisible = enableAI
    }

    fun enableAIWithDelay() {
        enableAIWithDelay = true
    }

    fun disableAI() {
        enableAI = false
        enableAIWithDelay = false
        enableAiWithDelayCount = 0f
    }

    fun spawnShadowBall(ball: Ball) { // inspired by https://www.rharel.com/projects/pong-ai
        shadowBall.setPosition(ball.getPosition())
        shadowBall.setVelocity(Vector2(ball.getVelocity().x * 5f, ball.getVelocity().y * 5f))
        shadowBall.inPlay = true
    }

    fun ballImpact() {
        hit++
        shouldRunBallImpactAnimation = true
    }

    private fun moveLeft() {
        if (getPosition().x >= leftWorldBounds) {
            if (Gdx.app.type == Application.ApplicationType.Desktop || enableAI) {
                setTurnAngle(0f)
                val entitySpeed = if (enableAI) speedAI else speedPlayerDesktop
                var entityAcceleration = if (enableAI) accelerationAI else accelerationPlayerDesktop

                if (entityAcceleration < 2f) entityAcceleration += .05f
                val speed = entitySpeed * entityAcceleration
                moveBy(rotation.transform(Vector3(-speed, 0f, 0f)).scl(.1f))
            } else if (Gdx.app.type == Application.ApplicationType.Android) {
                val speed = speedPlayerAndroid * abs(normalizedTouchX - normalizedXPosition) // smooths movement
                moveBy(rotation.transform(Vector3(-speed, 0f, 0f)).scl(.1f))
            }
            tilt(left = true)
        }
    }

    private fun moveRight() {
        if (getPosition().x <= rightWorldBounds) {
            if (Gdx.app.type == Application.ApplicationType.Desktop || enableAI) {
                setTurnAngle(0f)
                val entitySpeed = if (enableAI) speedAI else speedPlayerDesktop
                var entityAcceleration = if (enableAI) accelerationAI else accelerationPlayerDesktop

                if (entityAcceleration < 2f) entityAcceleration += .05f
                val speed = entitySpeed * entityAcceleration
                moveBy(rotation.transform(Vector3(speed, 0f, 0f)).scl(.1f))
            } else if (Gdx.app.type == Application.ApplicationType.Android) {
                val speed = speedPlayerAndroid * abs(normalizedTouchX - normalizedXPosition) // smooths movement
                moveBy(rotation.transform(Vector3(speed, 0f, 0f)).scl(.1f))
            }
            tilt(left = false)
        }
    }

    private fun standStill() {
        accelerationPlayerDesktop = 1f
        accelerationAI = 1f
        turnInterpolation = 0f
        setTurnAngle(turnInterpolation)
    }

    private fun ballImpactAnimation() {
        ballImpactAnimationPercent += .2f
        if (ballImpactAnimationPercent > 1f) {
            ballImpactAnimationPercent = 0f
            shouldRunBallImpactAnimation = false
        }
        val scaleX = MathUtils.lerp(1f, 1.125f, ballImpactAnimationPercent)
        val scaleY = MathUtils.lerp(1f, .75f, ballImpactAnimationPercent)
        setScale(scaleX, scaleY, 1f)
    }

    private fun tilt(left: Boolean) {
        // tilt
        val max = 5f
        val amount = .05f
        if (left) {
            if (turnInterpolation < 0) turnInterpolation = 0f
            if (turnInterpolation < max) turnInterpolation += amount
            setTurnAngle(turnInterpolation)
        } else {
            if (turnInterpolation > 0) turnInterpolation = 0f
            if (turnInterpolation > -max) turnInterpolation -= amount
            setTurnAngle(turnInterpolation)
        }

        // reset y position
        if (bottomPlayer) setPosition(Vector3(getPosition().x, bottomPlayerYPosition, 0f))
        else setPosition(Vector3(getPosition().x, topPlayerYPosition, 0f))
    }
}
