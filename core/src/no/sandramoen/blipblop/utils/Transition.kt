package no.sandramoen.blipblop.utils

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import no.sandramoen.blipblop.screens.gameplay.ChallengeScreen
import no.sandramoen.blipblop.screens.gameplay.ClassicScreen
import no.sandramoen.blipblop.screens.shell.MenuScreen
import no.sandramoen.blipblop.screens.shell.OptionsScreen

class Transition(x: Float = 0f, y: Float = 0f, s: Stage) : BaseActor(x, y, s) {
    private var tag: String = "Transition"
    private var duration = .125f

    init {
        loadImage("whitePixel_BIG")
        setSize(BaseGame.WORLD_WIDTH+2, BaseGame.WORLD_HEIGHT+2)
        setPosition(-1f, -1f)
        color = Color.BLACK
    }

    fun fadeOut () {
        addAction(Actions.fadeOut(duration))
    }

    fun fadeIn() {
        addAction(Actions.fadeIn(duration))
    }

    fun fadeInToMenuScreen() {
        addAction(Actions.sequence(
                Actions.fadeIn(duration),
                Actions.run {
                    BaseGame.setActiveScreen(MenuScreen())
                }
        ))
    }

    fun fadeInToClassicScreen() {
        addAction(Actions.sequence(
                Actions.fadeIn(duration),
                Actions.run {
                    BaseGame.setActiveScreen(ClassicScreen())
                }
        ))
    }

    fun fadeInToChallengeScreen() {
        addAction(Actions.sequence(
                Actions.fadeIn(duration),
                Actions.run {
                    BaseGame.setActiveScreen(ChallengeScreen())
                }
        ))
    }

    fun fadeInToOptionsScreen() {
        addAction(Actions.sequence(
                Actions.fadeIn(duration),
                Actions.run {
                    BaseGame.setActiveScreen(OptionsScreen())
                }
        ))
    }
}
