package no.sandramoen.blipblop.ui

import com.badlogic.gdx.Application
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener
import no.sandramoen.blipblop.screens.gameplay.LevelScreen
import no.sandramoen.blipblop.screens.shell.MenuScreen
import no.sandramoen.blipblop.utils.BaseGame
import no.sandramoen.blipblop.utils.GameUtils

class GameMenu(uiTable: Table) {
    private val tag = "GameMenu"

    var restart: TextButton
    var mainMenu: TextButton

    init {
        // initialize
        val scale = if (Gdx.app.type == Application.ApplicationType.Android) .8f else .5f
        val offset = if (Gdx.app.type == Application.ApplicationType.Android) 60f else 80f
        restart = TextButton("Restart", BaseGame.textButtonStyle)
        restart.isTransform = true
        restart.label.setFontScale(scale)
        restart.padRight(offset * scale)
        restart.color = Color.GREEN
        restart.color.a = 0f
        GameUtils.addTextButtonEnterExitEffect(restart)

        mainMenu = TextButton("Main Menu", BaseGame.textButtonStyle)
        mainMenu.isTransform = true
        mainMenu.label.setFontScale(scale)
        mainMenu.padRight(offset * scale)
        mainMenu.color = Color.RED
        mainMenu.color.a = 0f
        mainMenu.addListener(object : ActorGestureListener() {
            override fun tap(event: InputEvent?, x: Float, y: Float, count: Int, button: Int) {
                BaseGame.clickSound!!.play(BaseGame.soundVolume)
                // TODO: screen transition
                BaseGame.setActiveScreen(MenuScreen())
            }
        })
        GameUtils.addTextButtonEnterExitEffect(mainMenu)

        disappear()

        // positioning
        val table = Table()
        if (Gdx.app.type == Application.ApplicationType.Android) {
            table.add(restart)
            table.row()
            table.add(mainMenu)
            table.isTransform = true
            table.setOrigin(Gdx.graphics.width * .25f / 2, table.prefHeight / 2)
            table.rotateBy(-90f)

            uiTable.add(table).width(Gdx.graphics.width * .25f).padLeft(Gdx.graphics.width * .25f)
        } else if (Gdx.app.type == Application.ApplicationType.Desktop) {
            table.add(restart)
            table.row()
            table.add(mainMenu)

            uiTable.add(table).width(Gdx.graphics.width * .25f).expandX().padLeft(Gdx.graphics.width * .25f)
        }
        // scoreTable.debug = true
    }

    fun appear(delay: Float = 1f) {
        restart.addAction(Actions.sequence(
            Actions.delay(delay),
            Actions.fadeIn(1.5f)
        ))
        restart.touchable = Touchable.enabled
        mainMenu.addAction(Actions.sequence(
            Actions.delay(delay),
            Actions.fadeIn(1.5f)
        ))
        mainMenu.touchable = Touchable.enabled
    }

    fun disappear() {
        restart.addAction(Actions.fadeOut(.25f))
        restart.touchable = Touchable.disabled
        mainMenu.addAction(Actions.fadeOut(.25f))
        mainMenu.touchable = Touchable.disabled
    }
}
