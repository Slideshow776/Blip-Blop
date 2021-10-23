package no.sandramoen.blipblop.actors.challenges

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Stage

class VeilChallenge(x: Float, y: Float, s: Stage) : Challenge(x, y, s) {
    private var tag = "VeilChallenge"
    override var title = "Foggy Veil!"

    init {
        loadImage("whitePixel_BIG")
        setSize(100f, 25f)
        setPosition(0f, 50f - height / 2)
        color = Color.PINK
        color.a = 0f
    }

    override fun act(dt: Float) {
        super.act(dt)
        if (start && !finished) {
            setSize(width, height + dt)
            setPosition(0f, 50f - height / 2)

            if (height >= 50f) {
                endChallenge()
            }
        }
    }

    override fun resetChallenge() {
        setSize(100f, 25f)
        setPosition(0f, 50f - height / 2)
    }
}
