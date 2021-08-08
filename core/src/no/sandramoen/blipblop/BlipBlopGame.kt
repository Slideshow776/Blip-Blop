package no.sandramoen.blipblop

import no.sandramoen.blipblop.screens.gameplay.LevelScreen
import no.sandramoen.blipblop.utils.BaseGame

class BlipBlopGame() : BaseGame() {

    override fun create() {
        super.create()

        // setActiveScreen(SplashScreen()) // TODO: @release: change to this
        setActiveScreen(LevelScreen())
        // setActiveScreen(MenuScreen())
    }
}
