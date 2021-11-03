package no.sandramoen.blipblop

import no.sandramoen.blipblop.screens.gameplay.ChallengeScreen
import no.sandramoen.blipblop.utils.BaseGame
import no.sandramoen.blipblop.utils.GooglePlayServices

class BlipBlopGame(googlePlayServices: GooglePlayServices?) : BaseGame(googlePlayServices) {

    override fun create() {
        super.create()

        // setActiveScreen(SplashScreen()) // TODO: @release: change to this

        // setActiveScreen(MenuScreen())
        // setActiveScreen(OptionsScreen())

        // setActiveScreen(ClassicScreen())
        setActiveScreen(ChallengeScreen())
    }
}
