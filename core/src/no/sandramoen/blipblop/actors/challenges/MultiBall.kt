package no.sandramoen.blipblop.actors.challenges

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.Array
import no.sandramoen.blipblop.actors.Ball
import no.sandramoen.blipblop.utils.GameUtils
import no.sandramoen.blipblop.utils.Stage3D

class MultiBall(x: Float, y: Float, s: Stage, balls: Array<Ball>, mainStage3D: Stage3D) : Challenge(x, y, s) {
    private var tag = "MultiBall"
    private var time = 0f
    private val end = 5f
    private var triggerEnd = true
    private var balls = balls
    private var mainStage3D = mainStage3D

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
                val newBall = Ball(ball.getPosition().x, ball.getPosition().y, ball.getPosition().z, mainStage3D)
                newBall.setMotionAngle(ball.getMotionAngle() + MathUtils.random(-25f, 25f))
                newBall.setColor(GameUtils.randomColor())
                newBall.index = MathUtils.random(1, 1_000_000)
                balls.add(newBall)
            }
        } else {
            Gdx.app.error(tag, "ball was null when attempting to spawn...")
        }
    }
}
