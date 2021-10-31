package no.sandramoen.blipblop.utils

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Event
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener

class GameUtils {
    private val tag = "GameUtils.kt"

    companion object {
        fun isTouchDownEvent(e: Event): Boolean { // Custom type checker
            return e is InputEvent && e.type == InputEvent.Type.touchDown
        }

        fun playAndLoopMusic(music: Music?) {
            music!!.play()
            music!!.volume = BaseGame.musicVolume
            music!!.isLooping = true
        }

        fun saveGameState() {
            BaseGame.prefs!!.putBoolean("loadPersonalParameters", true)
            BaseGame.prefs!!.putBoolean("googlePlayServices", BaseGame.isGPS)
            BaseGame.prefs!!.putFloat("musicVolume", BaseGame.musicVolume)
            BaseGame.prefs!!.putFloat("soundVolume", BaseGame.soundVolume)
            BaseGame.prefs!!.putBoolean("googlePlayServices", BaseGame.isGPS)
            try {
                BaseGame.prefs!!.putFloat("gameTime", BaseGame.gameTime)
            } catch (error: Error) {
                BaseGame.prefs!!.putFloat("gameTime", Float.MAX_VALUE)
            }
            BaseGame.prefs!!.flush()
        }

        fun loadGameState() {
            BaseGame.prefs = Gdx.app.getPreferences("blipBlopGameState")
            BaseGame.loadPersonalParameters = BaseGame.prefs!!.getBoolean("loadPersonalParameters")
            BaseGame.isGPS = BaseGame.prefs!!.getBoolean("googlePlayServices")
            BaseGame.musicVolume = BaseGame.prefs!!.getFloat("musicVolume")
            BaseGame.soundVolume = BaseGame.prefs!!.getFloat("soundVolume")
            BaseGame.isGPS = BaseGame.prefs!!.getBoolean("googlePlayServices")
            BaseGame.gameTime = BaseGame.prefs!!.getFloat("gameTime")
        }

        fun stopAllMusic() {
            BaseGame.levelMusic!!.stop()
        }

        fun setMusicVolume() {
            BaseGame.levelMusic!!.volume = BaseGame.musicVolume
        }

        fun addTextButtonEnterExitEffect(button: TextButton, enterColor: Color = BaseGame.lightPink, exitColor: Color = Color.WHITE) {
            button.addListener(object : ClickListener() {
                override fun enter(
                        event: InputEvent?,
                        x: Float,
                        y: Float,
                        pointer: Int,
                        fromActor: Actor?
                ) {
                    button.label.color = enterColor
                    super.enter(event, x, y, pointer, fromActor)
                }

                override fun exit(
                        event: InputEvent?,
                        x: Float,
                        y: Float,
                        pointer: Int,
                        toActor: Actor?
                ) {
                    button.label.color = exitColor
                    super.exit(event, x, y, pointer, toActor)
                }
            })
        }

        fun addLabelButtonEnterExitEffect(label: Label, enter: Color = BaseGame.lightPink, exit: Color = Color.WHITE) {
            label.addListener(object : ClickListener() {
                override fun enter(
                        event: InputEvent?,
                        x: Float,
                        y: Float,
                        pointer: Int,
                        fromActor: Actor?
                ) {
                    label.color = enter
                    super.enter(event, x, y, pointer, fromActor)
                }

                override fun exit(
                        event: InputEvent?,
                        x: Float,
                        y: Float,
                        pointer: Int,
                        toActor: Actor?
                ) {
                    label.color = exit
                    super.exit(event, x, y, pointer, toActor)
                }
            })
        }

        fun randomColor(): Color {
            when (MathUtils.random(28)) {
                0 -> return Color.RED
                1 -> return Color.BLUE
                2 -> return Color.BROWN
                3 -> return Color.CHARTREUSE
                4 -> return Color.CORAL
                5 -> return Color.CYAN
                6 -> return Color.FIREBRICK
                7 -> return Color.FOREST
                8 -> return Color.GOLD
                9 -> return Color.GOLDENROD
                10 -> return Color.GREEN
                11 -> return Color.LIME
                12 -> return Color.MAGENTA
                13 -> return Color.MAROON
                14 -> return Color.NAVY
                15 -> return Color.OLIVE
                16 -> return Color.ORANGE
                17 -> return Color.PINK
                18 -> return Color.PURPLE
                19 -> return Color.RED
                20 -> return Color.ROYAL
                21 -> return Color.SALMON
                22 -> return Color.SCARLET
                23 -> return Color.SKY
                24 -> return Color.SLATE
                25 -> return Color.TAN
                26 -> return Color.TEAL
                27 -> return Color.VIOLET
                28 -> return Color.YELLOW
            }
            return Color.WHITE
        }
    }
}
