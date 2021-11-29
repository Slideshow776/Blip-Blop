package no.sandramoen.blipblop.actors.challenges

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.VertexAttributes
import com.badlogic.gdx.graphics.g3d.Material
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.Array
import no.sandramoen.blipblop.actors.Ball
import no.sandramoen.blipblop.utils.BaseActor3D
import no.sandramoen.blipblop.utils.Stage3D
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute
import no.sandramoen.blipblop.actors.particleEffects.BlackHoleEffect
import no.sandramoen.blipblop.actors.particleEffects.BubblePopEffect
import no.sandramoen.blipblop.actors.particleEffects.ParticleActor
import no.sandramoen.blipblop.utils.BaseGame
import kotlin.math.floor


class BlackHole(x: Float, y: Float, s: Stage, balls: Array<Ball>, s3D: Stage3D) : Challenge(x, y, s) {
    private var tag = "BlackHole"
    private lateinit var blackHoleEntity: BaseActor3D
    private var showBlackHole = false
    private var time = 0f
    private var endFlag = false
    private var balls = balls
    override var title: String = BaseGame.myBundle!!.get("blackHole")

    private val endTime = 40f
    private val s3D = s3D
    private var effect: BlackHoleEffect

    init {
        initializeBlackHoleEntity()
        effect = BlackHoleEffect()
    }

    override fun act(dt: Float) {
        super.act(dt)

        // end challenge
        if (start && !finished) {
            time += dt
            if (time >= endTime && !endFlag) {
                endFlag = true
                endChallenge()
            }

            // black hole
            blackHoleEntity.turnZ(.1f)

            for (ball in balls) {
                if (ball.overlaps(blackHoleEntity)) {
                    ball.setMotionAngle(
                            ball.getMotionAngle() + (ball.getPosition().x - blackHoleEntity.getPosition().x) * 1f
                    )
                }
            }
        }
    }

    override fun startChallengeLogic() {
        super.startChallengeLogic()
        showBlackHole = true
        blackHoleEntity.moveBy(Vector3(-50f, 0f, 0f))

        effect.setScale(Gdx.graphics.height * .00004f)
        effect.setPosition(50f, 50f)
        stage.addActor(effect)
        effect.start()

        // ambient sound
        BaseGame.blackHoleMusic!!.volume = BaseGame.musicVolume
        BaseGame.blackHoleMusic!!.play()
    }

    override fun resetChallengeLogic() {
        blackHoleEntity.moveBy(Vector3(50f, 0f, 0f))
        effect.stop()
        BaseGame.blackHoleMusic!!.stop()
    }

    private fun initializeBlackHoleEntity() {
        // 3D model
        val modelBuilder = ModelBuilder()
        val boxMaterial = Material()
        boxMaterial.set(BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA))
        val usageCode = VertexAttributes.Usage.Position + VertexAttributes.Usage.ColorPacked + VertexAttributes.Usage.Normal + VertexAttributes.Usage.TextureCoordinates
        val boxModel = modelBuilder.createBox(5f, 5f, .1f, boxMaterial, usageCode.toLong())
        val position = Vector3(0f, 0f, 0f)

        blackHoleEntity = BaseActor3D(0f, 0f, 0f, s3D)
        blackHoleEntity.setModelInstance(ModelInstance(boxModel, position))
        blackHoleEntity.setBaseRectangle()
        blackHoleEntity.moveBy(Vector3(50f, 0f, 0f))

        // miscellaneous
        blackHoleEntity.loadTexture("black hole")
    }
}
