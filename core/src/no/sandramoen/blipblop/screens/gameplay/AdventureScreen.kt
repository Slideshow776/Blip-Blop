package no.sandramoen.blipblop.screens.gameplay

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import no.sandramoen.blipblop.actors.Background
import no.sandramoen.blipblop.actors.challenges.VeilChallenge
import no.sandramoen.blipblop.utils.BaseActor
import no.sandramoen.blipblop.utils.BaseGame
import kotlin.math.floor

class AdventureScreen : LevelScreen() {
    private var tag = "AdventureScreen"
    private var time = 0f
    private var isChallenge = false
    private val challengeFrequency = 6f

    // private lateinit var challenges: ArrayList<BaseActor>
    private var currentChallenge: BaseActor? = null
    private lateinit var veilChallenge: VeilChallenge

    private lateinit var challengeTextLabel: Label
    private lateinit var challengeCountdownLabel: Label

    override fun initialize() {
        super.initialize()
        tag = "AdventureScreen"

        // background
        background = Background(0f, 0f, background2DStage, "")

        // middle effect
        veilChallenge = VeilChallenge(50f, 50f, foreground2DStage)

        // challenges = ArrayList()
        // challenges.add(veilChallenge)

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

        if (isChallenge && currentChallenge!!.finished) resetChallenge()

        // check if challenge is finished
        /*for (challenge in challenges) {
            if (challenge.finished && isChallenge) {
                reset(challenge)
            }
        }*/

        println(currentChallenge)
    }

    override fun exitGame() {
        super.exitGame()
        if (ball.pause) {
            challengeTextLabel.addAction(Actions.fadeOut(.5f))
            challengeCountdownLabel.addAction(Actions.fadeOut(.5f))
        }
    }

    override fun restart() {
        super.restart()
        challengeTextLabel.addAction(Actions.fadeIn(.5f))
        challengeCountdownLabel.addAction(Actions.fadeIn(.5f))
        resetChallenge()
    }

    private fun giveRandomChallenge() {
        isChallenge = true
        challengeTextLabel.addAction(Actions.fadeOut(1f))
        challengeCountdownLabel.addAction(Actions.fadeOut(1f))

        if (MathUtils.random(1, 1) == 1) {
            veilChallenge.startChallenge()
            currentChallenge = veilChallenge
        }
    }

    private fun resetChallenge() {
        isChallenge = false
        currentChallenge!!.finished = false
        time = 0f
        challengeTextLabel.addAction(Actions.fadeIn(1f))
        challengeCountdownLabel.addAction(Actions.fadeIn(1f))
    }
}
