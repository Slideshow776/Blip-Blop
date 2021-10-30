package no.sandramoen.blipblop.screens.gameplay

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import no.sandramoen.blipblop.actors.Background
import no.sandramoen.blipblop.actors.Ball
import no.sandramoen.blipblop.actors.challenges.FoggyVeil
import no.sandramoen.blipblop.actors.challenges.MultiBall
import no.sandramoen.blipblop.utils.BaseActor
import no.sandramoen.blipblop.utils.BaseGame
import kotlin.math.floor

class AdventureScreen : LevelScreen() {
    private var tag = "AdventureScreen"
    private var time = 0f
    private var isChallenge = false
    private var currentChallenge: BaseActor? = null
    private val challengeFrequency = 5f + 1 // 1 is offset, so we can see the top number

    private lateinit var foggyVeil: FoggyVeil
    private lateinit var multiBall: MultiBall
    private lateinit var challengeTextLabel: Label
    private lateinit var challengeCountdownLabel: Label

    override fun initialize() {
        super.initialize()
        tag = "AdventureScreen"

        // background
        background = Background(0f, 0f, background2DStage, "")

        // challenges
        foggyVeil = FoggyVeil(50f, 50f, foreground2DStage)
        multiBall = MultiBall(0f, 0f, foreground2DStage)

        // ui
        challengeTextLabel = Label("Challenge!", BaseGame.labelStyle)
        challengeTextLabel.setFontScale(.1f)
        challengeTextLabel.color = Color.GOLDENROD
        challengeTextLabel.setSize(100f, 10f)
        challengeTextLabel.setAlignment(Align.center)
        challengeTextLabel.setPosition(0f, 52.5f - challengeTextLabel.height / 2)
        foreground2DStage.addActor(challengeTextLabel)

        challengeCountdownLabel = Label("$challengeFrequency", BaseGame.labelStyle)
        challengeCountdownLabel.setFontScale(.1f)
        challengeCountdownLabel.color = Color.GOLDENROD
        challengeCountdownLabel.setSize(100f, 10f)
        challengeCountdownLabel.setAlignment(Align.center)
        challengeCountdownLabel.setPosition(0f, 47.5f - challengeCountdownLabel.height / 2)
        foreground2DStage.addActor(challengeCountdownLabel)
    }

    override fun update(dt: Float) {
        super.update(dt)
        if (ball.pause) return

        if (time <= challengeFrequency)
            time += dt

        // set label and trigger new challenge
        if (time > challengeFrequency && !isChallenge) {
            giveRandomChallenge()
        } else {
            val countDown = floor(challengeFrequency - time).toInt()
            if (countDown >= 0)
                challengeCountdownLabel.setText("$countDown")
        }

        // check if challenge is finished
        if (isChallenge && currentChallenge != null && currentChallenge!!.finished) {
            println("$tag: loop => resetChallenge()")
            resetChallenge()
        }

        // challenges
        if (currentChallenge == multiBall && multiBall.start && multiBall.shouldSpawn) {
            multiBall.shouldSpawn = false

            val ball0 = Ball(ball.getPosition().x, ball.getPosition().y, ball.getPosition().z, mainStage3D)
            ball0.setMotionAngle(ball.getMotionAngle() + 30f)
            ball0.setColor(Color.PINK) // TODO: remove?
            ball0.index = 1
            balls.add(ball0)

            val ball1 = Ball(ball.getPosition().x, ball.getPosition().y, ball.getPosition().z, mainStage3D)
            ball1.setMotionAngle(ball.getMotionAngle() - 30f)
            ball1.setColor(Color.CYAN) // TODO: remove?
            ball1.index = 2
            balls.add(ball1)
        }

        if (balls.size == 1 && currentChallenge == multiBall)
            multiBall.endChallenge()
    }

    override fun endGame() {
        super.endGame()
        println("endGame()")
        if (currentChallenge != null) {
            foggyVeil.endChallenge(0f)
            foggyVeil.isVisible = false
            currentChallenge = null
        }
        if (ball.pause) {
            challengeTextLabel.clearActions()
            challengeTextLabel.addAction(Actions.sequence(
                    Actions.fadeOut(.5f),
                    Actions.run { challengeTextLabel.setText("Challenge!") }
            ))
            challengeCountdownLabel.clearActions()
            challengeCountdownLabel.addAction(Actions.fadeOut(.5f))
        }
    }

    override fun restart() {
        println("$tag: restart()")
        super.restart()
        challengeTextLabel.addAction(Actions.fadeIn(.5f))
        challengeCountdownLabel.addAction(Actions.fadeIn(.5f))
        time = 0f
        isChallenge = false

        foggyVeil.remove()
        multiBall.remove()
        foggyVeil = FoggyVeil(50f, 50f, foreground2DStage)
        multiBall = MultiBall(0f, 0f, foreground2DStage)
        /*if (currentChallenge != null) {
            currentChallenge!!.finished // TODO: is this right?
            currentChallenge!!.pause = false
            currentChallenge!!.setAnimationPaused(pause = false)
            currentChallenge = null
        }
        resetChallenge()*/
    }

    private fun giveRandomChallenge() {
        isChallenge = true

        when (MathUtils.random(2, 2)) {
            1 -> {
                foggyVeil.startChallenge()
                currentChallenge = foggyVeil
            }
            2 -> {
                multiBall.startChallenge()
                currentChallenge = multiBall
            }
        }

        // labels
        challengeTextLabel.addAction(
                Actions.sequence(
                        Actions.fadeOut(.25f),
                        Actions.run { challengeTextLabel.setText(currentChallenge!!.title) },
                        Actions.delay(1f),
                        Actions.fadeIn(.5f),
                        Actions.delay(1f),
                        Actions.fadeOut(1f),
                        Actions.delay(1f),
                        Actions.run { challengeTextLabel.setText("Challenge!") }
                )
        )
        challengeCountdownLabel.addAction(Actions.fadeOut(.25f))
    }

    private fun resetChallenge() {
        println("$tag: resetChallenge()")
        isChallenge = false
        if (currentChallenge != null)
            currentChallenge!!.finished = false
        time = 0f
        challengeTextLabel.addAction(Actions.fadeIn(1f))
        challengeCountdownLabel.addAction(Actions.fadeIn(1f))

        // end all challenges
        /*foggyVeil.endChallenge(0f)
        multiBall.endChallenge(0f)
        if (balls.size > 1) { // multiball
            for (ball in balls) ball.remove()
            balls.clear()
            ball = Ball(0f, 0f, 0f, mainStage3D)
            balls.add(ball)
        }*/
        // println("$tag: resetChallenge(), currentChallenge!!.finished => ${currentChallenge!!.finished}")
    }
}
