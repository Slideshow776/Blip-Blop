package no.sandramoen.blipblop.ui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.Align
import no.sandramoen.blipblop.utils.BaseGame

class Score(uiTable: Table) {
    private val tag = "Score"
    private var topPlayerLabel: Label
    private var bottomPlayerLabel: Label

    init {
        // initialize
        topPlayerLabel = Label("0", BaseGame.labelStyle)
        topPlayerLabel.setAlignment(Align.center)
        bottomPlayerLabel = Label("0", BaseGame.labelStyle)
        bottomPlayerLabel.setAlignment(Align.center)

        //  positioning
        val table = Table()
        table.add(topPlayerLabel).width(Gdx.graphics.width * .01f)
            .padRight(Gdx.graphics.width * .25f)
        table.add(bottomPlayerLabel).width(Gdx.graphics.width * .01f)
        table.isTransform = true
        table.setOrigin(Gdx.graphics.width * .125f / 2, table.prefHeight / 2)
        table.rotateBy(-90f)
        // scoreTable.debug = true

        // add
        uiTable.add(table).width(Gdx.graphics.width * .125f)
    }

    fun setScore(topScore: Int, bottomScore: Int) {
        topPlayerLabel.setText("$topScore")
        bottomPlayerLabel.setText("$bottomScore")
    }
}