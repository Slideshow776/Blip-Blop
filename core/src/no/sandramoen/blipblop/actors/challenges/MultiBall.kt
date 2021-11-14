package no.sandramoen.blipblop.actors.challenges

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.Array
import no.sandramoen.blipblop.actors.Ball
import no.sandramoen.blipblop.actors.particleEffects.BubblePopEffect
import no.sandramoen.blipblop.actors.particleEffects.ParticleActor
import no.sandramoen.blipblop.utils.GameUtils
import no.sandramoen.blipblop.utils.Stage3D

class MultiBall(x: Float, y: Float, s2D: Stage, balls: Array<Ball>, mainStage3D: Stage3D) : Challenge(x, y, s2D) {
    private var tag = "MultiBall"
    private var time = 0f
    private val end = 5f
    private var triggerEnd = true
    private var balls = balls
    private var mainStage3D = mainStage3D
    private var s2D = s2D

    override var title = "Multi Ball!"
    var shouldSpawn = false

    override fun startChallengeLogic() {
        super.startChallengeLogic()
        time = 0f
        triggerEnd = true
        shouldSpawn = true
    }

    override fun act(dt: Float) {
        super.act(dt)
        if (time > end && triggerEnd) {
            triggerEnd = false
            endChallenge()
        } else if (start && triggerEnd) {
            time += dt
        }

        if (start && shouldSpawn)
            spawnBalls()
    }

    private fun spawnBalls() {
        shouldSpawn = false

        var ball: Ball? = null
        for (b in balls) {
            if (b.inPlay) {
                ball = b
                break
            }
        }

        if (ball != null) {
            val numBalls = MathUtils.random(3, 5)
            for (i in balls.size..(numBalls + balls.size - 1)) {
                val newBall = Ball(ball.getPosition().x, ball.getPosition().y, ball.getPosition().z, s2D,  mainStage3D)
                newBall.setMotionAngle(ball.getMotionAngle() + MathUtils.random(-25f, 25f))
                newBall.setColor(GameUtils.randomLightColor(min=.8f))
                newBall.index = MathUtils.random(1, 1_000_000)
                balls.add(newBall)
            }

            // effect
            val position2D = Vector2(
                    GameUtils.normalizeValues(ball.getPosition().x, -9.1f, 9.1f) * 100 - 2.0f / 2,
                    GameUtils.normalizeValues(ball.getPosition().y, -6.5f, 6.5f) * 100
            )
            var effect: ParticleActor = BubblePopEffect()
            effect.setScale(Gdx.graphics.height * .00004f)
            effect.setPosition(position2D.x, position2D.y)
            stage.addActor(effect)
            effect.start()
        } else {
            Gdx.app.error(tag, "ball was null when attempting to spawn...")
        }
    }
}
