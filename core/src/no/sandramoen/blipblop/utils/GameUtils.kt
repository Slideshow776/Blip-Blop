package no.sandramoen.blipblop.utils

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.graphics.Color
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
            BaseGame.prefs!!.putBoolean("googlePlayServices", BaseGame.disableGPS)
            BaseGame.prefs!!.putFloat("musicVolume", BaseGame.musicVolume)
            BaseGame.prefs!!.putFloat("soundVolume", BaseGame.soundVolume)
            BaseGame.prefs!!.flush()
        }

        fun loadGameState() {
            BaseGame.prefs = Gdx.app.getPreferences("blipBlopGameState")
            BaseGame.loadPersonalParameters = BaseGame.prefs!!.getBoolean("loadPersonalParameters")
            BaseGame.disableGPS = BaseGame.prefs!!.getBoolean("googlePlayServices")
            BaseGame.musicVolume = BaseGame.prefs!!.getFloat("musicVolume")
            BaseGame.soundVolume = BaseGame.prefs!!.getFloat("soundVolume")
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
    }
}
