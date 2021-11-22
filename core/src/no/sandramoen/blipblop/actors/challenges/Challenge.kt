package no.sandramoen.blipblop.actors.challenges

import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import no.sandramoen.blipblop.utils.BaseActor
import no.sandramoen.blipblop.utils.BaseGame

open class Challenge(x: Float, y: Float, s: Stage) : BaseActor(x, y, s) {
    private var tag = "Challenge"
    open var start = false
    open var pauseChallenge = false
    override var title = "Warning: Challenge has no title!"

    open fun startChallenge() {
        addAction(Actions.sequence(
                Actions.delay(3f),
                Actions.run { startChallengeLogic() }
        ))
        start = true
    }

    fun endChallenge(duration: Float = 1f) {
        if (duration == 0f) {
            resetChallengeLogic()
            finished = true
            start = false
        } else if (actions.size == 0) { // TODO: relying on actions to be zero is not too good...
            addAction(Actions.sequence(
                    Actions.fadeOut(1f),
                    Actions.delay(4f),
                    Actions.run {
                        resetChallengeLogic()
                        finished = true
                        start = false
                    }
            ))
        }
    }

    open fun startChallengeLogic() {}
    open fun resetChallengeLogic() {}
}
