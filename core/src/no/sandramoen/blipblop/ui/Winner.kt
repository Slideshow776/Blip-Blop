package no.sandramoen.blipblop.ui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.Align
import no.sandramoen.blipblop.actors.particleEffects.GoldDrizzleEffect
import no.sandramoen.blipblop.utils.BaseGame


class Winner(uiTable: Table) {
    private val tag = "Winner"

    var topTable: Table
    var bottomTable: Table

    var topPlayerLabel: Label
    var bottomPlayerLabel: Label

    init {
        // initialize
        val scale = 1.0f
        topPlayerLabel = Label(BaseGame.myBundle!!.get("winner"), BaseGame.labelStyle)
        topPlayerLabel.setAlignment(Align.center)
        topPlayerLabel.setFontScale(scale)
        topPlayerLabel.color.a = 0f
        topTable = Table()
        topTable.add(topPlayerLabel)

        bottomPlayerLabel = Label(BaseGame.myBundle!!.get("winner"), BaseGame.labelStyle)
        bottomPlayerLabel.setAlignment(Align.center)
        bottomPlayerLabel.setFontScale(scale)
        bottomPlayerLabel.color.a = 0f
        bottomTable = Table()
        bottomTable.add(bottomPlayerLabel)

        //  positioning
        val table = Table()
        table.add(topTable).width(Gdx.graphics.width * .01f)
            .padRight(Gdx.graphics.width * 1f)
        table.add(bottomTable).width(Gdx.graphics.width * .01f)
        table.isTransform = true
        table.setOrigin(Gdx.graphics.width * .125f / 2, table.prefHeight / 2)
        table.rotateBy(-90f)
        // scoreTable.debug = true

        // add
        uiTable.add(table).width(Gdx.graphics.width * .125f)
    }

    fun playAnimation(top: Boolean) {
        val winnerAction = Actions.sequence(
            Actions.color(Color.GOLD, 2f),
            Actions.forever(
                Actions.sequence( // pulse
                    Actions.alpha(.8f, .5f),
                    Actions.alpha(1f, .5f)
                )
            )
        )

        if (top) {
            topPlayerLabel.addAction(winnerAction)
            topPlayerLabel.addAction(Actions.fadeIn(.5f))
            startEffect(topTable)
        } else {
            bottomPlayerLabel.addAction(winnerAction)
            bottomPlayerLabel.addAction(Actions.fadeIn(.5f))
            startEffect(bottomTable)
        }
    }

    fun resetAnimation() {
        topPlayerLabel.clearActions()
        bottomPlayerLabel.clearActions()

        if (topPlayerLabel.color.a != 0f) topPlayerLabel.addAction(Actions.sequence(
            Actions.fadeOut(.125f),
            Actions.color(Color.WHITE),
            Actions.fadeOut(0f)
        ))
        if (bottomPlayerLabel.color.a != 0f) bottomPlayerLabel.addAction(Actions.sequence(
            Actions.fadeOut(.125f),
            Actions.color(Color.WHITE),
            Actions.fadeOut(0f)
        ))

        topPlayerLabel.setScale(1f)
        bottomPlayerLabel.setScale(1f)
    }

    private fun startEffect(table: Table) {
        val effect = GoldDrizzleEffect()
        effect.setScale(Gdx.graphics.height * .0001f)
        table.addActor(effect)
        effect.start()
    }
}
