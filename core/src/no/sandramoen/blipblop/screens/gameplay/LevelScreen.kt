package no.sandramoen.blipblop.screens.gameplay

import no.sandramoen.blipblop.actors.Player
import no.sandramoen.blipblop.utils.BaseScreen

class LevelScreen : BaseScreen() {
    private val token = "LevelScreen"

    override fun initialize() {
        Player(mainStage, true)
        Player(mainStage, false)
    }

    override fun update(dt: Float) {

    }

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {

        return false
    }

    override fun keyDown(keycode: Int): Boolean { // desktop controls

        return false
    }


}
