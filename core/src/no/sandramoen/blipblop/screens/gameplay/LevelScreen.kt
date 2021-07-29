package no.sandramoen.blipblop.screens.gameplay

import com.badlogic.gdx.Input.Keys
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
    private var games = 1

    override fun initialize() {
        // miscellaneous
        players = Array()
        players.add(Player(mainStage, true))
        players.add(Player(mainStage, false))
        ball = Ball(mainStage)
        ball.inPlay = true
        tempMiddleActor()
    }

    override fun update(dt: Float) {
        // paddle hit
        for (player: BaseActor in BaseActor.getList(mainStage, Player::class.java.canonicalName)) {
            if (ball.overlaps(player as Player)) {
                if (player.bottomPlayer && ball.y < player.y) return
                else if (!player.bottomPlayer && ball.y > player.y) return

                val ballCenterX = ball.x + ball.width / 2
                val paddlePercentHit: Float = (ballCenterX - player.x) / player.width
                var bounceAngle = 0f
                if (player.bottomPlayer) {
                    bounceAngle = MathUtils.lerp(150f, 30f, paddlePercentHit)
                    BaseGame.blipSound!!.play(BaseGame.soundVolume)
                } else {
                    bounceAngle = MathUtils.lerp(210f, 330f, paddlePercentHit)
                    BaseGame.blopSound!!.play(BaseGame.soundVolume)
                }
                ball.playerImpactAnimation()
                ball.setMotionAngle(bounceAngle)
                ball.setSpeed(ball.ballSpeed) // pixels / seconds
                ball.toggleCollision()

                player.ballImpactAnimation()
                player.hit++

                if (player.bottomPlayer && players[1].enableAI) players[1].spawnShadowBall(ball)
                else if (!player.bottomPlayer && players[0].enableAI) players[0].spawnShadowBall(ball)
            }
        }

        // ball
        if (!ball.inPlay) {
            if (ball.y > 50f) {
                players[0].score++
                players[1].miss++
            } else {
                players[1].score++
                players[0].miss++
            }
            ball.reset()
            players[0].aiShouldMoveToX = 50f
            players[1].aiShouldMoveToX = 50f

            if (players[1].enableAI && ball.getVelocity().y > 0) players[1].spawnShadowBall(ball)
            if (players[0].enableAI && ball.getVelocity().y < 0) players[0].spawnShadowBall(ball)

            reportHitRating()
            games++
        }
    }

    override fun keyUp(keycode: Int): Boolean {
        if (keycode == Keys.RIGHT || keycode == Keys.LEFT) players[0].enableAIWithDelay()
        if (keycode == Keys.A || keycode == Keys.D) players[1].enableAIWithDelay()
        return super.keyUp(keycode)
    }

    override fun keyDown(keycode: Int): Boolean {
        if (keycode == Keys.RIGHT || keycode == Keys.LEFT) players[0].disableAI()
        if (keycode == Keys.A || keycode == Keys.D) players[1].disableAI()
        return false
    }

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        val worldCoordinates = camera.unproject(Vector3(screenX.toFloat(), screenY.toFloat(), 0f))

        if (worldCoordinates.y <= 50f) players[0].disableAI()
        else players[1].disableAI()
        return super.touchDown(screenX, screenY, pointer, button)
    }

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        val worldCoordinates = camera.unproject(Vector3(screenX.toFloat(), screenY.toFloat(), 0f))

        if (worldCoordinates.y <= 50f) players[0].enableAIWithDelay()
        else players[1].enableAIWithDelay()
        return super.touchUp(screenX, screenY, pointer, button)
    }

    override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean {
        val worldCoordinates = camera.unproject(Vector3(screenX.toFloat(), screenY.toFloat(), 0f))

        if (worldCoordinates.y <= 50f) players[0].touchX = worldCoordinates.x
        else players[1].touchX = worldCoordinates.x
        return super.touchDragged(screenX, screenY, pointer)
    }

    private fun reportHitRating() {
        val player1HitRating = players[1].hit.toDouble() / (players[1].hit + players[1].miss)
        val player0HitRating = players[0].hit.toDouble() / (players[0].hit + players[0].miss)
        println("\nGames: $games")
        println("top player: hit rating: ${String.format("%.2f", player1HitRating)}")
        println("bottom player: hit rating: ${String.format("%.2f", player0HitRating)}")
    }

    private fun tempMiddleActor() { // this will be replaced by graphics later
        val temp = BaseActor(0f, 0f, mainStage)
        temp.setPosition(0f, -1f)
        temp.height = 51f
        temp.width = 101f
    }
}
