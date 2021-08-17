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
import no.sandramoen.blipblop.utils.BaseActor3D
import no.sandramoen.blipblop.utils.Stage3D
import java.util.*
import kotlin.concurrent.schedule
import kotlin.math.abs

open class Player(x: Float = 0f, y: Float = 0f, z: Float = 0f, s: Stage3D, bottomPlayer: Boolean) : BaseActor3D(x, y, z, s) {
    private val tag = "Player"
    private val touchDeadZone = .05f
    private val playerAndroidSpeed = 20f
    private val playerDesktopSpeed = 1.0f
    private val leftWorldBounds = -5.7f
    private val rightWorldBounds = 5.7f
    private var enableAIWithDelay = false
    private var enableAiWithDelayCount = 0f
    private var enableAiWithDelayFrequency = 5f
    private var topPlayerYPosition = 5.5f
    private var bottomPlayerYPosition = -5.5f
    private var normalizedXPosition = .5f
    private var shadowBall: Ball
    private var shouldRunBallImpactAnimation = false
    private var ballImpactAnimationPercent = 0f

    val bottomPlayer: Boolean = bottomPlayer
    val width = 1.875f
    var enableAI = true
    var normalizedTouchX = .5f
    var score = 0
    var miss = 0
    var hit = 0
    var desktopAcceleration = 1f
    var aiShouldMoveToX = 0f

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

        // miscellaneous
        loadTexture("player")
        setColor(Color.GREEN)
        if (bottomPlayer) setPosition(Vector3(0f, bottomPlayerYPosition, 0f))
        else setPosition(Vector3(0f, topPlayerYPosition, 0f))
    }

    override fun act(dt: Float) {
        super.act(dt)

        // controls
        if (enableAI) {
            when {
                aiShouldMoveToX > getPosition().x + width / 4 -> moveRight()
                aiShouldMoveToX < getPosition().x - width / 4 -> moveLeft()
                else -> standStill()
            }
        } else if (Gdx.app.type == Application.ApplicationType.Android && !enableAI) {
            normalizedXPosition = (getPosition().x - leftWorldBounds) / (rightWorldBounds - leftWorldBounds)
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
        if (shouldRunBallImpactAnimation) ballImpactAnimation(dt)

        // miscellaneous
        if (enableAIWithDelay) {
            enableAiWithDelayCount += dt
            if (enableAiWithDelayCount > enableAiWithDelayFrequency) {
                enableAIWithDelay = false
                enableAI = true
                enableAiWithDelayCount = 0f
            }
        }
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
                if (desktopAcceleration < 2f) desktopAcceleration += .05f
                val speed = playerDesktopSpeed * desktopAcceleration
                moveBy(rotation.transform(Vector3(-speed, 0f, 0f)).scl(.1f))
            } else if (Gdx.app.type == Application.ApplicationType.Android) {
                val speed = playerAndroidSpeed * abs(normalizedTouchX - normalizedXPosition) // smooths movement
                moveBy(rotation.transform(Vector3(-speed, 0f, 0f)).scl(.1f))
            }
        }
    }

    private fun moveRight() {
        if (getPosition().x <= rightWorldBounds) {
            if (Gdx.app.type == Application.ApplicationType.Desktop || enableAI) {
                if (desktopAcceleration < 2f) desktopAcceleration += .05f
                val speed = playerDesktopSpeed * desktopAcceleration
                moveBy(rotation.transform(Vector3(speed, 0f, 0f)).scl(.1f))
            } else if (Gdx.app.type == Application.ApplicationType.Android) {
                val speed = playerAndroidSpeed * abs(normalizedTouchX - normalizedXPosition) // smooths movement
                moveBy(rotation.transform(Vector3(speed, 0f, 0f)).scl(.1f))
            }
        }
    }

    private fun standStill() {
        desktopAcceleration = 1f
    }

    private fun ballImpactAnimation(dt: Float) {
        ballImpactAnimationPercent += .2f
        if (ballImpactAnimationPercent > 1f) {
            ballImpactAnimationPercent = 0f
            shouldRunBallImpactAnimation = false
        }
        val scaleX = MathUtils.lerp(1f, 1.125f, ballImpactAnimationPercent)
        val scaleY = MathUtils.lerp(1f, .75f, ballImpactAnimationPercent)
        setScale(scaleX, scaleY, 1f)
    }
}
