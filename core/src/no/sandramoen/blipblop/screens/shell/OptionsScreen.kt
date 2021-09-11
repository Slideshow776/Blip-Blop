package no.sandramoen.blipblop.screens.shell

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.Scaling
import no.sandramoen.blipblop.actors.Background
import no.sandramoen.blipblop.screens.gameplay.LevelScreen
import no.sandramoen.blipblop.ui.MadeByLabel
import no.sandramoen.blipblop.utils.BaseGame
import no.sandramoen.blipblop.utils.BaseScreen
import no.sandramoen.blipblop.utils.GameUtils

class OptionsScreen : BaseScreen() {
    private lateinit var tag: String

    override fun initialize() {
        tag = "MenuScreen.kt"

        // background
        Background(0f, 0f, mainStage)

        // main label
        val mainLabel = Label("Options", BaseGame.labelStyle)
        mainLabel.setFontScale(1.5f)

        val optionsWidgetWidth =
            Gdx.graphics.width * .6f // value must be pre-determined for scaling
        val optionsWidgetHeight =
            Gdx.graphics.height * .015f // value must be pre-determined for scaling
        val optionsSliderScale =
            Gdx.graphics.height * .002f // makes sure scale is device adjustable-ish

        // music
        val musicLabel = Label("Music", BaseGame.labelStyle)
        musicLabel.setFontScale(.5f)
        musicLabel.addListener(object : ClickListener() {
            override fun enter(event: InputEvent?, x: Float, y: Float, pointer: Int, fromActor: Actor?) {
                musicLabel.color = BaseGame.lightPink
                super.enter(event, x, y, pointer, fromActor)
            }
            override fun exit(event: InputEvent?, x: Float, y: Float, pointer: Int, toActor: Actor?) {
                musicLabel.color = Color.WHITE
                super.exit(event, x, y, pointer, toActor)
            }
        })

        val optionsMusicSlider = Slider(0f, 1f, .1f, false, BaseGame.skin)
        optionsMusicSlider.value = BaseGame.musicVolume
        optionsMusicSlider.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                BaseGame.musicVolume = optionsMusicSlider.value
                GameUtils.setMusicVolume()
                GameUtils.saveGameState()
            }
        })
        val optionsMusicSliderContainer = Container(optionsMusicSlider)
        optionsMusicSliderContainer.isTransform = true
        optionsMusicSliderContainer.setOrigin(
            (optionsWidgetWidth * 5 / 6) / 2,
            optionsWidgetHeight / 2
        )
        optionsMusicSliderContainer.setScale(optionsSliderScale)
        optionsMusicSliderContainer.addListener(object : ClickListener() {
            override fun enter(event: InputEvent?, x: Float, y: Float, pointer: Int, fromActor: Actor?) {
                musicLabel.color = BaseGame.lightPink
                super.enter(event, x, y, pointer, fromActor)
            }
            override fun exit(event: InputEvent?, x: Float, y: Float, pointer: Int, toActor: Actor?) {
                musicLabel.color = Color.WHITE
                super.exit(event, x, y, pointer, toActor)
            }
        })

        // sound
        val soundLabel = Label("Sound", BaseGame.labelStyle)
        soundLabel.setFontScale(.5f)
        soundLabel.addListener(object : ClickListener() {
            override fun enter(event: InputEvent?, x: Float, y: Float, pointer: Int, fromActor: Actor?) {
                soundLabel.color = BaseGame.lightPink
                super.enter(event, x, y, pointer, fromActor)
            }

            override fun exit(event: InputEvent?, x: Float, y: Float, pointer: Int, toActor: Actor?) {
                soundLabel.color = Color.WHITE
                super.exit(event, x, y, pointer, toActor)
            }
        })

        val optionsSoundSlider = Slider(0f, 1f, .1f, false, BaseGame.skin)
        optionsSoundSlider.value = BaseGame.soundVolume
        optionsSoundSlider.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                BaseGame.soundVolume = optionsSoundSlider.value
                BaseGame.clickSound!!.play(BaseGame.soundVolume)
                GameUtils.saveGameState()
            }
        })
        val optionsSoundSliderContainer = Container(optionsSoundSlider)
        optionsSoundSliderContainer.isTransform = true
        optionsSoundSliderContainer.setOrigin(
            (optionsWidgetWidth * 5 / 6) / 2,
            optionsWidgetHeight / 2
        )
        optionsSoundSliderContainer.setScale(optionsSliderScale)
        optionsSoundSliderContainer.addListener(object : ClickListener() {
            override fun enter( event: InputEvent?, x: Float, y: Float, pointer: Int, fromActor: Actor?) {
                soundLabel.color = BaseGame.lightPink
                super.enter(event, x, y, pointer, fromActor)
            }
            override fun exit(event: InputEvent?, x: Float, y: Float, pointer: Int, toActor: Actor?) {
                soundLabel.color = Color.WHITE
                super.exit(event, x, y, pointer, toActor)
            }
        })

        // google play services
        val optionsUseGPSCheckBox = CheckBox("Google Play", BaseGame.skin)
        // optionsUseGPSCheckBox.isDisabled = true
        /*optionsUseGPSCheckBox.isChecked = !BaseGame.disableGPS
        optionsUseGPSCheckBox.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                BaseGame.disableGPS = !BaseGame.disableGPS
                BaseGame.clickSound!!.play(BaseGame.soundVolume)
                GameUtils.saveGameState()

                if (!BaseGame.disableGPS) {
                    BaseGame.gps!!.signIn()
                    BaseGame.gps!!.submitScore(BaseGame.highScore)
                    optionsShowScore.touchable = Touchable.enabled
                    optionsShowScore.color.a = 1f
                } else {
                    BaseGame.gps!!.signOut()
                    optionsShowScore.touchable = Touchable.disabled
                    optionsShowScore.color.a = .75f
                }
            }
        })*/
        optionsUseGPSCheckBox.isTransform = true
        optionsUseGPSCheckBox.image.setScaling(Scaling.fill)
        optionsUseGPSCheckBox.imageCell.size(optionsWidgetWidth * .1f)
        optionsUseGPSCheckBox.imageCell.padRight(Gdx.graphics.width * .02f)
        optionsUseGPSCheckBox.label.setFontScale(.75f)
        optionsUseGPSCheckBox.setOrigin(optionsWidgetWidth / 2, optionsWidgetHeight / 2)
        optionsUseGPSCheckBox.addListener(object : ClickListener() {
            override fun enter(event: InputEvent?, x: Float, y: Float, pointer: Int, fromActor: Actor?) {
                optionsUseGPSCheckBox.label.color = BaseGame.lightPink
                super.enter(event, x, y, pointer, fromActor)
            }
            override fun exit(event: InputEvent?, x: Float, y: Float, pointer: Int, toActor: Actor?) {
                optionsUseGPSCheckBox.label.color = Color.WHITE
                super.exit(event, x, y, pointer, toActor)
            }
        })

        // back
        val backButton = TextButton("Back", BaseGame.textButtonStyle)
        backButton.addListener(object : ActorGestureListener() {
            override fun tap(event: InputEvent?, x: Float, y: Float, count: Int, button: Int) {
                BaseGame.clickSound!!.play(BaseGame.soundVolume)
                backButton.label.color = BaseGame.lightPink
                backButton.addAction(Actions.sequence(
                    Actions.delay(.5f),
                    Actions.run { BaseGame.setActiveScreen(MenuScreen()) }
                ))
            }
        })
        GameUtils.addTextButtonEnterExitEffect(backButton)

        val buttonsTable = Table()
        buttonsTable.add(optionsSoundSliderContainer).width(optionsWidgetWidth * 5 / 6)
            .height(optionsWidgetHeight)
        buttonsTable.add(soundLabel).width(optionsWidgetWidth * 1 / 6)
            .padLeft(Gdx.graphics.width * .11f).row()
        buttonsTable.add(Label("", BaseGame.labelStyle)).row()
        buttonsTable.add(optionsMusicSliderContainer).width(optionsWidgetWidth * 5 / 6)
            .height(optionsWidgetHeight)
        buttonsTable.add(musicLabel).width(optionsWidgetWidth * 1 / 6)
            .padLeft(Gdx.graphics.width * .11f).row()
        buttonsTable.add(Label("", BaseGame.labelStyle)).row()
        buttonsTable.add(optionsUseGPSCheckBox).colspan(2).row()
        buttonsTable.add(Label("", BaseGame.labelStyle)).row()
        buttonsTable.add(backButton).colspan(2)
        buttonsTable.debug = false

        // gui setup
        val table = Table()
        table.add(mainLabel).padTop(Gdx.graphics.height * .015f)
        table.row()
        table.add(buttonsTable).fillY().expandY()
        table.row()
        table.add(MadeByLabel().label).padBottom(Gdx.graphics.height * .01f)
        table.setFillParent(true)
        uiTable.add(table).fill().expand()
        // table.debug = true

        // screen transition
    }

    override fun update(dt: Float) {}
}