package no.sandramoen.blipblop.screens.gameplay

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input.Keys
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener
import com.badlogic.gdx.utils.Array
import no.sandramoen.blipblop.actors.*
import no.sandramoen.blipblop.ui.*
import no.sandramoen.blipblop.utils.BaseGame
import no.sandramoen.blipblop.utils.BaseScreen3D

class LevelScreen : BaseScreen3D() {
    private val tag = "LevelScreen"
    private lateinit var ball: Ball
    private lateinit var players: Array<Player>
    private lateinit var score: Score
    private lateinit var winner: Winner
    private lateinit var gameMenu: GameMenu
    private var games = 1

    override fun initialize() {
        // miscellaneous
        players = Array()

        // players
        players.add(Player(s = mainStage3D, f = foreground2DStage, bottomPlayer = true))
        players.add(Player(s = mainStage3D, f = foreground2DStage, bottomPlayer = false))

        // ball
        ball = Ball(0f, 0f, 0f, mainStage3D)

        // background
        Background(s = mainStage3D).setPosition(Vector3(0f, 0f, -1f))

        // middle line
        val line = Box(0f, 0f, 0f, mainStage3D)
        line.setScale(15f, .02f, .02f)
        line.setColor(Color.WHITE)

        // camera
        mainStage3D.setCameraPosition(0f, 0f, 10f)
        mainStage3D.setCameraDirection(0f, 0f, 0f)

        // gui
        gameMenu = GameMenu(uiTable)
        winner = Winner(uiTable)
        score = Score(uiTable)

        gameMenu.restart.addListener(object : ActorGestureListener() {
            override fun tap(event: InputEvent?, x: Float, y: Float, count: Int, button: Int) {
                BaseGame.clickSound!!.play(BaseGame.soundVolume)
                restart()
                resume()
            }
        })
    }

    override fun update(dt: Float) {
        // player
        for (player in players) {
            if (ball.overlaps(player)) {
                ball.playerImpact(player)

                player.ballImpact()

                if (player.bottomPlayer && players[1].enableAI) players[1].spawnShadowBall(ball)
                else if (!player.bottomPlayer && players[0].enableAI) players[0].spawnShadowBall(
                    ball
                )
            }
        }

        // ball
        if (!ball.inPlay) {
            // score
            if (ball.getPosition().y > 0f) {
                players[0].score++
                players[1].miss++
            } else {
                players[0].miss++
                players[1].score++
            }
            score.setScore(players[1].score, players[0].score)
            // reportHitRating()
            games++
            if (players[0].score >= 10) {
                winner.bottomPlayerLabel.isVisible = true
                gameOver()
            } else if (players[1].score >= 10) {
                winner.topPlayerLabel.isVisible = true
                gameOver()
            }

            // ball
            ball.reset()
            players[0].aiShouldMoveToX = .5f
            players[1].aiShouldMoveToX = .5f

            // shadow ball
            if (players[0].enableAI && ball.getVelocity().y < 0) players[0].spawnShadowBall(ball)
            if (players[1].enableAI && ball.getVelocity().y > 0) players[1].spawnShadowBall(ball)

        }
    }

    override fun scrolled(amountX: Float, amountY: Float): Boolean {
        return false
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
        var normalizedPosY = screenY / Gdx.graphics.height.toFloat()
        if (normalizedPosY >= .5f) players[0].disableAI()
        else players[1].disableAI()
        return super.touchDown(screenX, screenY, pointer, button)
    }

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        var normalizedPosY = screenY / Gdx.graphics.height.toFloat()
        if (normalizedPosY >= .5f) players[0].enableAIWithDelay()
        else players[1].enableAIWithDelay()
        return super.touchUp(screenX, screenY, pointer, button)
    }

    override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean {
        var normalizedPosX = screenX / Gdx.graphics.width.toFloat()
        var normalizedPosY = screenY / Gdx.graphics.height.toFloat()

        if (normalizedPosY >= .5f) players[0].normalizedTouchX = normalizedPosX
        else players[1].normalizedTouchX = normalizedPosX

        return super.touchDragged(screenX, screenY, pointer)
    }

    override fun resume() {
        super.resume()
        for (player in players) player.pause = false
        ball.pause = false
        gameMenu.disappear()
    }

    private fun reportHitRating() {
        val player1HitRating = players[1].hit.toDouble() / (players[1].hit + players[1].miss)
        val player0HitRating = players[0].hit.toDouble() / (players[0].hit + players[0].miss)
        println("\nGames: $games")
        println("top player: hit rating: ${String.format("%.2f", player1HitRating)}")
        println("bottom player: hit rating: ${String.format("%.2f", player0HitRating)}")
    }

    private fun restart() {
        for (player in players) {
            player.score = 0
            player.enableAI = true
        }
        score.setScore(players[1].score, players[0].score)
        winner.reset()
        ball.reset()
        if (players[0].enableAI && ball.getVelocity().y < 0) players[0].spawnShadowBall(ball)
        if (players[1].enableAI && ball.getVelocity().y > 0) players[1].spawnShadowBall(ball)
        gameMenu.disappear()
    }

    private fun gameOver() {
        for (player in players) player.pause = true
        ball.pause = true
        gameMenu.appear()
    }
}
