package no.sandramoen.blipblop.actors.challenges

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.VertexAttributes
import com.badlogic.gdx.graphics.g3d.Material
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.Array
import no.sandramoen.blipblop.actors.Ball
import no.sandramoen.blipblop.actors.particleEffects.BubblePopEffect
import no.sandramoen.blipblop.actors.particleEffects.ParticleActor
import no.sandramoen.blipblop.utils.BaseActor3D
import no.sandramoen.blipblop.utils.BaseGame
import no.sandramoen.blipblop.utils.GameUtils
import no.sandramoen.blipblop.utils.Stage3D

class Rectangles(x: Float, y: Float, s: Stage, balls: Array<Ball>, s3D: Stage3D) : Challenge(x, y, s) {
    private var tag = "Rectangles"
    override var title = "Rectangles!" // for some reason a 'c' followed by a 'k' does not work too well with the font..

    private val s3D = s3D
    private var balls = balls
    private var rectangles: Array<BaseActor3D> = Array()

    private val endTime = 30f
    private var time = 0f
    private var endFlag = false

    private var rectangleScale = 0f
    private var rectangleShowing = false

    override fun act(dt: Float) {
        super.act(dt)
        if (start && !finished) {
            // end challenge
            time += dt
            if (time >= endTime && !endFlag) {
                endFlag = true
                endChallenge()
            }

            if (!rectangleShowing) {
                for (rectangle in rectangles) {
                    fadeInRectangle(rectangle, dt)
                }
            } else {
                for (ball in balls) {
                    for (rectangle in rectangles) {
                        if (ball.overlaps(rectangle)) {
                            // ball
                            ball.preventOverlap(rectangle)
                            ball.setVelocity(Vector2(ball.getVelocity().x, ball.getVelocity().y * -1))

                            // effect
                            val position2D = Vector2(
                                    GameUtils.normalizeValues(rectangle.getPosition().x, -9.1f, 9.1f) * 100 - 2.0f / 2,
                                    GameUtils.normalizeValues(rectangle.getPosition().y, -6.5f, 6.5f) * 100
                            )
                            var effect: ParticleActor = BubblePopEffect()
                            effect.setScale(Gdx.graphics.height * .00004f)
                            effect.setPosition(position2D.x, position2D.y)
                            stage.addActor(effect)
                            effect.start()

                            // miscellaneous
                            BaseGame.rectangleExplosionSound!!.play(BaseGame.soundVolume, MathUtils.random(.5f, 1.5f), 0f)
                            rectangle.setPosition(Vector3(50f, 50f, 50f))
                            rectangle.remove()
                        }
                    }
                }
            }
        } else {
            for (rectangle in rectangles) {
                fadeOutRectangle(rectangle, dt)
            }
        }
    }

    override fun startChallengeLogic() {
        super.startChallengeLogic()
        val row0Height = .2f
        rectangles.add(createRectangle(Vector3(-5.5f, row0Height, 0f)))
        rectangles.add(createRectangle(Vector3(-3.3f, row0Height, 0f)))
        rectangles.add(createRectangle(Vector3(-1.1f, row0Height, 0f)))
        rectangles.add(createRectangle(Vector3(1.1f, row0Height, 0f)))
        rectangles.add(createRectangle(Vector3(3.3f, row0Height, 0f)))
        rectangles.add(createRectangle(Vector3(5.5f, row0Height, 0f)))

        val row1Height = -.2f
        rectangles.add(createRectangle(Vector3(-5.5f, row1Height, 0f)))
        rectangles.add(createRectangle(Vector3(-3.3f, row1Height, 0f)))
        rectangles.add(createRectangle(Vector3(-1.1f, row1Height, 0f)))
        rectangles.add(createRectangle(Vector3(1.1f, row1Height, 0f)))
        rectangles.add(createRectangle(Vector3(3.3f, row1Height, 0f)))
        rectangles.add(createRectangle(Vector3(5.5f, row1Height, 0f)))
    }

    override fun resetChallengeLogic() {
        super.resetChallengeLogic()
        time = 0f
        endFlag = false
        rectangleShowing = false
        rectangleScale = 0f
        for (rectangle in rectangles) {
            rectangle.setPosition(Vector3(50f, 50f, 50f))
            rectangle.remove()
        }
        rectangles.clear()
    }

    private fun createRectangle(position: Vector3): BaseActor3D {
        val modelBuilder = ModelBuilder()
        val boxMaterial = Material()
        // boxMaterial.set(BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA))
        val usageCode = VertexAttributes.Usage.Position + VertexAttributes.Usage.ColorPacked + VertexAttributes.Usage.Normal + VertexAttributes.Usage.TextureCoordinates
        val boxModel = modelBuilder.createBox(2f, .3f, .3f, boxMaterial, usageCode.toLong())
        val baseActor = BaseActor3D(0f, 0f, 0f, s3D)
        baseActor.setModelInstance(ModelInstance(boxModel, position))
        baseActor.setBaseRectangle()
        baseActor.setPosition(position)
        baseActor.setScale(0f, 0f, 0f)

        // miscellaneous
        baseActor.loadTexture("player")
        baseActor.setColor(GameUtils.randomColor())
        return baseActor
    }

    private fun fadeInRectangle(rectangle: BaseActor3D, dt: Float) {
        rectangleScale += dt * .1f
        if (rectangleScale < 1f) {
            rectangle.setScale(rectangleScale, rectangleScale, rectangleScale)
        } else if (rectangleScale >= 1f) {
            rectangleShowing = true
        }
    }

    private fun fadeOutRectangle(rectangle: BaseActor3D, dt: Float) {
        if (rectangleScale >= 0f) {
            rectangleScale -= dt * .4f
            if (rectangleScale < 0f) {
                rectangleScale = 0f
                rectangleShowing = false
            }
            rectangle.setScale(rectangleScale, rectangleScale, rectangleScale)
        }
    }
}