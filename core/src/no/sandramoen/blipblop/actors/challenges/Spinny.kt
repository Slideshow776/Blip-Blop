package no.sandramoen.blipblop.actors.challenges

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.utils.Array
import no.sandramoen.blipblop.actors.Ball
import no.sandramoen.blipblop.actors.Player
import no.sandramoen.blipblop.actors.particleEffects.BubblePopEffect
import no.sandramoen.blipblop.actors.particleEffects.ParticleActor
import no.sandramoen.blipblop.utils.*

class Spinny(x: Float, y: Float, s: Stage, balls: Array<Ball>, players: Array<Player>, s3D: Stage3D) : Challenge(x, y, s) {
    private var tag = "Spinny"
    override var title = BaseGame.myBundle!!.get("spinny")

    private val s3D = s3D
    private var balls = balls
    private var players = players

    private val endTime = 20f
    private var time = 0f
    private var endFlag = false

    private var reverse = true
    private val reverseSpeed = 1.1f // ball goes 10% faster
    private var turn = 0f
    private val spinTimer = BaseActor(0f, 0f, s)

    override fun act(dt: Float) {
        super.act(dt)
        if (start && !finished) {
            // end challenge
            time += dt
            if (time >= endTime && !endFlag) {
                endFlag = true
                endChallenge()
            }

            // balls
            for (ball in balls) {
                if (!ball.collisionEnabled && reverse) {
                    val randomVelocityX = if (MathUtils.randomBoolean()) -1 else 1
                    ball.setVelocity(Vector2(ball.getVelocity().x * reverseSpeed * randomVelocityX, ball.getVelocity().y * reverseSpeed))
                    for (player in players) {
                        player.shadowBall.setVelocity(Vector2(
                                ball.getVelocity().x * player.shadowBallSpeed,
                                ball.getVelocity().y * player.shadowBallSpeed
                        ))
                    }
                    turn = 0f
                    if (ball.getPosition().y <= 0) spinPlayer(players[0])
                    else spinPlayer(players[1])
                    BaseGame.spinSound!!.play(BaseGame.soundVolume * .125f, MathUtils.random(.75f, 1.25f), 0f)
                    reverse = false
                } else if (ball.collisionEnabled) {
                    reverse = true
                }
            }
        }
    }

    override fun resetChallengeLogic() {
        super.resetChallengeLogic()
        time = 0f
        endFlag = false
        reverse = true
        spinTimer.clearActions()
        turn = 0f
    }

    private fun spinPlayer(player: Player) {
        // player spin
        spinTimer.addAction(Actions.forever(Actions.sequence(
                Actions.run {
                    turn += 25f
                    player.setTurnAngleZ(turn)
                }
        )))
        spinTimer.addAction(Actions.sequence(
                Actions.delay(.4f),
                Actions.run {
                    spinTimer.clearActions()
                    player.setTurnAngleZ(0f)
                }
        ))

        // effect
        val position2D = Vector2(
                GameUtils.normalizeValues(player.getPosition().x, -9.1f, 9.1f) * 100 - 2.0f / 2,
                GameUtils.normalizeValues(player.getPosition().y, -6.5f, 6.5f) * 100
        )
        var effect: ParticleActor = BubblePopEffect()
        effect.setScale(Gdx.graphics.height * .00004f)
        effect.setPosition(position2D.x, position2D.y)
        stage.addActor(effect)
        effect.start()
    }
}
