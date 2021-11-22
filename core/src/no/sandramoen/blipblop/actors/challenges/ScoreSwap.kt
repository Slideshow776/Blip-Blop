package no.sandramoen.blipblop.actors.challenges

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.Array
import no.sandramoen.blipblop.actors.Player
import no.sandramoen.blipblop.ui.Score
import no.sandramoen.blipblop.utils.BaseGame

class ScoreSwap(x: Float, y: Float, s: Stage, players: Array<Player>, score: Score) : Challenge(x, y, s) {
    private var tag = "ScoreSwap"
    override var title = "Swap Scores!"

    private var players = players
    private var score = score

    private val endTime = 15f
    private var time = 0f
    private var endFlag = false

    override fun act(dt: Float) {
        super.act(dt)
        if (start && !finished) {
            // end challenge
            time += dt
            if (time >= endTime && !endFlag) {
                endFlag = true
                endChallenge()
            }
        }
    }

    override fun startChallengeLogic() {
        super.startChallengeLogic()
        score.setColours(BaseGame.bottomPlayerColor, BaseGame.topPlayerColor)
        swapScores()
    }

    override fun resetChallengeLogic() {
        super.resetChallengeLogic()
        time = 0f
        endFlag = false
        score.setColours(Color.WHITE, Color.WHITE)
        swapScores()
    }

    fun swapScores() {
        val temp = players[1].score
        players[1].score = players[0].score
        players[0].score = temp
        score.setScore(players[1].score, players[0].score)
    }
}
