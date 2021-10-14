package no.sandramoen.blipblop.screens.gameplay

import no.sandramoen.blipblop.actors.Background
import no.sandramoen.blipblop.utils.BaseGame

class ClassicScreen :  LevelScreen() {
    private val tag = "ClassicScreen"

    override fun initialize() {
        super.initialize()

        // background
        background = Background(0f, 0f, background2DStage, BaseGame.backgroundShader.toString())
    }
}
