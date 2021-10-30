package no.sandramoen.blipblop.actors.challenges

import com.badlogic.gdx.scenes.scene2d.Stage

class MultiBall(x: Float, y: Float, s: Stage) : Challenge(x, y, s) {
    private var tag = "MultiBall"
    override var title = "Multi Ball!"
    var shouldSpawn = false

    override fun startChallengeLogic() {
        super.startChallengeLogic()
        shouldSpawn = true
    }
}
