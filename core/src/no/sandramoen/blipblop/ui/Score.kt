package no.sandramoen.blipblop.ui

import com.badlogic.gdx.Application
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.actions.Actions
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
        // table.debug = true

        // add
        if (Gdx.app.type == Application.ApplicationType.Android)
            uiTable.add(table).width(Gdx.graphics.width * .125f)//.padLeft(Gdx.graphics.width * .25f)
        else if (Gdx.app.type == Application.ApplicationType.Desktop)
            uiTable.add(table).width(Gdx.graphics.width * .125f)
    }

    fun setScore(topScore: Int, bottomScore: Int) {
        topPlayerLabel.setText("$topScore")
        bottomPlayerLabel.setText("$bottomScore")
    }

    fun topScoreWarning(top: Boolean) {
        BaseGame.scoreWarningSound!!.play(BaseGame.soundVolume)
        val label = if (top) topPlayerLabel else bottomPlayerLabel
        val duration = .1f
        label.addAction(Actions.sequence(
                Actions.color(Color.GOLD, duration),
                Actions.color(Color.WHITE, duration),
                Actions.color(Color.GOLD, duration),
                Actions.color(Color.WHITE, duration),
                Actions.color(Color.GOLD, duration),
                Actions.color(Color.WHITE, duration),
                Actions.color(Color.GOLD, duration),
                Actions.color(Color.WHITE, duration)
        ))
    }
}