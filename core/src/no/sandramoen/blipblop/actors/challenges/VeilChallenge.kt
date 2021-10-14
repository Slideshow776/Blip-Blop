package no.sandramoen.blipblop.actors.challenges

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import no.sandramoen.blipblop.utils.BaseActor

class VeilChallenge(x: Float, y: Float, s: Stage) : BaseActor(x, y, s) {
    private var start = false

    init {
        loadImage("whitePixel_BIG")
        setSize(100f, 25f)
        setPosition(0f, 50f - height / 2)
        color = Color.PINK
        color.a = 0f
    }

    override fun act(dt: Float) {
        super.act(dt)
        if (start) {
            setSize(width, height + dt)
            setPosition(0f, 50f - height / 2)

            if (height >= 50f) {
                endChallenge()
            }
        }
    }

    fun startChallenge() {
        addAction(Actions.alpha(1f, 2f, Interpolation.exp10Out))
        start = true
    }

    private fun endChallenge() {
        finished = true
        start = false
        addAction(Actions.sequence(
                Actions.fadeOut(1f),
                Actions.run {
                    setSize(100f, 25f)
                    setPosition(0f, 50f - height / 2)
                }
        ))
    }
}
