package no.sandramoen.blipblop.screens.gameplay

import com.badlogic.gdx.Application
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input.Keys
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener
import com.badlogic.gdx.utils.Array
import no.sandramoen.blipblop.actors.*
import no.sandramoen.blipblop.ui.*
import no.sandramoen.blipblop.utils.BaseGame
import no.sandramoen.blipblop.utils.BaseScreen3D
import no.sandramoen.blipblop.utils.GameUtils
import no.sandramoen.blipblop.utils.Transition
import kotlin.math.floor

open class LevelScreen : BaseScreen3D() {
    open var classicMode = true
    lateinit var ball: Ball
    lateinit var balls: Array<Ball>

    private lateinit var background: Background
    private var tag = "LevelScreen"
    private var games = 1
    private var gameTime = BaseGame.gameTime
    lateinit var players: Array<Player>
    open lateinit var score: Score
    private lateinit var winner: Winner
    private lateinit var gameMenu: GameMenu
    private lateinit var transition: Transition
    private var highestScore: Int = 0
    private var previousScore: Int = 0

    override fun initialize() {
        // miscellaneous
        tag = "LevelScreen"

        // background
        background = Background(0f, 0f, background2DStage, colour = Vector3(.06f, .06f, .06f))

        // players
        players = Array()
        players.add(Player(s = mainStage3D, stage2D = foreground2DStage, bottomPlayer = true))
        players.add(Player(s = mainStage3D, stage2D = foreground2DStage, bottomPlayer = false))

        // middle line
        MiddleWhiteLine(0f, 0f, foreground2DStage)

        // ball
        ball = Ball(0f, 0f, 0f, foreground2DStage, mainStage3D)
        balls = Array()
        balls.add(ball)

        // camera
        mainStage3D.setCameraPosition(0f, 0f, 10f)
        mainStage3D.setCameraDirection(0f, 0f, 0f)

        // gui
        gameMenu = GameMenu(uiTable)
        winner = Winner(uiTable)
        score = Score(uiTable)

        gameMenu.restart.addListener(object : ActorGestureListener() {
            override fun tap(event: InputEvent?, x: Float, y: Float, count: Int, button: Int) {
                BaseGame.gameStartSound!!.play(BaseGame.soundVolume)
                restart()
                for (player in players) player.pause = false
                for (ball in balls) ball.pause = false
                gameMenu.disappear()
            }
        })

        // transition
        transition = Transition(0f, 0f, transitionStage)
        transition.fadeOut()
    }

    override fun update(dt: Float) {
        // player
        for (player in players) {
            for (ball in balls) {
                if (ball.overlaps(player) && !ball.pause) {
                    ball.playerImpact(player)

                    player.ballImpact()

                    if (player.bottomPlayer && players[1].enableAI)
                        players[1].spawnShadowBall(ball)
                    else if (!player.bottomPlayer && players[0].enableAI)
                        players[0].spawnShadowBall(ball)
                }
            }
        }

        // ball
        for (i in 0 until balls.size) {
            if (!balls[i].inPlay && balls.size == 1) { // WARNING: this code should only run once
                // score
                if (balls[i].getPosition().y > 0f) {
                    players[0].score++
                    players[1].miss++
                } else {
                    players[0].miss++
                    players[1].score++
                }

                if (players[0].score > players[1].score && players[0].score > highestScore) {
                    highestScore = players[0].score
                    background.increaseSpeed = true
                } else if (players[1].score > players[0].score && players[1].score > highestScore) {
                    highestScore = players[1].score
                    background.increaseSpeed = true
                }

                score.setScore(players[1].score, players[0].score)
                // reportHitRating()
                games++

                if (players[0].score == 10)
                    score.topScoreWarning(top = false)
                if (players[1].score == 10)
                    score.topScoreWarning(top = true)

                if (players[0].score >= 11) {
                    winner.playAnimation(top = false)
                    gameOver()
                } else if (players[1].score >= 11) {
                    winner.playAnimation(top = true)
                    gameOver()
                }

                // ball
                balls[i].reset()
                players[0].aiShouldMoveToX = .5f
                players[1].aiShouldMoveToX = .5f

                // shadow ball
                if (players[0].enableAI && balls[i].getVelocity().y < 0) players[0].spawnShadowBall(balls[i])
                if (players[1].enableAI && balls[i].getVelocity().y > 0) players[1].spawnShadowBall(balls[i])
            } else if (!balls[i].inPlay) { // in case of multi ball
                var copy = Array<Ball>()
                for (temp in balls) {
                    if (temp.index == balls[i].index) {
                        temp.remove()
                    } else
                        copy.add(temp)
                }
                balls.clear() // balls = Array()
                for (ball in copy) balls.add(ball)
                return
            }
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
        if (keycode == Keys.BACK || keycode == Keys.ESCAPE || keycode == Keys.BACKSPACE) endGame()
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

    open fun endGame() {
        // screen transition
        for (ball in balls) {
            if (ball.pause) {
                BaseGame.clickSound!!.play(BaseGame.soundVolume)
                transition.fadeInToMenuScreen()
                break
            } else {
                for (player in players) {
                    player.pause = true
                    player.label.addAction(Actions.fadeOut(.125f))
                }
                ball.pause = true
                gameMenu.appear(delay = 0f)
                BaseGame.pauseSound!!.play(BaseGame.soundVolume)
                gameTime = BaseGame.gameTime
                score.resetWarnings()
            }
        }
    }

    open fun restart() {
        for (player in players) {
            player.score = 0
            player.enableAI = true
            player.label.addAction(Actions.fadeIn(.125f))
        }
        score.setScore(players[1].score, players[0].score)
        winner.resetAnimation()
        for (ball in balls) ball.remove()
        balls.clear()
        ball = Ball(0f, 0f, 0f, foreground2DStage, mainStage3D)
        balls.add(ball)
        ball.reset()
        if (players[0].enableAI && ball.getVelocity().y < 0) players[0].spawnShadowBall(ball)
        if (players[1].enableAI && ball.getVelocity().y > 0) players[1].spawnShadowBall(ball)
        gameMenu.disappear()
        background.restart()
    }

    open fun gameOver() {
        for (player in players) {
            player.pause = true
            player.label.addAction(Actions.fadeOut(.125f))
        }
        for (ball in balls) ball.pause = true
        gameMenu.appear()
        if (MathUtils.randomBoolean()) BaseGame.win01Sound!!.play(BaseGame.soundVolume)
        else BaseGame.win02Sound!!.play(BaseGame.soundVolume)
        background.increaseSpeed = false
        background.decreaseSpeed = true
    }

    open fun startAchievements(isClassic: Boolean) {
        background.addAction(Actions.forever(Actions.sequence(
                Actions.delay(BaseGame.registerAchievementFrequency),
                Actions.run {
                    for (ball in balls) {
                        if (
                                !ball.pause &&
                                BaseGame.gameTime <= BaseGame.biggestAchievementTime &&
                                BaseGame.isGPS &&
                                BaseGame.gps!!.isSignedIn()
                        ) {
                            try {
                                if (isClassic) {
                                    BaseGame.gps!!.incrementClassicAchievements()
                                    BaseGame.gameTime = gameTime
                                    GameUtils.saveGameState()
                                } else {
                                    BaseGame.gps!!.incrementChallengeAchievements()
                                    BaseGame.gameTime = gameTime
                                    GameUtils.saveGameState()
                                }
                            } catch (error: Throwable) {
                                Gdx.app.error(tag, "Could not increment achievement, error: $error")
                            }
                        }
                    }
                }
        )))
    }

    private fun reportHitRating() {
        val player1HitRating = players[1].hit.toDouble() / (players[1].hit + players[1].miss)
        val player0HitRating = players[0].hit.toDouble() / (players[0].hit + players[0].miss)
        println("\nGames: $games")
        println("top player: hit rating: ${String.format("%.2f", player1HitRating)}")
        println("bottom player: hit rating: ${String.format("%.2f", player0HitRating)}")
    }
}
