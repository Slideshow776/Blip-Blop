package no.sandramoen.blipblop.actors

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.VertexAttributes
import com.badlogic.gdx.graphics.g3d.Material
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector3
import no.sandramoen.blipblop.utils.BaseActor
import no.sandramoen.blipblop.utils.BaseActor3D
import no.sandramoen.blipblop.utils.BaseGame
import no.sandramoen.blipblop.utils.Stage3D
import java.util.Timer
import kotlin.concurrent.schedule

class Ball(x: Float, y: Float, z: Float, s: Stage3D, isShadowBall: Boolean = false) :
    BaseActor3D(x, y, z, s) {
    private val tag = "Ball"
    private val isShadowBall: Boolean = isShadowBall
    private var shouldRunWallAnimation = false
    private var wallAnimationPercent = 0f
    private var shouldRunPlayerImpactAnimation = false
    private var playerImpactAnimationPercent = 0f

    val ballSpeed = 12f
    var inPlay = true
    var pause = false

    init {
        // 3D model
        val modelBuilder = ModelBuilder()
        val sphereMaterial = Material()
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
        if (isShadowBall) { setColor(Color.PURPLE) }
        else { BaseGame.startSound!!.play(BaseGame.soundVolume) }
        setSpeed(ballSpeed / 2)
        setMotionAngle(270f)
    }

    override fun act(dt: Float) {
        if (pause) return
        super.act(dt)

        // logic
        if (inPlay) {
            applyPhysics(dt)

            // vertical bounce
            if (getPosition().x >= 6.4f || getPosition().x <= -6.4f)
                wallBounce() // TODO: BUG: ball can get stuck in the wall..

            // horizontal out of bounds
            if (!isShadowBall && getPosition().y >= 6.5f || getPosition().y <= -6.5f)
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
        Timer("EnablingCollision", false).schedule(500) { collisionEnabled = true }
        shouldRunPlayerImpactAnimation = true
    }

    private fun wallBounce() {
        if (this.velocityVec.x < 0)
            setPosition(
                Vector3(
                    getPosition().x + .5f,
                    getPosition().y,
                    getPosition().z
                )
            ) // offset position so ball doesn't get stuck
        else
            setPosition(Vector3(getPosition().x - .15f, getPosition().y, getPosition().z))

        if (!isShadowBall) BaseGame.deflectSound!!.play(BaseGame.soundVolume)
        this.velocityVec.x *= -1

        shouldRunWallAnimation = true
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
}
