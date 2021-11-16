package no.sandramoen.blipblop.utils

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Image

class ScreenTransition {
    val blackOverLay: Image
    val actionDuration = 1f

    init {
        blackOverLay = Image(BaseGame.textureAtlas!!.findRegion("whitePixel"))
        blackOverLay.color = Color.BLACK
        blackOverLay.touchable = Touchable.childrenOnly
        blackOverLay.setSize(Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())
    }

    fun fadeInAndExit() {
        blackOverLay.addAction(Actions.sequence(
            Actions.fadeIn(actionDuration),
            Actions.run {
                Gdx.app.exit()
            }
        ))
    }

    fun fadeIn() { blackOverLay.addAction(Actions.fadeIn(actionDuration)) }
    fun fadeOut() {blackOverLay.addAction(Actions.fadeOut(actionDuration)) }
    fun fadeOut(duration: Float) {blackOverLay.addAction(Actions.fadeOut(duration)) }
}
