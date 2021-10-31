package no.sandramoen.blipblop.actors.challenges

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions

class FoggyVeil(x: Float, y: Float, s: Stage) : Challenge(x, y, s) {
    private var tag = "FoggyVeil"
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

    override fun startChallengeLogic() {
        super.startChallengeLogic()
        addAction(Actions.alpha(1f, 2f, Interpolation.exp10Out))
    }

    override fun resetChallengeLogic() {
        setSize(100f, 25f)
        setPosition(0f, 50f - height / 2)
    }
}
