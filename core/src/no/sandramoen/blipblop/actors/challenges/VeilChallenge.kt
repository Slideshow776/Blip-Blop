package no.sandramoen.blipblop.actors.challenges

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import no.sandramoen.blipblop.utils.BaseActor

class VeilChallenge(x: Float, y: Float, s: Stage) : BaseActor(x, y, s) {
    private var tag = "VeilChallenge"
    private var start = false
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

    fun startChallenge() {
        addAction(Actions.sequence(
                Actions.delay(3f),
                Actions.alpha(1f, 2f, Interpolation.exp10Out))
        )
        start = true
    }

    private fun endChallenge() {
        if (actions.size == 0) {
            addAction(Actions.sequence(
                    Actions.fadeOut(1f),
                    Actions.delay(4f),
                    Actions.run {
                        setSize(100f, 25f)
                        setPosition(0f, 50f - height / 2)
                        println("$tag: finished!")
                        finished = true
                        start = false
                    }
            ))
        }
    }
}
