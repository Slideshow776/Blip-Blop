package no.sandramoen.blipblop.screens.gameplay

import com.badlogic.gdx.Application
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import no.sandramoen.blipblop.actors.challenges.*
import no.sandramoen.blipblop.utils.BaseActor
import no.sandramoen.blipblop.utils.BaseGame
import kotlin.math.floor

class ChallengeScreen : LevelScreen() {
    private var tag = "ChallengeScreen"
    private var time = 0f
    private var isChallenge = false
    private var currentChallenge: BaseActor? = null
    private val challengeFrequency = 5f + 1 // 1 is offset, so we can see the top number
    private var countDown: Int = 0
    private var previousCount: Int = 0

    private lateinit var foggyVeil: FoggyVeil
    private lateinit var multiBall: MultiBall
    private lateinit var longPlayer: LongPlayer
    private lateinit var blackHole: BlackHole
    private lateinit var portals: Portals
    private lateinit var rectangles: Rectangles
    private lateinit var bubbles: Bubbles
    private lateinit var daggers: Daggers
    private lateinit var spinny: Spinny
    private lateinit var challengeTextLabel: Label
    private lateinit var challengeCountdownLabel: Label

    override fun initialize() {
        super.initialize()
        tag = "ChallengeScreen"
        startAchievements(false)

        // challenges
        foggyVeil = FoggyVeil(50f, 50f, foreground2DStage)
        multiBall = MultiBall(0f, 0f, foreground2DStage, balls, mainStage3D)
        longPlayer = LongPlayer(0f, 0f, foreground2DStage, players)
        blackHole = BlackHole(0f, 0f, foreground2DStage, balls, mainStage3D)
        portals = Portals(0f, 0f, foreground2DStage, balls, mainStage3D)
        rectangles = Rectangles(0f, 0f, foreground2DStage, balls, mainStage3D)
        bubbles = Bubbles(0f, 0f, foreground2DStage, balls, players, mainStage3D)
        daggers = Daggers(0f, 0f, foreground2DStage, balls, mainStage3D)
        spinny = Spinny(0f, 0f, foreground2DStage, balls, players, mainStage3D)

        // ui
        challengeTextLabel = Label("Challenge!", BaseGame.labelStyle)
        challengeTextLabel.color = Color.GOLDENROD
        if (Gdx.app.type == Application.ApplicationType.Android) {
            challengeTextLabel.setFontScale(.055f)

            val challengeTextGroup = Group()
            challengeTextGroup.isTransform = true
            challengeTextGroup.rotateBy(-90f)
            challengeTextGroup.addActor(challengeTextLabel)
            challengeTextGroup.setOrigin(Align.center)
            challengeTextGroup.setPosition(20f, 70f)

            foreground2DStage.addActor(challengeTextGroup)
        } else if (Gdx.app.type == Application.ApplicationType.Desktop) {
            challengeTextLabel.setFontScale(.1f)
            challengeTextLabel.setSize(100f, 10f)
            challengeTextLabel.setAlignment(Align.center)
            challengeTextLabel.setPosition(0f, 52.5f - challengeTextLabel.height / 2)
            foreground2DStage.addActor(challengeTextLabel)
        }

        challengeCountdownLabel = Label("$challengeFrequency", BaseGame.labelStyle)
        challengeCountdownLabel.color = Color.GOLDENROD
        if (Gdx.app.type == Application.ApplicationType.Android) {

            challengeCountdownLabel.setFontScale(.055f)

            val challengeCountdownGroup = Group()
            challengeCountdownGroup.isTransform = true
            challengeCountdownGroup.rotateBy(-90f)
            challengeCountdownGroup.addActor(challengeCountdownLabel)
            challengeCountdownGroup.setOrigin(Align.center)
            challengeCountdownGroup.setPosition(10f, 52f)

            foreground2DStage.addActor(challengeCountdownGroup)
        } else if (Gdx.app.type == Application.ApplicationType.Desktop) {
            challengeCountdownLabel.setFontScale(.1f)
            challengeCountdownLabel.setPosition(0f, 42f)
            challengeCountdownLabel.setSize(100f, 10f)
            challengeCountdownLabel.setAlignment(Align.center)
            foreground2DStage.addActor(challengeCountdownLabel)
        }
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
            countDown = floor(challengeFrequency - time).toInt()
            if (countDown >= 0) challengeCountdownLabel.setText("$countDown")
            if (previousCount != countDown && countDown >= 0) {
                previousCount = countDown
                when (countDown) {
                    0 -> BaseGame.countDown1Sound!!.play(BaseGame.soundVolume)
                    in 1..3 -> BaseGame.countDown0Sound!!.play(BaseGame.soundVolume)
                }
            }
        }

        // check if challenge is finished
        if (isChallenge && currentChallenge != null && currentChallenge!!.finished) {
            resetChallenge()
        }
    }

    override fun gameOver() {
        super.gameOver()
        endChallenges()
    }

    override fun endGame() {
        super.endGame()
        endChallenges()
    }

    override fun restart() {
        super.restart()
        challengeTextLabel.addAction(Actions.fadeIn(.5f))
        challengeCountdownLabel.addAction(Actions.fadeIn(.5f))
        time = 0f
        isChallenge = false

        foggyVeil.remove()
        foggyVeil.endChallenge(0f)
        multiBall.remove()
        longPlayer.remove()
        blackHole.endChallenge(0f)
        blackHole.remove()
        portals.endChallenge(0f)
        portals.remove()
        rectangles.endChallenge(0f)
        rectangles.remove()
        bubbles.endChallenge(0f)
        bubbles.remove()
        daggers.endChallenge(0f)
        daggers.remove()
        spinny.endChallenge(0f)
        spinny.remove()
        foggyVeil = FoggyVeil(50f, 50f, foreground2DStage)
        multiBall = MultiBall(0f, 0f, foreground2DStage, balls, mainStage3D)
        longPlayer = LongPlayer(0f, 0f, foreground2DStage, players)
        blackHole = BlackHole(0f, 0f, foreground2DStage, balls, mainStage3D)
        portals = Portals(0f, 0f, foreground2DStage, balls, mainStage3D)
        rectangles = Rectangles(0f, 0f, foreground2DStage, balls, mainStage3D)
        bubbles = Bubbles(0f, 0f, foreground2DStage, balls, players, mainStage3D)
        daggers = Daggers(0f, 0f, foreground2DStage, balls, mainStage3D)
        spinny = Spinny(0f, 0f, foreground2DStage, balls, players, mainStage3D)
    }

    private fun giveRandomChallenge() {
        isChallenge = true

        when (MathUtils.random(1, 9)) {
            1 -> {
                foggyVeil.startChallenge()
                currentChallenge = foggyVeil
            }
            2 -> {
                multiBall.startChallenge()
                currentChallenge = multiBall
            }
            3 -> {
                longPlayer.startChallenge()
                currentChallenge = longPlayer
            }
            4 -> {
                blackHole.startChallenge()
                currentChallenge = blackHole
            }
            5 -> {
                portals.startChallenge()
                currentChallenge = portals
            }
            6 -> {
                rectangles.startChallenge()
                currentChallenge = rectangles
            }
            7 -> {
                bubbles.startChallenge()
                currentChallenge = bubbles
            }
            8 -> {
                daggers.startChallenge()
                currentChallenge = daggers
            }
            9 -> {
                spinny.startChallenge()
                currentChallenge = spinny
            }
        }

        // labels
        challengeTextLabel.addAction(
                Actions.sequence(
                        Actions.fadeOut(.25f),
                        Actions.run { if (currentChallenge != null) challengeTextLabel.setText(currentChallenge!!.title) },
                        Actions.delay(1f),
                        Actions.run { BaseGame.startChallengeSound!!.play(BaseGame.soundVolume) },
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
        isChallenge = false
        if (currentChallenge != null)
            currentChallenge!!.finished = false
        time = 0f
        challengeTextLabel.addAction(Actions.fadeIn(1f))
        challengeCountdownLabel.addAction(Actions.fadeIn(1f))
    }

    private fun endChallenges() {
        if (currentChallenge != null) {
            foggyVeil.endChallenge(0f)
            foggyVeil.isVisible = false
            blackHole.endChallenge(0f)
            portals.endChallenge(0f)
            rectangles.endChallenge(0f)
            bubbles.endChallenge(0f)
            daggers.endChallenge(0f)
            spinny.endChallenge(0f)
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
}
