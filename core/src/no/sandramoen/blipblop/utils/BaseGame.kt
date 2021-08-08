package no.sandramoen.blipblop.utils

import com.badlogic.gdx.*
import com.badlogic.gdx.assets.AssetDescriptor
import com.badlogic.gdx.assets.AssetErrorListener
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture.TextureFilter
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.NinePatch
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable
import kotlin.system.measureTimeMillis

abstract class BaseGame() : Game(), AssetErrorListener {
    private val tag = "BaseGame.kt"

    init {
        game = this
    }

    companion object {
        private var game: BaseGame? = null

        lateinit var assetManager: AssetManager
        lateinit var fontGenerator: FreeTypeFontGenerator
        const val WORLD_WIDTH = 100f
        const val WORLD_HEIGHT = 100f
        const val scale = 1.5f
        var RATIO = 0f

        // game assets
        var labelStyle: LabelStyle? = null
        var textButtonStyle: TextButtonStyle? = null
        var textureAtlas: TextureAtlas? = null
        var skin: Skin? = null
        var defaultShader: String? = null
        var shockwaveShader: String? = null
        var levelMusic1: Music? = null
        var blipSound: Sound? = null
        var blopSound: Sound? = null
        var startSound: Sound? = null
        var deflectSound: Sound? = null
        var vibrations: Boolean = false
        var green = Color(0.113f, 0.968f, 0.282f, 1f)
        var yellow = Color(0.968f, 0.815f, 0.113f, 1f)
        var red = Color(0.968f, 0.113f, 0.113f, 1f)

        // game state
        var prefs: Preferences? = null
        var loadPersonalParameters = false
        var highScore: Int = 0
        var mysteryKinksterScore: Int = 250_000
        var soundVolume = .75f
        var musicVolume = .125f

        fun setActiveScreen(s: BaseScreen) {
            game?.setScreen(s)
        }

        fun setActiveScreen(s: BaseScreen3D) {
            game?.setScreen(s)
        }
    }

    override fun create() {
        Gdx.input.inputProcessor = InputMultiplexer() // discrete input

        // global variables
        RATIO = Gdx.graphics.width.toFloat() / Gdx.graphics.height

        // GameUtils.loadGameState()
        if (!loadPersonalParameters) {
            soundVolume = .75f
            musicVolume = .25f
            vibrations = true
        }

        // asset manager
        val time = measureTimeMillis {
            assetManager = AssetManager()
            assetManager.setErrorListener(this)
            assetManager.load("images/included/packed/blipBlop.pack.atlas", TextureAtlas::class.java)
            // assetManager.load("audio/music/AlexBeroza_-_Drive.mp3", Music::class.java)
            assetManager.load("audio/sound/blip.wav", Sound::class.java)
            assetManager.load("audio/sound/blop.wav", Sound::class.java)
            assetManager.load("audio/sound/start.wav", Sound::class.java)
            assetManager.load("audio/sound/deflect.wav", Sound::class.java)

            // assetManager.load("skins/default/uiskin.json", Skin::class.java)

            val resolver = InternalFileHandleResolver()
            assetManager.setLoader(FreeTypeFontGenerator::class.java, FreeTypeFontGeneratorLoader(resolver))
            assetManager.setLoader(BitmapFont::class.java, ".ttf", FreetypeFontLoader(resolver))
            assetManager.setLoader(Text::class.java, TextLoader(InternalFileHandleResolver()))

            assetManager.load(AssetDescriptor("shaders/default.vs", Text::class.java, TextLoader.TextParameter()))
            assetManager.load(AssetDescriptor("shaders/shockwave.fs", Text::class.java, TextLoader.TextParameter()))
            assetManager.finishLoading()

            textureAtlas = assetManager.get("images/included/packed/blipBlop.pack.atlas") // all images are found in this global static variable

            // audio
            // levelMusic1 = assetManager.get("audio/music/AlexBeroza_-_Drive.mp3", Music::class.java)
            blipSound = assetManager.get("audio/sound/blip.wav", Sound::class.java)
            blopSound = assetManager.get("audio/sound/blop.wav", Sound::class.java)
            startSound = assetManager.get("audio/sound/start.wav", Sound::class.java)
            deflectSound = assetManager.get("audio/sound/deflect.wav", Sound::class.java)

            // text files
            defaultShader = assetManager.get("shaders/default.vs", Text::class.java).getString()
            shockwaveShader = assetManager.get("shaders/shockwave.fs", Text::class.java).getString()

            // skins
            skin = Skin(Gdx.files.internal("skins/default/uiskin.json"))

            // fonts
            FreeTypeFontGenerator.setMaxTextureSize(2048) // solves font bug that won't show some characters like "." and "," in android
            val fontGenerator = FreeTypeFontGenerator(Gdx.files.internal("fonts/arcade.ttf"))
            val fontParameters = FreeTypeFontParameter()
            fontParameters.size = (.038f * Gdx.graphics.height).toInt() // Font size is based on width of screen...
            fontParameters.color = Color.WHITE
            fontParameters.borderWidth = 2f
            fontParameters.borderColor = Color.BLACK
            fontParameters.borderStraight = true
            fontParameters.minFilter = TextureFilter.Linear
            fontParameters.magFilter = TextureFilter.Linear
            val customFont = fontGenerator.generateFont(fontParameters)

            val buttonFontParameters = FreeTypeFontParameter()
            buttonFontParameters.size =
                    (.04f * Gdx.graphics.height).toInt() // If the resolutions height is 1440 then the font size becomes 86
            buttonFontParameters.color = Color.WHITE
            buttonFontParameters.borderWidth = 2f
            buttonFontParameters.borderColor = Color.BLACK
            buttonFontParameters.borderStraight = true
            buttonFontParameters.minFilter = TextureFilter.Linear
            buttonFontParameters.magFilter = TextureFilter.Linear
            val buttonCustomFont = fontGenerator.generateFont(buttonFontParameters)

            labelStyle = LabelStyle()
            labelStyle!!.font = customFont

            textButtonStyle = TextButtonStyle()
            val buttonTexUp = textureAtlas!!.findRegion("button")
            val buttonTexDown = textureAtlas!!.findRegion("button-pressed")
            val buttonPatchUp = NinePatch(buttonTexUp, 24, 24, 24, 24)
            val buttonPatchDown = NinePatch(buttonTexDown, 24, 24, 24, 24)
            textButtonStyle!!.up = NinePatchDrawable(buttonPatchUp)
            textButtonStyle!!.down = NinePatchDrawable(buttonPatchDown)
            textButtonStyle!!.font = buttonCustomFont
            textButtonStyle!!.fontColor = Color.WHITE
        }
        Gdx.app.log(tag, "Asset manager took $time ms to load all game assets.")
    }

    override fun dispose() {
        super.dispose()

        assetManager.dispose()
        fontGenerator.dispose()
        /*try { // TODO: uncomment this when development is done
            assetManager.dispose()
            fontGenerator.dispose()
        } catch (error: UninitializedPropertyAccessException) {
            Gdx.app.error("BaseGame", "Error $error")
        }*/
    }

    override fun error(asset: AssetDescriptor<*>, throwable: Throwable) {
        Gdx.app.error(tag, "Could not load asset: " + asset.fileName, throwable)
    }
}
