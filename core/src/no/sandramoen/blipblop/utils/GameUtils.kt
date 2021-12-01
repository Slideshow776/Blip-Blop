package no.sandramoen.blipblop.utils

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Event
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.ui.Widget
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener

/**
 * Utility class of often used custom methods.
 */
class GameUtils {
    companion object {
        private val tag = "GameUtils.kt"

        /**
         * Detects if [event] is of type InputEvent.Type.touchDown
         */
        fun isTouchDownEvent(event: Event): Boolean { // Custom type checker
            return event is InputEvent && event.type == InputEvent.Type.touchDown
        }

        /**
         * Play, set volume and loop [music].
         */
        fun playAndLoopMusic(music: Music?) {
            music!!.play()
            music!!.volume = BaseGame.musicVolume
            music!!.isLooping = true
        }

        /**
         * Save persistent game data.
         */
        fun saveGameState() {
            BaseGame.prefs!!.putBoolean("loadPersonalParameters", true)
            BaseGame.prefs!!.putBoolean("googlePlayServices", BaseGame.isGPS)
            BaseGame.prefs!!.putFloat("musicVolume", BaseGame.musicVolume)
            BaseGame.prefs!!.putFloat("soundVolume", BaseGame.soundVolume)
            BaseGame.prefs!!.putBoolean("googlePlayServices", BaseGame.isGPS)
            BaseGame.prefs!!.putString("locale", BaseGame.currentLocale)
            try {
                BaseGame.prefs!!.putFloat("gameTime", BaseGame.gameTime)
            } catch (error: Error) {
                BaseGame.prefs!!.putFloat("gameTime", Float.MAX_VALUE)
            }
            BaseGame.prefs!!.flush()
        }

        /**
         * Load persistent game data.
         */
        fun loadGameState() {
            BaseGame.prefs = Gdx.app.getPreferences("blipBlopGameState")
            BaseGame.loadPersonalParameters = BaseGame.prefs!!.getBoolean("loadPersonalParameters")
            BaseGame.isGPS = BaseGame.prefs!!.getBoolean("googlePlayServices")
            BaseGame.musicVolume = BaseGame.prefs!!.getFloat("musicVolume")
            BaseGame.soundVolume = BaseGame.prefs!!.getFloat("soundVolume")
            BaseGame.isGPS = BaseGame.prefs!!.getBoolean("googlePlayServices")
            BaseGame.gameTime = BaseGame.prefs!!.getFloat("gameTime")
            BaseGame.currentLocale = BaseGame.prefs!!.getString("locale")
        }

        /**
         * Stop all music.
         */
        fun stopAllMusic() {
            BaseGame.levelMusic!!.stop()
        }

        /**
         * Sets the game's music volume to [volume] &#91;0-1&#93;.
         */
        fun setMusicVolume(volume: Float) {
            if (volume > 1f || volume < 0f)
                Gdx.app.error(tag, "setMusicVolume()'s parameter needs to be within [0-1]. Volume is: $volume")
            BaseGame.musicVolume = volume
            BaseGame.levelMusic!!.volume = BaseGame.musicVolume
        }

        /**
         * Adds an [enter]/[exit] color effect on the [textButton].
         */
        fun addTextButtonEnterExitEffect(textButton: TextButton, enterColor: Color = BaseGame.lightPink, exitColor: Color = Color.WHITE) {
            textButton.addListener(object : ClickListener() {
                override fun enter(event: InputEvent?, x: Float, y: Float, pointer: Int, fromActor: Actor?) {
                    textButton.label.color = enterColor
                    super.enter(event, x, y, pointer, fromActor)
                }

                override fun exit(event: InputEvent?, x: Float, y: Float, pointer: Int, toActor: Actor?) {
                    textButton.label.color = exitColor
                    super.exit(event, x, y, pointer, toActor)
                }
            })
        }

        /**
         * Adds an [enter]/[exit] color effect on the [widget].
         */
        fun addWidgetEnterExitEffect(widget: Widget, enter: Color = BaseGame.lightPink, exit: Color = Color.WHITE) {
            widget.addListener(object : ClickListener() {
                override fun enter(event: InputEvent?, x: Float, y: Float, pointer: Int, fromActor: Actor?) {
                    widget.color = enter
                    super.enter(event, x, y, pointer, fromActor)
                }

                override fun exit(event: InputEvent?, x: Float, y: Float, pointer: Int, toActor: Actor?) {
                    widget.color = exit
                    super.exit(event, x, y, pointer, toActor)
                }
            })
        }

        /**
         * Returns a random color where at least one of the color channels has a threshold of [min] &#91;0-1&#93; and [alpha] &#91;0-1&#93;.
         */
        fun randomLightColor(min: Float = 0f, alpha: Float = 1f): Color {
            if (min < 0 || alpha < 0) {
                Gdx.app.error(tag, "randomLightColor()'s parameters values must be greater than 0 => min is: $min, alpha is: $alpha")
                return Color(.5f, .5f, .5f, 1f)
            } else if (min > 1 || alpha > 1) {
                Gdx.app.error(tag, "randomLightColor()'s parameters values must be less than 1 => min is: $min, alpha is: $alpha")
                return Color(.5f, .5f, .5f, 1f)
            }

            var r = 0f
            var g = 0f
            var b = 0f
            while (r <= min && g <= min && b <= min) {
                r = MathUtils.random(0f, 1f)
                g = MathUtils.random(0f, 1f)
                b = MathUtils.random(0f, 1f)
            }

            return Color(r, g, b, alpha)
        }

        /**
         * Returns the normalized value of [x] withing [min] and [max].
         */
        fun normalizeValues(x: Float, min: Float, max: Float): Float {
            val dividend = x - min
            val divisor = max - min
            return dividend / divisor
        }
    }
}
