package no.sandramoen.blipblop.screens.gameplay

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector3
import no.sandramoen.blipblop.actors.Ball
import no.sandramoen.blipblop.actors.Player
import no.sandramoen.blipblop.utils.BaseActor
import no.sandramoen.blipblop.utils.BaseGame
import no.sandramoen.blipblop.utils.BaseScreen
import com.badlogic.gdx.utils.Array

class LevelScreen : BaseScreen() {
    private val tag = "LevelScreen"
    private lateinit var ball: Ball
    private lateinit var players: Array<Player>

    override fun initialize() {
        players =  Array()
        players.add(Player(mainStage, true))
        players.add(Player(mainStage, false))
        ball = Ball(mainStage)
        tempMiddleActor()
    }

    override fun update(dt: Float) {

        // paddle hit
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
                player.ballImpactAnimation()
                ball.playerImpactAnimation()
                ball.setMotionAngle(bounceAngle)
                ball.setSpeed(ball.ballSpeed) // pixels / seconds
            }
        }
    }

    override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean {
        val worldCoordinates = camera.unproject(Vector3(screenX.toFloat(), screenY.toFloat(), 0f))

        if (worldCoordinates.y <= 50f) players[0].touchX = worldCoordinates.x
        else players[1].touchX = worldCoordinates.x

        return super.touchDragged(screenX, screenY, pointer)
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
