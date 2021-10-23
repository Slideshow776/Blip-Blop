package no.sandramoen.blipblop.actors.challenges

import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import no.sandramoen.blipblop.utils.BaseActor

open class Challenge(x: Float, y: Float, s: Stage) : BaseActor(x, y, s) {
    private var tag = "Challenge"
    open var start = false
    override var title = "Warning: Challenge have no title!"

    fun startChallenge() {
        addAction(Actions.sequence(
                Actions.delay(3f),
                Actions.alpha(1f, 2f, Interpolation.exp10Out))
        )
        start = true
    }

    fun endChallenge() {
        if (actions.size == 0) {
            addAction(Actions.sequence(
                    Actions.fadeOut(1f),
                    Actions.delay(4f),
                    Actions.run {
                        resetChallenge()
                        finished = true
                        start = false
                    }
            ))
        }
    }

    open fun resetChallenge() {}
}
