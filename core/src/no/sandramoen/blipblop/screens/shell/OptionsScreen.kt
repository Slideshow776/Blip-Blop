package no.sandramoen.blipblop.screens.shell

import com.badlogic.gdx.Application
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import no.sandramoen.blipblop.actors.Background
import no.sandramoen.blipblop.ui.MadeByLabel
import no.sandramoen.blipblop.utils.BaseGame
import no.sandramoen.blipblop.utils.BaseScreen
import no.sandramoen.blipblop.utils.GameUtils

class OptionsScreen : BaseScreen() {
    private lateinit var tag: String
    private lateinit var achievementButton: TextButton
    private lateinit var achievementImage: Image
    private lateinit var onImage: Image
    private lateinit var offImage: Image
    private lateinit var toggleGPS: Button

    private var attemptedToSignIn = false
    private lateinit var up: TextureRegion
    private lateinit var down: TextureRegion

    override fun initialize() {
        tag = "OptionsScreen.kt"
        transition.fadeOut()

        // background ---------------------------------------------------------------------------------------------
        Background(0f, 0f, mainStage, colour = Vector3(.09f, .09f, .09f))

        // main label ---------------------------------------------------------------------------------------------
        val mainLabel = Label("Options", BaseGame.labelStyle)
        mainLabel.setFontScale(1.5f)

        val optionsWidgetWidth =
                Gdx.graphics.width * .6f // value must be pre-determined for scaling
        val optionsWidgetHeight =
                Gdx.graphics.height * .015f // value must be pre-determined for scaling
        val optionsSliderScale =
                Gdx.graphics.height * .002f // makes sure scale is device adjustable-ish

        // music -------------------------------------------------------------------------------------------------
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

        // sound -------------------------------------------------------------------------------------------------
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
            override fun enter(event: InputEvent?, x: Float, y: Float, pointer: Int, fromActor: Actor?) {
                soundLabel.color = BaseGame.lightPink
                super.enter(event, x, y, pointer, fromActor)
            }

            override fun exit(event: InputEvent?, x: Float, y: Float, pointer: Int, toActor: Actor?) {
                soundLabel.color = Color.WHITE
                super.exit(event, x, y, pointer, toActor)
            }
        })

        // display achievements ----------------------------------------------------------------------------------------
        achievementButton = TextButton("Achievements", BaseGame.textButtonStyle)
        achievementButton.label.setFontScale(.5f)
        achievementButton.addListener(object : ActorGestureListener() {
            override fun tap(event: InputEvent?, x: Float, y: Float, count: Int, button: Int) {
                BaseGame.clickSound!!.play(BaseGame.soundVolume)
                achievementButton.label.color = BaseGame.lightPink
                if (BaseGame.gps != null && BaseGame.gps!!.isSignedIn())
                    BaseGame.gps!!.showAchievements()
            }
        })
        GameUtils.addTextButtonEnterExitEffect(achievementButton)

        achievementImage = Image(BaseGame.textureAtlas!!.findRegion("achievements-google-play-achievements-icon"))
        if (BaseGame.gps != null && BaseGame.gps!!.isSignedIn()) {
            achievementButton.touchable = Touchable.enabled
        } else {
            achievementImage.color = Color.DARK_GRAY
            achievementButton.touchable = Touchable.disabled
            achievementButton.label.color = Color.DARK_GRAY
        }

        val achievementTable = Table()
        achievementTable.add(achievementButton)
        achievementTable.add(achievementImage).width(Gdx.graphics.width * .06f).height(Gdx.graphics.height * .045f)

        // google play services --------------------------------------------------------------------------------------
        val gpsLabel = Label("Google Play Services", BaseGame.labelStyle)
        gpsLabel.setFontScale(.5f)

        onImage = Image(BaseGame.textureAtlas!!.findRegion("gpsOn"))
        offImage = Image(BaseGame.textureAtlas!!.findRegion("gpsOff"))

        up = BaseGame.textureAtlas!!.findRegion("on")
        down = BaseGame.textureAtlas!!.findRegion("off")
        val buttonStyle = Button.ButtonStyle()
        buttonStyle.up = TextureRegionDrawable(up)
        buttonStyle.checked = TextureRegionDrawable(down)
        toggleGPS = Button(buttonStyle)
        toggleGPS.isChecked = BaseGame.gps != null && !BaseGame.gps!!.isSignedIn()
        toggleGPS.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                BaseGame.clickSound!!.play(BaseGame.soundVolume)
                if (!BaseGame.isGPS) {
                    BaseGame.gps!!.signIn()
                    attemptedToSignIn = true
                } else {
                    BaseGame.isGPS = false
                    BaseGame.gps!!.signOut()
                    achievementButton.touchable = Touchable.disabled
                    achievementButton.label.color = Color.DARK_GRAY
                    achievementImage.color = Color.DARK_GRAY
                    GameUtils.saveGameState()
                    setToggleButtonColors(toggleGPS, onImage, offImage)
                    attemptedToSignIn = false
                }

                toggleGPS.style.up = TextureRegionDrawable(down)
                toggleGPS.style.checked = TextureRegionDrawable(down)
            }
        })
        setToggleButtonColors(toggleGPS, onImage, offImage)

        val gpsTable = Table()
        gpsTable.add(gpsLabel).colspan(3).padBottom(Gdx.graphics.height * .03f).row()
        gpsTable.add(offImage).width(Gdx.graphics.width * .1f).height(Gdx.graphics.height * .045f).right()
        gpsTable.add(toggleGPS).width(Gdx.graphics.width * .15f).height(Gdx.graphics.height * .037f)
        gpsTable.add(onImage).width(Gdx.graphics.width * .1f).height(Gdx.graphics.height * .045f).left()
        // gpsTable.debug = true

        // back button -------------------------------------------------------------------------------------------------
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

        val buttonsTable = Table() // -----------------------------------------------------------------------------------
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

        if (Gdx.app.type == Application.ApplicationType.Android) {
            buttonsTable.add(gpsTable).colspan(2).row()
            buttonsTable.add(achievementTable).padTop(Gdx.graphics.height * .03f).colspan(2).row()
        }

        buttonsTable.add(Label("", BaseGame.labelStyle)).row()
        buttonsTable.add(backButton).colspan(2)
        buttonsTable.debug = false

        // gui setup -------------------------------------------------------------------------------------------------
        val table = Table()
        table.add(mainLabel).padTop(Gdx.graphics.height * .015f)
        table.row()
        table.add(buttonsTable).fillY().expandY()
        table.row()
        table.add(MadeByLabel().label).padBottom(Gdx.graphics.height * .01f)
        table.setFillParent(true)
        // table.debug = true
        uiTable.add(table).fill().expand()
    }

    override fun update(dt: Float) {
        if (attemptedToSignIn && BaseGame.gps!!.isSignedIn()) {
            BaseGame.isGPS = true

            achievementButton.touchable = Touchable.enabled
            achievementButton.label.color = Color.WHITE
            achievementImage.color = Color.WHITE

            GameUtils.saveGameState()
            setToggleButtonColors(toggleGPS, onImage, offImage)
            toggleGPS.style.up = TextureRegionDrawable(up)
            toggleGPS.style.checked = TextureRegionDrawable(up)
            attemptedToSignIn = false
        }
    }

    override fun keyDown(keycode: Int): Boolean {
        if (keycode == Input.Keys.BACK || keycode == Input.Keys.ESCAPE || keycode == Input.Keys.BACKSPACE)
            BaseGame.setActiveScreen(MenuScreen())
        return false
    }

    private fun setToggleButtonColors(toggleButton: Button, onImage: Image, offImage: Image) {
        if (!toggleButton.isChecked) {
            onImage.color = Color.WHITE
            offImage.color = Color.DARK_GRAY
        } else {
            onImage.color = Color.DARK_GRAY
            offImage.color = Color.WHITE
        }
    }
}