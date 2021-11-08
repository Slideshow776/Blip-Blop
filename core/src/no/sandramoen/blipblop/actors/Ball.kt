package no.sandramoen.blipblop.actors

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.VertexAttributes
import com.badlogic.gdx.graphics.g3d.Material
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.scenes.scene2d.Stage
import no.sandramoen.blipblop.actors.particleEffects.*
import no.sandramoen.blipblop.utils.BaseActor3D
import no.sandramoen.blipblop.utils.BaseGame
import no.sandramoen.blipblop.utils.GameUtils
import no.sandramoen.blipblop.utils.Stage3D
import java.util.Timer
import kotlin.concurrent.schedule

class Ball(x: Float, y: Float, z: Float, stage: Stage, s3D: Stage3D, isShadowBall: Boolean = false) :
        BaseActor3D(x, y, z, s3D) {
    private val tag = "Ball"
    private val isShadowBall: Boolean = isShadowBall
    private val ballSpeed = 12f
    private var shouldRunWallAnimation = false
    private var wallAnimationPercent = 0f
    private var shouldRunPlayerImpactAnimation = false
    private var playerImpactAnimationPercent = 0f

    var inPlay = true
    var pause = false
    var index = 0

    private var leftWall: Wall
    private var rightWall: Wall

    private var leftBounce = false
    private var normalizedYPosition = 0f
    private var horizontalTopBounds = 6.5f
    private var horizontalBottomBounds = -6.5f
    private var s2D = stage

    init {
        // 3D model
        val modelBuilder = ModelBuilder()
        val sphereMaterial = Material()
        if (isShadowBall) sphereMaterial.set(BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA))
        val usageCode =
                VertexAttributes.Usage.Position + VertexAttributes.Usage.ColorPacked + VertexAttributes.Usage.Normal + VertexAttributes.Usage.TextureCoordinates
        val radius = if (isShadowBall) .0f else .5f
        val model = modelBuilder.createSphere(
                radius,
                radius * BaseGame.RATIO,
                radius,
                32,
                32,
                sphereMaterial,
                usageCode.toLong()
        )
        val position = Vector3(0f, 0f, 0f)
        setModelInstance(ModelInstance(model, position))
        setBasePolygon()

        // miscellaneous
        if (isShadowBall) {
            setColor(Color.PURPLE) // for debug
            loadTexture("blankPixel") // makes ball invisible
        } else {
            BaseGame.startSound!!.play(BaseGame.soundVolume)
        }
        setSpeed(ballSpeed / 2)
        setMotionAngle(270f)

        // walls
        leftWall = Wall(-9.1f, 0f, 0f, s3D)
        rightWall = Wall(9.1f, 0f, 0f, s3D)
    }

    override fun act(dt: Float) {
        super.act(dt)
        if (pause) return

        // logic
        if (inPlay) {
            applyPhysics(dt)

            if (this.overlaps(leftWall)) {
                this.wallBounce(leftWall)
            } else if (this.overlaps(rightWall)) {
                this.wallBounce(rightWall)
            }

            // horizontal out of bounds
            if (!isShadowBall && getPosition().y >= horizontalTopBounds || getPosition().y <= horizontalBottomBounds)
                inPlay = false
        }

        // animation
        if (shouldRunWallAnimation) wallAnimation()
        if (shouldRunPlayerImpactAnimation) playerImpactAnimation()
    }

    fun reset() {
        if (isShadowBall) return
        setPosition(Vector3(0f, 0f, 0f))
        setSpeed(ballSpeed / 2) // pixels / seconds
        if (MathUtils.randomBoolean()) setMotionAngle(MathUtils.random(30f, 150f))
        else setMotionAngle(MathUtils.random(210f, 330f))

        BaseGame.startSound!!.play(BaseGame.soundVolume)
        collisionEnabled = true
        inPlay = true
    }

    fun playerImpact(player: Player) {
        val ballCenterX = getPosition().x // TODO: not perfect, but good enough(?)
        var paddlePercentHit: Float =
                (ballCenterX - (player.getPosition().x - (player.width / 2))) / player.width
        if (paddlePercentHit < 0f) paddlePercentHit = 0f
        else if (paddlePercentHit > 1f) paddlePercentHit = 1f
        var bounceAngle: Float
        if (player.bottomPlayer) {
            bounceAngle = MathUtils.lerp(150f, 30f, paddlePercentHit)
            if (!isShadowBall) BaseGame.blipSound!!.play(BaseGame.soundVolume)
        } else {
            bounceAngle = MathUtils.lerp(210f, 330f, paddlePercentHit)
            if (!isShadowBall) BaseGame.blopSound!!.play(BaseGame.soundVolume)
        }
        setMotionAngle(bounceAngle)
        setSpeed(ballSpeed) // pixels / seconds

        collisionEnabled = false
        Timer("EnablingCollision", false).schedule(250) { collisionEnabled = true }
        shouldRunPlayerImpactAnimation = true
    }

    private fun wallBounce(wall: Wall) {
        normalizedYPosition = GameUtils.normalizeValues(x = getPosition().y, min = horizontalBottomBounds, max = horizontalTopBounds)
        this.preventOverlap(wall)
        if (!isShadowBall) BaseGame.deflectSound!!.play(BaseGame.soundVolume, MathUtils.random(.8f, 1.2f), 0f)
        leftBounce = this.velocityVec.x <= 0
        this.velocityVec.x *= -1

        shouldRunWallAnimation = true
        if (!isShadowBall) startBounceEffect(s2D)
    }

    private fun wallAnimation() {
        wallAnimationPercent += .15f
        if (wallAnimationPercent > 1f) {
            wallAnimationPercent = 0f
            shouldRunWallAnimation = false
        }
        val scaleX = MathUtils.lerp(1f, .1f, wallAnimationPercent)
        val scaleY = MathUtils.lerp(1f, 1.5f, wallAnimationPercent)
        setScale(scaleX, scaleY, 1f)
    }

    private fun playerImpactAnimation() {
        playerImpactAnimationPercent += .15f
        if (playerImpactAnimationPercent > 1f) {
            playerImpactAnimationPercent = 0f
            shouldRunPlayerImpactAnimation = false
        }
        val scaleX = MathUtils.lerp(1f, 1.5f, playerImpactAnimationPercent)
        val scaleY = MathUtils.lerp(1f, .1f, playerImpactAnimationPercent)
        setScale(scaleX, scaleY, 1f)
    }

    private fun startBounceEffect(stage: Stage) {
        var effect: ParticleActor = if (leftBounce) BallImpactLeftEffect() else BallImpactRightEffect()
        effect.setScale(Gdx.graphics.height * .00004f)
        var xPosition = if (leftBounce) 2f else 98f
        effect.setPosition(xPosition, normalizedYPosition * 100 - 1 / 2)
        stage.addActor(effect)
        effect.start()
    }
}
