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

abstract class BaseGame(var googlePlayServices: GooglePlayServices?) : Game(), AssetErrorListener {
    private val tag = "BaseGame.kt"

    init {
        game = this
    }

    companion object {
        private var game: BaseGame? = null

        lateinit var assetManager: AssetManager
        const val WORLD_WIDTH = 100f
        const val WORLD_HEIGHT = 100f
        const val scale = 1.0f
        var RATIO = 0f
        val lightPink = Color(1f, .816f, .94f, 1f)
        var enableCustomShaders = true

        // game assets
        var gps: GooglePlayServices? = null
        var labelStyle: LabelStyle? = null
        var textButtonStyle: TextButtonStyle? = null
        var textureAtlas: TextureAtlas? = null
        var skin: Skin? = null
        var defaultShader: String? = null
        var shockwaveShader: String? = null
        var backgroundShader: String? = null
        var veilShader: String? = null
        var glowShader: String? = null
        var levelMusic: Music? = null
        var blackHoleMusic: Music? = null
        var blipSound: Sound? = null
        var blopSound: Sound? = null
        var startSound: Sound? = null
        var deflectSound: Sound? = null
        var clickSound: Sound? = null
        var gameStartSound: Sound? = null
        var win01Sound: Sound? = null
        var win02Sound: Sound? = null
        var portal1Sound: Sound? = null
        var portal2Sound: Sound? = null
        var rectangleExplosionSound: Sound? = null
        var bubblePopSound: Sound? = null
        var startChallengeSound: Sound? = null
        var countDown0Sound: Sound? = null
        var countDown1Sound: Sound? = null
        var scoreWarningSound: Sound? = null
        var pauseSound: Sound? = null
        var daggerSound: Sound? = null
        var portalWorkingSound: Sound? = null
        var gameTime: Float = 0f

        // game state
        var prefs: Preferences? = null
        var loadPersonalParameters = false
        var soundVolume = .75f
        var musicVolume = .125f
        var isGPS = false
        var registerAchievementFrequency: Float = 3 * 60f   // three minutes
        var biggestAchievementTime: Float = 60 * 60f        // one hour

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
        gps = this.googlePlayServices
        if (!loadPersonalParameters) {
            soundVolume = .75f
            musicVolume = .25f
        }
        RATIO = Gdx.graphics.width.toFloat() / Gdx.graphics.height

        // asset manager
        val time = measureTimeMillis {
            assetManager = AssetManager()
            assetManager.setErrorListener(this)
            assetManager.load("images/included/packed/blipBlop.pack.atlas", TextureAtlas::class.java)

            assetManager.load("audio/music/251461__joshuaempyre__arcade-music-loop.wav", Music::class.java)
            assetManager.load("audio/music/99431__robinhood76__01738-low-creepy-hole.wav", Music::class.java)

            assetManager.load("audio/sound/blip.wav", Sound::class.java)
            assetManager.load("audio/sound/blop.wav", Sound::class.java)
            assetManager.load("audio/sound/start.wav", Sound::class.java)
            assetManager.load("audio/sound/deflect.wav", Sound::class.java)
            assetManager.load("audio/sound/click1.wav", Sound::class.java)
            assetManager.load("audio/sound/gameStart.wav", Sound::class.java)
            assetManager.load("audio/sound/270331__littlerobotsoundfactory__jingle-achievement-00.wav", Sound::class.java)
            assetManager.load("audio/sound/270333__littlerobotsoundfactory__jingle-win-00.mp3", Sound::class.java)
            assetManager.load("audio/sound/portal1.wav", Sound::class.java)
            assetManager.load("audio/sound/portal2.wav", Sound::class.java)
            assetManager.load("audio/sound/386862__prof-mudkip__8-bit-explosion.wav", Sound::class.java)
            assetManager.load("audio/sound/pop.wav", Sound::class.java)
            assetManager.load("audio/sound/Pickup_Coin21.wav", Sound::class.java)
            assetManager.load("audio/sound/countDown0.wav", Sound::class.java)
            assetManager.load("audio/sound/countDown1.wav", Sound::class.java)
            assetManager.load("audio/sound/scoreWarning.wav", Sound::class.java)
            assetManager.load("audio/sound/pause.wav", Sound::class.java)
            assetManager.load("audio/sound/portalWorking.wav", Sound::class.java)
            assetManager.load("audio/sound/175953__freefire66__dagger-drawn2.wav", Sound::class.java)

            val resolver = InternalFileHandleResolver()
            assetManager.setLoader(FreeTypeFontGenerator::class.java, FreeTypeFontGeneratorLoader(resolver))
            assetManager.setLoader(BitmapFont::class.java, ".ttf", FreetypeFontLoader(resolver))
            assetManager.setLoader(Text::class.java, TextLoader(InternalFileHandleResolver()))

            assetManager.load(AssetDescriptor("shaders/default.vs", Text::class.java, TextLoader.TextParameter()))
            assetManager.load(AssetDescriptor("shaders/shockwave.fs", Text::class.java, TextLoader.TextParameter()))
            assetManager.load(AssetDescriptor("shaders/glow-pulse.fs", Text::class.java, TextLoader.TextParameter()))
            assetManager.load(AssetDescriptor("shaders/voronoi01.fs", Text::class.java, TextLoader.TextParameter()))
            assetManager.load(AssetDescriptor("shaders/veil.fs", Text::class.java, TextLoader.TextParameter()))

            assetManager.load("skins/arcade/arcade.json", Skin::class.java)

            assetManager.finishLoading()

            textureAtlas = assetManager.get("images/included/packed/blipBlop.pack.atlas") // all images are found in this global static variable

            // skin
            skin = assetManager.get("skins/arcade/arcade.json", Skin::class.java)

            // audio
            levelMusic = assetManager.get("audio/music/251461__joshuaempyre__arcade-music-loop.wav", Music::class.java)
            blackHoleMusic = assetManager.get("audio/music/99431__robinhood76__01738-low-creepy-hole.wav", Music::class.java)

            blipSound = assetManager.get("audio/sound/blip.wav", Sound::class.java)
            blopSound = assetManager.get("audio/sound/blop.wav", Sound::class.java)
            startSound = assetManager.get("audio/sound/start.wav", Sound::class.java)
            deflectSound = assetManager.get("audio/sound/deflect.wav", Sound::class.java)
            clickSound = assetManager.get("audio/sound/click1.wav", Sound::class.java)
            gameStartSound = assetManager.get("audio/sound/gameStart.wav", Sound::class.java)
            win01Sound = assetManager.get("audio/sound/270331__littlerobotsoundfactory__jingle-achievement-00.wav", Sound::class.java)
            win02Sound = assetManager.get("audio/sound/270333__littlerobotsoundfactory__jingle-win-00.mp3", Sound::class.java)
            portal1Sound = assetManager.get("audio/sound/portal1.wav", Sound::class.java)
            portal2Sound = assetManager.get("audio/sound/portal2.wav", Sound::class.java)
            rectangleExplosionSound = assetManager.get("audio/sound/386862__prof-mudkip__8-bit-explosion.wav", Sound::class.java)
            bubblePopSound = assetManager.get("audio/sound/pop.wav", Sound::class.java)
            startChallengeSound = assetManager.get("audio/sound/Pickup_Coin21.wav", Sound::class.java)
            countDown0Sound = assetManager.get("audio/sound/countDown0.wav", Sound::class.java)
            countDown1Sound = assetManager.get("audio/sound/countDown1.wav", Sound::class.java)
            scoreWarningSound = assetManager.get("audio/sound/scoreWarning.wav", Sound::class.java)
            pauseSound = assetManager.get("audio/sound/pause.wav", Sound::class.java)
            portalWorkingSound = assetManager.get("audio/sound/portalWorking.wav", Sound::class.java)
            daggerSound = assetManager.get("audio/sound/175953__freefire66__dagger-drawn2.wav", Sound::class.java)

            // text files
            defaultShader = assetManager.get("shaders/default.vs", Text::class.java).getString()
            shockwaveShader = assetManager.get("shaders/shockwave.fs", Text::class.java).getString()
            glowShader = assetManager.get("shaders/glow-pulse.fs", Text::class.java).getString()
            backgroundShader = assetManager.get("shaders/voronoi01.fs", Text::class.java).getString()
            veilShader = assetManager.get("shaders/veil.fs", Text::class.java).getString()

            // fonts
            FreeTypeFontGenerator.setMaxTextureSize(2048) // solves font bug that won't show some characters like "." and "," in android
            val fontGenerator = FreeTypeFontGenerator(Gdx.files.internal("fonts/ARCADE_R.TTF"))
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
            val buttonTexUp = textureAtlas!!.findRegion("blankPixel") // button
            // val buttonTexDown = textureAtlas!!.findRegion("button-pressed")
            val buttonPatchUp = NinePatch(buttonTexUp, 44, 24, 24, 24)
            // val buttonPatchDown = NinePatch(buttonTexDown, 44, 24, 24, 24)
            textButtonStyle!!.up = NinePatchDrawable(buttonPatchUp)
            // textButtonStyle!!.down = NinePatchDrawable(buttonPatchDown)
            textButtonStyle!!.font = buttonCustomFont
            textButtonStyle!!.fontColor = Color.WHITE
        }
        Gdx.app.error(tag, "Asset manager took $time ms to load all game assets.")
    }

    override fun dispose() {
        GameUtils.saveGameState()
        super.dispose()
        gps!!.signOut()
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
