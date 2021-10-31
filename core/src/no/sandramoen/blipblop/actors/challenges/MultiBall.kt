package no.sandramoen.blipblop.actors.challenges

import com.badlogic.gdx.scenes.scene2d.Stage

class MultiBall(x: Float, y: Float, s: Stage) : Challenge(x, y, s) {
    private var tag = "MultiBall"
    private var time = 0f
    private val end = 10f
    private var triggerEnd = true

    override var title = "Multi Ball!"
    var shouldSpawn = false

    override fun startChallengeLogic() {
        super.startChallengeLogic()
        time = 0f
        triggerEnd = true
        shouldSpawn = true
    }

    override fun act(dt: Float) {
        super.act(dt)
        if (time > end && triggerEnd) {
            triggerEnd = false
            endChallenge()
        } else if (start && triggerEnd) {
            time += dt
        }
    }
}
