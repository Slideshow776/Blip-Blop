package no.sandramoen.blipblop.actors.challenges

import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.Array
import no.sandramoen.blipblop.actors.Player
import no.sandramoen.blipblop.utils.BaseGame

class LongPlayer(x: Float, y: Float, s: Stage, players: Array<Player>) : Challenge(x, y, s) {
    private var tag = "LongPlayer"
    private var players = players
    private var manipulatePlayers = false

    override var title = BaseGame.myBundle!!.get("longPaddles")

    override fun act(dt: Float) {
        super.act(dt)
        if (start && !finished && manipulatePlayers) {
            when (players[0].hit) {
                0 -> players[0].setScale(6f, 1f, 1f)
                1 -> players[0].setScale(5f, 1f, 1f)
                2 -> players[0].setScale(4f, 1f, 1f)
                3 -> players[0].setScale(3f, 1f, 1f)
                4 -> players[0].setScale(2f, 1f, 1f)
                5 -> players[0].setScale(1f, 1f, 1f)
            }
            when (players[1].hit) {
                0 -> players[1].setScale(6f, 1f, 1f)
                1 -> players[1].setScale(5f, 1f, 1f)
                2 -> players[1].setScale(4f, 1f, 1f)
                3 -> players[1].setScale(3f, 1f, 1f)
                4 -> players[1].setScale(2f, 1f, 1f)
                5 -> players[1].setScale(1f, 1f, 1f)
            }

            if (players[0].hit >= 3 && players[1].hit >= 3)
                endChallenge()
        } else {
            for (player in players)
                player.hit = 0
        }
    }

    override fun resetChallengeLogic() {
        players[0].setScale(1f, 1f, 1f)
        players[1].setScale(1f, 1f, 1f)
    }

    override fun remove(): Boolean {
        resetChallengeLogic()
        return super.remove()
    }

    override fun startChallengeLogic() {
        super.startChallengeLogic()
        manipulatePlayers = true
    }
}
