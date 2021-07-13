package no.sandramoen.blipblop.screens.shell

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import no.sandramoen.blipblop.actors.ShockwaveBackground
import no.sandramoen.blipblop.utils.BaseActor
import no.sandramoen.blipblop.utils.BaseGame
import no.sandramoen.blipblop.utils.BaseScreen

class SplashScreen : BaseScreen() {
    private lateinit var shock: ShockwaveBackground

    override fun initialize() {
        // image with effect
        shock = ShockwaveBackground(0f, 0f, "images/excluded/splash.jpg", mainStage)

        // black overlay
        val background = BaseActor(0f, 0f, mainStage)
        background.loadImage("whitePixel")
        background.color = Color.BLACK
        background.touchable = Touchable.childrenOnly
        background.setSize(BaseGame.WORLD_WIDTH, BaseGame.WORLD_HEIGHT)
        var totalDurationInSeconds = 6f
        background.addAction(
            Actions.sequence(
                Actions.fadeIn(0f),
                Actions.fadeOut(totalDurationInSeconds / 4),
                /*Actions.run {
                    // google play services sign in
                    if (Gdx.app.type == Application.ApplicationType.Android && !BaseGame.disableGPS)
                        BaseGame.gps!!.signIn()
                },*/
                Actions.delay(totalDurationInSeconds / 4),
                Actions.fadeIn(totalDurationInSeconds / 4)
            )
        )
        background.addAction(Actions.after(Actions.run {
            dispose()
            // GameUtils.stopAllMusic()
            BaseGame.setActiveScreen(MenuScreen())
        }))
    }

    override fun update(dt: Float) {}

    override fun dispose() {
        super.dispose()
        shock.shaderProgram.dispose()
        shock.remove()
    }
}
