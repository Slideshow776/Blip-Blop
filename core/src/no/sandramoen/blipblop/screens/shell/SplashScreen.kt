package no.sandramoen.blipblop.screens.shell

import com.badlogic.gdx.Application
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import no.sandramoen.blipblop.actors.ShockwaveBackground
import no.sandramoen.blipblop.utils.BaseActor
import no.sandramoen.blipblop.utils.BaseGame
import no.sandramoen.blipblop.utils.BaseScreen
import no.sandramoen.blipblop.utils.GameUtils

class SplashScreen : BaseScreen() {
    private lateinit var tag: String
    private lateinit var shock: ShockwaveBackground

    override fun initialize() {
        tag = "SplashScreen"
        transition.color.a = 0f

        // image with effect
        shock = ShockwaveBackground(0f, 0f, "images/excluded/splash.jpg", mainStage)

        // black overlay
        val background = BaseActor(0f, 0f, mainStage)
        background.loadImage("whitePixel_BIG")
        background.color = Color.BLACK
        background.touchable = Touchable.childrenOnly
        background.setSize(BaseGame.WORLD_WIDTH+2, BaseGame.WORLD_HEIGHT+2)
        background.setPosition(-1f, -1f)
        var totalDurationInSeconds = 6f
        background.addAction(
            Actions.sequence(
                Actions.fadeIn(0f),
                Actions.fadeOut(totalDurationInSeconds / 4),
                Actions.run {
                    // google play services sign in
                    GameUtils.loadGameState()
                    if (Gdx.app.type == Application.ApplicationType.Android && BaseGame.isGPS)
                        BaseGame.gps!!.signIn()
                },
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
