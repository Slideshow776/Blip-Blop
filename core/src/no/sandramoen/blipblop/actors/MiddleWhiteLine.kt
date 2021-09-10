package no.sandramoen.blipblop.actors

import com.badlogic.gdx.scenes.scene2d.Stage
import no.sandramoen.blipblop.utils.BaseActor

class MiddleWhiteLine(x: Float, y: Float, s: Stage) : BaseActor(x, y, s) {
    init {
        loadImage("whitePixel")
        setSize(100f, .1f)
        setPosition(0f, 50f - height / 2)
    }
}
