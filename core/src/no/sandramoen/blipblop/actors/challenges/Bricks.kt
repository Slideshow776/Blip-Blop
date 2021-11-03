package no.sandramoen.blipblop.actors.challenges

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
import no.sandramoen.blipblop.utils.BaseActor3D
import no.sandramoen.blipblop.utils.BaseGame
import no.sandramoen.blipblop.utils.GameUtils
import no.sandramoen.blipblop.utils.Stage3D

class Bricks(x: Float, y: Float, s: Stage, balls: Array<Ball>, s3D: Stage3D) : Challenge(x, y, s) {
    private var tag = "Bricks"
    override var title = "Rectangles!" // for some reason a 'c' followed by a 'k' does not work too well with the font..

    private val s3D = s3D
    private var balls = balls
    private var bricks: Array<BaseActor3D> = Array()

    private val endTime = 40f
    private var time = 0f
    private var endFlag = false

    private var brickScale = 0f
    private var bricksShowing = false

    override fun act(dt: Float) {
        super.act(dt)
        if (start && !finished) {
            // end challenge
            time += dt
            if (time >= endTime && !endFlag) {
                endFlag = true
                endChallenge()
            }

            if (!bricksShowing) {
                for (brick in bricks) {
                    fadeInBrick(brick, dt)
                }
            } else {
                for (ball in balls) {
                    for (brick in bricks) {
                        if (ball.overlaps(brick)) {
                            ball.preventOverlap(brick)
                            ball.setVelocity(Vector2(ball.getVelocity().x, ball.getVelocity().y * -1))
                            BaseGame.brickExplosionSound!!.play(BaseGame.soundVolume, MathUtils.random(0f, 2f), 0f)
                            brick.setPosition(Vector3(50f, 50f, 50f))
                            brick.remove()
                        }
                    }
                }
            }
        } else {
            for (brick in bricks) {
                fadeOutBrick(brick, dt)
            }
        }
    }

    override fun startChallengeLogic() {
        super.startChallengeLogic()
        bricks.add(createBricks(Vector3(-5.5f, .35f, 0f)))
        bricks.add(createBricks(Vector3(-3.3f, .35f, 0f)))
        bricks.add(createBricks(Vector3(-1.1f, .35f, 0f)))
        bricks.add(createBricks(Vector3(1.1f, .35f, 0f)))
        bricks.add(createBricks(Vector3(3.3f, .35f, 0f)))
        bricks.add(createBricks(Vector3(5.5f, .35f, 0f)))

        bricks.add(createBricks(Vector3(-5.5f, 0f, 0f)))
        bricks.add(createBricks(Vector3(-3.3f, 0f, 0f)))
        bricks.add(createBricks(Vector3(-1.1f, 0f, 0f)))
        bricks.add(createBricks(Vector3(1.1f, 0f, 0f)))
        bricks.add(createBricks(Vector3(3.3f, 0f, 0f)))
        bricks.add(createBricks(Vector3(5.5f, 0f, 0f)))

        bricks.add(createBricks(Vector3(-5.5f, -.35f, 0f)))
        bricks.add(createBricks(Vector3(-3.3f, -.35f, 0f)))
        bricks.add(createBricks(Vector3(-1.1f, -.35f, 0f)))
        bricks.add(createBricks(Vector3(1.1f, -.35f, 0f)))
        bricks.add(createBricks(Vector3(3.3f, -.35f, 0f)))
        bricks.add(createBricks(Vector3(5.5f, -.35f, 0f)))
    }

    override fun resetChallengeLogic() {
        super.resetChallengeLogic()
        time = 0f
        endFlag = false
        bricksShowing = false
        brickScale = 0f
        for (brick in bricks) {
            brick.setPosition(Vector3(50f, 50f, 50f))
            brick.remove()
        }
        bricks.clear()
    }

    private fun createBricks(position: Vector3): BaseActor3D {
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

    private fun fadeInBrick(brick: BaseActor3D, dt: Float) {
        brickScale += dt * .1f
        if (brickScale < 1f) {
            brick.setScale(brickScale, brickScale, brickScale)
        } else if (brickScale >= 1f) {
            bricksShowing = true
        }
    }

    private fun fadeOutBrick(brick: BaseActor3D, dt: Float) {
        if (brickScale >= 0f) {
            brickScale -= dt * .4f
            if (brickScale < 0f) {
                brickScale = 0f
                bricksShowing = false
            }
            brick.setScale(brickScale, brickScale, brickScale)
        }
    }
}