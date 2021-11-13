package no.sandramoen.blipblop.screens.shell

import com.badlogic.gdx.Application
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input.Keys
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener
import no.sandramoen.blipblop.actors.Background
import no.sandramoen.blipblop.ui.MadeByLabel
import no.sandramoen.blipblop.utils.BaseActor
import no.sandramoen.blipblop.utils.BaseGame
import no.sandramoen.blipblop.utils.BaseScreen
import no.sandramoen.blipblop.utils.GameUtils

class MenuScreen : BaseScreen() {
    private lateinit var tag: String
    private lateinit var title0: BaseActor
    private lateinit var madeByLabel: Label
    private lateinit var startClassicButton: TextButton
    private lateinit var startChallengeButton: TextButton
    private lateinit var optionsButton: TextButton
    private lateinit var exitButton: TextButton
    private lateinit var titleBlipLabel: Label
    private lateinit var titleBlopLabel: Label

    private lateinit var ball: BaseActor
    private var leftBallBounds = 0f
    private var rightBallBounds = 0f
    private var ballMovingLeft = true

    override fun initialize() {
        tag = "MenuScreen.kt"
        transition.fadeOut()

        // audio
        GameUtils.playAndLoopMusic(BaseGame.levelMusic)

        // background
        Background(0f, 0f, mainStage, colour = Vector3(.09f, .09f, .09f))

        // title
        val titleScale = 0.4f
        titleBlipLabel = Label("Blip", BaseGame.labelStyle)
        val titleBlipGroup = Group()
        titleBlipGroup.scaleBy(titleScale)
        titleBlipGroup.addActor(titleBlipLabel)

        titleBlopLabel = Label("Blop", BaseGame.labelStyle)
        val titleBlopGroup = Group()
        titleBlopGroup.scaleBy(titleScale)
        titleBlopGroup.addActor(titleBlopLabel)

        val labelHeight = titleBlipLabel.prefHeight * (1 + titleScale)

        val padding = Gdx.graphics.width * .025f
        val ballSpace = Gdx.graphics.width - padding - (titleBlipLabel.prefWidth * (1 + titleScale)) - (titleBlopLabel.prefWidth * (1 + titleScale))

        val titleTable = Table()
        titleTable.add(titleBlipGroup).width(titleBlipLabel.prefWidth * (1 + titleScale)).height(labelHeight).left().padLeft(padding).padTop(padding * BaseGame.RATIO)
        titleTable.add().width(ballSpace)// .height(labelHeight)
        titleTable.add(titleBlopGroup).width(titleBlopLabel.prefWidth * (1 + titleScale)).height(labelHeight).right().padRight(padding).padTop(padding * BaseGame.RATIO)
        // titleTable.debug = true

        // animated ball
        ball = BaseActor(0f, 0f, uiStage)
        ball.loadImage("whitePixel")
        ball.color = Color.GREEN
        val ballSize = Gdx.graphics.width * .03f
        ball.setSize(ballSize, Gdx.graphics.height * .03f * BaseGame.RATIO)
        ball.setPosition(titleBlipLabel.prefWidth * (1 + titleScale) + ballSize / 2, Gdx.graphics.height * .95f)

        leftBallBounds = titleBlipLabel.prefWidth * (1 + titleScale)
        rightBallBounds = leftBallBounds + ballSpace
        if (Gdx.app.type == Application.ApplicationType.Android) ball.setSpeed(Gdx.graphics.width * .1f)
        else ball.setSpeed(Gdx.graphics.width * .04f)
        ball.setMotionAngle(180f)

        // menu
        val buttonScale = .75f
        startClassicButton = TextButton("Classic", BaseGame.textButtonStyle)
        startClassicButton.label.setFontScale(buttonScale)
        startClassicButton.addListener(object : ActorGestureListener() {
            override fun tap(event: InputEvent?, x: Float, y: Float, count: Int, button: Int) {
                BaseGame.clickSound!!.play(BaseGame.soundVolume)
                startClassicButton.label.color = BaseGame.lightPink
                startClassicMode()
            }
        })
        GameUtils.addTextButtonEnterExitEffect(startClassicButton)

        startChallengeButton = TextButton("Challenge", BaseGame.textButtonStyle)
        startChallengeButton.label.setFontScale(buttonScale)
        startChallengeButton.addListener(object : ActorGestureListener() {
            override fun tap(event: InputEvent?, x: Float, y: Float, count: Int, button: Int) {
                BaseGame.clickSound!!.play(BaseGame.soundVolume)
                startChallengeButton.label.color = BaseGame.lightPink
                startChallengeMode()
            }
        })
        GameUtils.addTextButtonEnterExitEffect(startChallengeButton)

        optionsButton = TextButton("Options", BaseGame.textButtonStyle)
        optionsButton.label.setFontScale(buttonScale)
        optionsButton.addListener(object : ActorGestureListener() {
            override fun tap(event: InputEvent?, x: Float, y: Float, count: Int, button: Int) {
                BaseGame.clickSound!!.play(BaseGame.soundVolume)
                optionsButton.label.color = BaseGame.lightPink
                changeToOptionsScreen()
            }
        })
        GameUtils.addTextButtonEnterExitEffect(optionsButton)

        val buttonsTable = Table()
        buttonsTable.add(startClassicButton).row()
        buttonsTable.add(startChallengeButton).row()
        buttonsTable.add(optionsButton).row()

        // gui setup
        val table = Table()
        table.add(titleTable).width(Gdx.graphics.width.toFloat()).row()//.fillX().expandX().row()
        table.add(buttonsTable).fillY().expandY()
        table.row()
        table.add(MadeByLabel().label).padBottom(Gdx.graphics.height * .01f)
        table.setFillParent(true)
        uiTable.add(table).fill().expand()
        // table.debug = true

        // screen transition
    }

    override fun update(dt: Float) {
        ball.applyPhysics(dt)
        if (ball.x >= rightBallBounds - ball.width / 2) {
            ball.setMotionAngle(180f)
            titleBlopLabel.addAction(Actions.sequence(
                    Actions.color(Color(.7f, .7f, 1f, 1f), .1f, Interpolation.circleOut),
                    Actions.color(Color.WHITE, .1f, Interpolation.circleIn)
            ))
        } else if (ball.x <= leftBallBounds + Gdx.graphics.width * .005f) {
            ball.setMotionAngle(0f)
            titleBlipLabel.addAction(Actions.sequence(
                    Actions.color(Color(1f, .7f, .7f, 1f), .1f, Interpolation.circleOut),
                    Actions.color(Color.WHITE, .1f, Interpolation.circleIn)
            ))
        }
    }

    override fun keyDown(keycode: Int): Boolean {
        if (keycode == Keys.BACK || keycode == Keys.ESCAPE || keycode == Keys.BACKSPACE)
            exitGame()
        else if (keycode == Keys.ENTER)
            startClassicMode()
        return false
    }

    private fun startClassicMode() {
        // screen transition
        startClassicButton.addAction(Actions.sequence(
                Actions.delay(.5f),
                Actions.run { transition.fadeInToClassicScreen() }
        ))
    }

    private fun startChallengeMode() {
        // screen transition
        startClassicButton.addAction(Actions.sequence(
                Actions.delay(.5f),
                Actions.run { transition.fadeInToChallengeScreen() }
        ))
    }

    private fun changeToOptionsScreen() {
        // screen transition
        optionsButton.addAction(Actions.sequence(
                Actions.delay(.5f),
                Actions.run { transition.fadeInToOptionsScreen() }
        ))
    }

    private fun exitGame() {
        // screen transition
        super.dispose()
        Gdx.app.exit()
    }
}
