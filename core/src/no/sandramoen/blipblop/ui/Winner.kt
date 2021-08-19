package no.sandramoen.blipblop.ui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.Align
import no.sandramoen.blipblop.utils.BaseGame


class Winner(uiTable: Table) {
    private val tag = "Winner"

    var topPlayerLabel: Label
    var bottomPlayerLabel: Label

    init {
        // initialize
        val scale = .5f
        topPlayerLabel = Label("Winner!", BaseGame.labelStyle)
        topPlayerLabel.setAlignment(Align.center)
        topPlayerLabel.setFontScale(scale)
        topPlayerLabel.isVisible = false
        bottomPlayerLabel = Label("Winner!", BaseGame.labelStyle)
        bottomPlayerLabel.setAlignment(Align.center)
        bottomPlayerLabel.setFontScale(scale)
        bottomPlayerLabel.isVisible = false

        //  positioning
        val table = Table()
        table.add(topPlayerLabel).width(Gdx.graphics.width * .01f)
            .padRight(Gdx.graphics.width * 1f)
        table.add(bottomPlayerLabel).width(Gdx.graphics.width * .01f)
        table.isTransform = true
        table.setOrigin(Gdx.graphics.width * .125f / 2, table.prefHeight / 2)
        table.rotateBy(-90f)
        // scoreTable.debug = true

        // add
        uiTable.add(table).width(Gdx.graphics.width * .125f)
    }

    fun reset() {
        topPlayerLabel.isVisible = false
        bottomPlayerLabel.isVisible = false
    }
}
