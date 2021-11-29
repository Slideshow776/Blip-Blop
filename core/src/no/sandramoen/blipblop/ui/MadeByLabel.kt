package no.sandramoen.blipblop.ui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.Align
import no.sandramoen.blipblop.utils.BaseGame
import no.sandramoen.blipblop.utils.GameUtils

class MadeByLabel {
    var label: Label
    init {
        label = Label("${BaseGame.myBundle!!.get("madeBy")} Sandra Moen 2021", BaseGame.labelStyle)
        label.setFontScale(.4f)
        label.setAlignment(Align.center)
        label.color = Color.GRAY
        label.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                super.clicked(event, x, y)
                BaseGame.clickSound!!.play(BaseGame.soundVolume)
                label.addAction(
                    Actions.sequence(
                        Actions.delay(.5f),
                        Actions.run { Gdx.net.openURI("https://sandramoen.no"); }
                    ))
            }
        })
        GameUtils.addWidgetEnterExitEffect(label, Color.GRAY, Color.DARK_GRAY)
    }
}