package no.sandramoen.blipblop.ui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.Align
import no.sandramoen.blipblop.utils.BaseGame

class Score(uiTable: Table) {
    private var topPlayerScoreLabel: Label
    private var bottomPlayerScoreLabel: Label

    init {
        // initialize
        topPlayerScoreLabel = Label("0", BaseGame.labelStyle)
        topPlayerScoreLabel.setAlignment(Align.center)
        bottomPlayerScoreLabel = Label("0", BaseGame.labelStyle)
        bottomPlayerScoreLabel.setAlignment(Align.center)

        //  enable
        val scoreTable = Table()
        scoreTable.add(topPlayerScoreLabel).width(Gdx.graphics.width * .01f)
            .padRight(Gdx.graphics.width * .25f)
        scoreTable.add(bottomPlayerScoreLabel).width(Gdx.graphics.width * .01f)
        scoreTable.isTransform = true
        scoreTable.setOrigin(Gdx.graphics.width * .5f / 2, scoreTable.prefHeight / 2)
        scoreTable.rotateBy(-90f)
        // scoreTable.debug = true

        // add
        uiTable.add(scoreTable).width(Gdx.graphics.width * .5f).fillX()
            .padLeft(Gdx.graphics.width * .85f)
    }

    fun setScore(topScore: Int, bottomScore: Int) {
        topPlayerScoreLabel.setText("$topScore")
        bottomPlayerScoreLabel.setText("$bottomScore")
    }
}