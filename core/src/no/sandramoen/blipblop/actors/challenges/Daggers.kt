package no.sandramoen.blipblop.actors.challenges

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.VertexAttributes
import com.badlogic.gdx.graphics.g3d.Material
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.utils.Array
import no.sandramoen.blipblop.actors.Ball
import no.sandramoen.blipblop.actors.particleEffects.BubblePopEffect
import no.sandramoen.blipblop.actors.particleEffects.ParticleActor
import no.sandramoen.blipblop.utils.*

class Daggers(x: Float, y: Float, s: Stage, balls: Array<Ball>, s3D: Stage3D) : Challenge(x, y, s) {
    private var tag = "Daggers"
    override var title = "Daggers!"

    private val s3D = s3D
    private var balls = balls

    private val endTime = 30f
    private var time = 0f
    private var endFlag = false

    private var daggers: Array<BaseActor3D> = Array()
    private var timer = BaseActor(0f, 0f, s)

    private var daggerSpeed = 30f
    private var daggerAngle = MathUtils.random(-5f, 5f)
    private var daggerFrequency = MathUtils.random(.1f, .8f)

    override fun act(dt: Float) {
        super.act(dt)
        if (start && !finished) {
            // end challenge
            time += dt
            if (time >= endTime && !endFlag) {
                endFlag = true
                endChallenge()
            }

            // collision
            for (ball in balls) {
                for (dagger in daggers) {
                    if (ball.overlaps(dagger)) {
                        // ball
                        ball.preventOverlap(dagger)
                        ball.setVelocity(Vector2(ball.getVelocity().x, ball.getVelocity().y * -1))

                        // effect
                        val position2D = Vector2(
                                GameUtils.normalizeValues(dagger.getPosition().x, -9.1f, 9.1f) * 100 - 2.0f / 2,
                                GameUtils.normalizeValues(dagger.getPosition().y, -6.5f, 6.5f) * 100
                        )
                        var effect: ParticleActor = BubblePopEffect()
                        effect.setScale(Gdx.graphics.height * .00004f)
                        effect.setPosition(position2D.x, position2D.y)
                        stage.addActor(effect)
                        effect.start()

                        // miscellaneous
                        BaseGame.daggerSound!!.play(BaseGame.soundVolume, MathUtils.random(.5f, 1.5f), 0f)
                    }
                }
            }

            // daggers
            for (dagger in daggers) {
                if (dagger.getPosition().x < 9f) {
                    dagger.applyPhysics(dt)
                } else {
                    dagger.remove()
                }
            }
        }
    }

    override fun startChallengeLogic() {
        super.startChallengeLogic()
        timer.addAction(Actions.forever(Actions.sequence(
                Actions.delay(daggerFrequency),
                Actions.run {
                    daggers.add(createDaggers(Vector3(-9f, MathUtils.random(-4.5f, 4.5f), 0f)))
                }
        )))

        // ambient sound
        BaseGame.daggerMusic!!.volume = BaseGame.musicVolume * .9f
        BaseGame.daggerMusic!!.play()
    }

    override fun resetChallengeLogic() {
        super.resetChallengeLogic()
        time = 0f
        endFlag = false
        for (dagger in daggers) dagger.remove()
        daggers.clear()
        timer.clearActions()
        BaseGame.daggerMusic!!.stop()
    }

    private fun createDaggers(position: Vector3): BaseActor3D {
        val modelBuilder = ModelBuilder()
        val boxMaterial = Material()
        boxMaterial.set(BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA))
        val usageCode = VertexAttributes.Usage.Position + VertexAttributes.Usage.ColorPacked + VertexAttributes.Usage.Normal + VertexAttributes.Usage.TextureCoordinates
        val boxModel = modelBuilder.createBox(4f, .6f, .0001f, boxMaterial, usageCode.toLong())
        val baseActor = BaseActor3D(0f, 0f, 0f, s3D)
        baseActor.setModelInstance(ModelInstance(boxModel, position))
        baseActor.setBaseRectangle()
        baseActor.setPosition(position)
        baseActor.setSpeed(daggerSpeed)
        baseActor.setTurnAngleZ(daggerAngle)
        baseActor.setMotionAngle(daggerAngle)

        // miscellaneous
        baseActor.loadTexture("dagger")
        return baseActor
    }
}
