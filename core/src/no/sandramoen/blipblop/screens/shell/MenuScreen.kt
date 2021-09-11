package no.sandramoen.blipblop.screens.shell

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input.Keys
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.Align
import no.sandramoen.blipblop.actors.Background
import no.sandramoen.blipblop.screens.gameplay.LevelScreen
import no.sandramoen.blipblop.ui.MadeByLabel
import no.sandramoen.blipblop.utils.BaseActor
import no.sandramoen.blipblop.utils.BaseGame
import no.sandramoen.blipblop.utils.BaseScreen
import no.sandramoen.blipblop.utils.GameUtils

class MenuScreen : BaseScreen() {
    private lateinit var tag: String
    private lateinit var title0: BaseActor
    private lateinit var madeByLabel: Label
    private lateinit var startButton: TextButton
    private lateinit var optionsButton: TextButton
    private lateinit var exitButton: TextButton

    override fun initialize() {
        tag = "MenuScreen.kt"

        // audio
        GameUtils.playAndLoopMusic(BaseGame.levelMusic)

        // background
        Background(0f, 0f, mainStage)

        // title
        title0 = BaseActor(10f, 0f, mainStage)
        title0.loadImage("title0")
        title0.setSize(Gdx.graphics.width.toFloat() * .16f, Gdx.graphics.height.toFloat() * .01f)
        val title0Padding = (100 - title0.width) / 2
        title0.setPosition(
            title0Padding,
            100 - title0.height - (title0Padding * Gdx.graphics.width / Gdx.graphics.height)
        )

        // menu
        startButton = TextButton("Start", BaseGame.textButtonStyle)
        startButton.addListener(object : ActorGestureListener() {
            override fun tap(event: InputEvent?, x: Float, y: Float, count: Int, button: Int) {
                BaseGame.clickSound!!.play(BaseGame.soundVolume)
                startButton.label.color = BaseGame.lightPink
                startTheGame()
            }
        })
        GameUtils.addTextButtonEnterExitEffect(startButton)

        optionsButton = TextButton("Options", BaseGame.textButtonStyle)
        optionsButton.addListener(object : ActorGestureListener() {
            override fun tap(event: InputEvent?, x: Float, y: Float, count: Int, button: Int) {
                BaseGame.clickSound!!.play(BaseGame.soundVolume)
                optionsButton.label.color = BaseGame.lightPink
                changeToOptionsScreen()
            }
        })
        GameUtils.addTextButtonEnterExitEffect(optionsButton)

        val buttonsTable = Table()
        buttonsTable.add(startButton).row()
        buttonsTable.add(optionsButton).row()

        // gui setup
        val table = Table()
        table.add(buttonsTable).fillY().expandY()
        table.row()
        table.add(MadeByLabel().label).padBottom(Gdx.graphics.height * .01f)
        table.setFillParent(true)
        uiTable.add(table).fill().expand()
        // table.debug = true

        // screen transition
    }

    override fun update(dt: Float) {}

    override fun keyDown(keycode: Int): Boolean {
        if (keycode == Keys.BACK || keycode == Keys.ESCAPE || keycode == Keys.BACKSPACE)
            exitGame()
        else if (keycode == Keys.ENTER)
            startTheGame()
        return false
    }

    private fun startTheGame() {
        // screen transition
        startButton.addAction(Actions.sequence(
            Actions.delay(.5f),
            Actions.run { BaseGame.setActiveScreen(LevelScreen()) }
        ))
    }

    private fun changeToOptionsScreen() {
        // screen transition
        optionsButton.addAction(Actions.sequence(
            Actions.delay(.5f),
            Actions.run { BaseGame.setActiveScreen(OptionsScreen()) }
        ))
    }

    private fun exitGame() {
        // screen transition
        // super.dispose()
        // Gdx.app.exit()
    }
}
