package no.sandramoen.blipblop.screens.gameplay

import com.badlogic.gdx.math.MathUtils
import no.sandramoen.blipblop.actors.Ball
import no.sandramoen.blipblop.actors.Player
import no.sandramoen.blipblop.utils.BaseActor
import no.sandramoen.blipblop.utils.BaseGame
import no.sandramoen.blipblop.utils.BaseScreen

class LevelScreen : BaseScreen() {
    private val token = "LevelScreen"
    private lateinit var ball: Ball

    override fun initialize() {
        Player(mainStage, true)
        Player(mainStage, false)
        ball = Ball(mainStage)
        tempMiddleActor()
    }

    override fun update(dt: Float) {
        for (player: BaseActor in BaseActor.getList(mainStage, Player::class.java.canonicalName)) {
            if (ball.overlaps(player as Player) && ball.canBePaddled) {
                if (player.bottomPlayer && ball.y < player.y) return
                else if (!player.bottomPlayer && ball.y > player.y) return

                val ballCenterX = ball.x + ball.width / 2
                val paddlePercentHit: Float = (ballCenterX - player.x) / player.width
                var bounceAngle: Float
                if (player.bottomPlayer) {
                        bounceAngle = MathUtils.lerp(150f, 30f, paddlePercentHit)
                        BaseGame.blipSound!!.play(BaseGame.soundVolume)
                    }
                    else {
                        bounceAngle = MathUtils.lerp(210f, 330f, paddlePercentHit)
                        BaseGame.blopSound!!.play(BaseGame.soundVolume)
                    }
                player.hitAnimation()
                ball.hitAnimation()
                ball.setMotionAngle(bounceAngle)
                ball.setSpeed(ball.ballSpeed) // pixels / seconds
            }
        }
    }

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {

        return false
    }

    override fun keyDown(keycode: Int): Boolean { // desktop controls

        return false
    }

    private fun tempMiddleActor() { // this will be replaced by graphics later
        val temp = BaseActor(0f, 0f, mainStage)
        temp.setPosition(0f,-1f)
        temp.height = 51f
        temp.width = 101f
    }


}
