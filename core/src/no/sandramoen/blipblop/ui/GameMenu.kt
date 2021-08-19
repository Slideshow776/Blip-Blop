package no.sandramoen.blipblop.ui

import com.badlogic.gdx.Application
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener
import no.sandramoen.blipblop.utils.BaseGame

class GameMenu(uiTable: Table) {
    private val tag = "GameMenu"

    var restart: TextButton
    var mainMenu: TextButton

    init {
        // initialize
        val scale = if (Gdx.app.type == Application.ApplicationType.Android) .8f else .5f
        restart = TextButton("Restart", BaseGame.textButtonStyle)
        restart.isTransform = true
        restart.label.setFontScale(scale)

        mainMenu = TextButton("Main Menu", BaseGame.textButtonStyle)
        mainMenu.isTransform = true
        mainMenu.label.setFontScale(scale)
        mainMenu.addListener(object : ActorGestureListener() {
            override fun tap(event: InputEvent?, x: Float, y: Float, count: Int, button: Int) {
                // TODO: set screen to main menu
                Gdx.app.error(tag, "mainMenu button touched!")
            }
        })
        disappear()

        // positioning
        val table = Table()
        if (Gdx.app.type == Application.ApplicationType.Android) {
            table.add(restart).width(restart.width * scale).padLeft(Gdx.graphics.width * .125f)
            table.add(mainMenu).width(mainMenu.width * scale)
            table.isTransform = true
            table.setOrigin(Gdx.graphics.width * .25f / 2, table.prefHeight / 2)
            table.rotateBy(-90f)

            uiTable.add(table).width(Gdx.graphics.width * .25f).padLeft(Gdx.graphics.width * .5f)
        } else if (Gdx.app.type == Application.ApplicationType.Desktop) {
            table.add(restart).width(restart.width * scale)
            table.row()
            table.add(mainMenu).width(mainMenu.width * scale)

            uiTable.add(table).width(Gdx.graphics.width * .25f).expandX().padLeft(Gdx.graphics.width * .25f)
        }
        // scoreTable.debug = true
    }

    fun appear() {
        restart.isVisible = true
        restart.touchable = Touchable.enabled
        mainMenu.isVisible = true
        mainMenu.touchable = Touchable.enabled
    }

    fun disappear() {
        restart.isVisible = false
        restart.touchable = Touchable.disabled
        mainMenu.isVisible = false
        mainMenu.touchable = Touchable.disabled
    }
}
