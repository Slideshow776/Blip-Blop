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
import com.badlogic.gdx.utils.Array
import no.sandramoen.blipblop.actors.Ball
import no.sandramoen.blipblop.actors.Player
import no.sandramoen.blipblop.actors.particleEffects.BallImpactLeftEffect
import no.sandramoen.blipblop.actors.particleEffects.BallImpactRightEffect
import no.sandramoen.blipblop.actors.particleEffects.BubblePopEffect
import no.sandramoen.blipblop.actors.particleEffects.ParticleActor
import no.sandramoen.blipblop.utils.BaseActor3D
import no.sandramoen.blipblop.utils.BaseGame
import no.sandramoen.blipblop.utils.GameUtils
import no.sandramoen.blipblop.utils.Stage3D

class Bubbles(x: Float, y: Float, s: Stage, balls: Array<Ball>, players: Array<Player>, s3D: Stage3D) : Challenge(x, y, s) {
    private var tag = "Bubbles"
    override var title = "Bubbles!"

    private val s3D = s3D
    private var balls = balls
    private var players = players
    private var bubbles: Array<BaseActor3D> = Array()

    private val endTime = 20f
    private var time = 0f
    private var endFlag = false

    private var bubblesScale = 0f
    private var bubblesShowing = false

    override fun act(dt: Float) {
        super.act(dt)
        if (start && !finished) {
            // end challenge
            time += dt
            if (time >= endTime && !endFlag) {
                endFlag = true
                endChallenge()
            }

            if (!bubblesShowing) {
                for (bubble in bubbles) {
                    fadeInBubble(bubble, dt)
                }
            } else {
                for (ball in balls) { // collision
                    for (bubble in bubbles) {
                        if (ball.overlaps(bubble)) {
                            ball.preventOverlap(bubble)
                            ball.setVelocity(Vector2(ball.getVelocity().x, ball.getVelocity().y * -1))
                            popBubble(bubble)
                        }
                    }
                    for (i in 0 until bubbles.size) { // movement
                        bubbles[i].applyPhysics(dt)
                        for (j in 0 until bubbles.size) { // collision
                            if (bubbles[i].overlaps(bubbles[j]) && bubbles[i].id != bubbles[j].id) { // other bubbles
                                bubbles[i].preventOverlap(bubbles[j])
                                bubbles[i].setMotionAngle(MathUtils.random(0f, 360f))
                            }
                        }
                        for (player in players) { // players
                            if (bubbles[i].overlaps(player)) {
                                popBubble(bubbles[i])
                            }
                        }
                        if (bubbles[i].getPosition().x > 5.8f || bubbles[i].getPosition().x < -5.8f)
                            bubbles[i].setVelocity(Vector2(bubbles[i].getVelocity().x * -1, bubbles[i].getVelocity().y))
                        if (bubbles[i].getPosition().y > 5.8f || bubbles[i].getPosition().y < -5.8f)
                            bubbles[i].setVelocity(Vector2(bubbles[i].getVelocity().x, bubbles[i].getVelocity().y * -1))
                    }
                }
            }
        } else {
            for (bubble in bubbles) {
                fadeOutBubble(bubble, dt)
            }
        }
    }

    override fun startChallengeLogic() {
        super.startChallengeLogic()
        for (i in 18 until 36)
            bubbles.add(createBubbles(Vector3(MathUtils.random(-5.5f, 5.5f), MathUtils.random(-5.5f, 5.5f), 0f)))

        for (bubble in bubbles) {
            bubble.setSpeed(MathUtils.random(.5f, 1.25f))
            bubble.setMotionAngle(MathUtils.random(0f, 360f))
        }
    }

    override fun resetChallengeLogic() {
        super.resetChallengeLogic()
        time = 0f
        endFlag = false
        bubblesShowing = false
        bubblesScale = 0f
        for (bubble in bubbles) {
            bubble.setPosition(Vector3(50f, 50f, 50f))
            bubble.remove()
        }
        bubbles.clear()
    }

    private fun createBubbles(position: Vector3): BaseActor3D {
        // 3D model
        val modelBuilder = ModelBuilder()
        val sphereMaterial = Material()
        sphereMaterial.set(BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA))
        val usageCode = VertexAttributes.Usage.Position + VertexAttributes.Usage.ColorPacked + VertexAttributes.Usage.Normal + VertexAttributes.Usage.TextureCoordinates
        val radius = 1f
        val model = modelBuilder.createSphere(
                radius,
                radius,
                radius,
                32,
                32,
                sphereMaterial,
                usageCode.toLong()
        )
        val baseActor = BaseActor3D(0f, 0f, 0f, s3D)
        baseActor.setModelInstance(ModelInstance(model, position))
        baseActor.setBasePolygon()
        baseActor.setPosition(position)

        // miscellaneous
        baseActor.loadTexture("bubble")
        baseActor.setColor(GameUtils.randomColor())
        return baseActor
    }

    private fun fadeInBubble(bubble: BaseActor3D, dt: Float) {
        bubblesScale += dt * .5f
        if (bubblesScale < 1f) {
            bubble.setScale(bubblesScale, bubblesScale, bubblesScale)
        } else if (bubblesScale >= 1f) {
            bubblesShowing = true
        }
    }

    private fun fadeOutBubble(bubble: BaseActor3D, dt: Float) {
        if (bubblesScale >= 0f) {
            bubblesScale -= dt * .8f
            if (bubblesScale < 0f) {
                bubblesScale = 0f
                bubblesShowing = false
            }
            bubble.setScale(bubblesScale, bubblesScale, bubblesScale)
        }
    }

    private fun popBubble(bubble: BaseActor3D) {
        // effect
        val position2D = Vector2(
                GameUtils.normalizeValues(bubble.getPosition().x, -9.1f, 9.1f) * 100 - 2.0f / 2,
                GameUtils.normalizeValues(bubble.getPosition().y, -6.5f, 6.5f) * 100
        )
        var effect: ParticleActor = BubblePopEffect()
        effect.setScale(Gdx.graphics.height * .00004f)
        effect.setPosition(position2D.x, position2D.y)
        stage.addActor(effect)
        effect.start()

        // miscellaneous
        BaseGame.bubblePopSound!!.play(BaseGame.soundVolume, MathUtils.random(.5f, 1.5f), 0f)
        bubble.setPosition(Vector3(50f, 50f, 50f))
        bubble.remove()
    }
}