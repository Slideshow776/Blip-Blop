package no.sandramoen.blipblop.actors.challenges

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import no.sandramoen.blipblop.actors.particleEffects.FoggyBottomVeilEffect
import no.sandramoen.blipblop.actors.particleEffects.FoggyTopVeilEffect
import no.sandramoen.blipblop.actors.particleEffects.ParticleActor

class FoggyVeil(x: Float, y: Float, s: Stage) : Challenge(x, y, s) {
    private var tag = "FoggyVeil"
    override var title = "Foggy Veil!"

    private var bottomEffect: ParticleActor
    private var topEffect: ParticleActor

    init {
        loadImage("whitePixel_BIG")
        setSize(100f, 25f)
        setPosition(0f, 50f - height / 2)
        color = Color.PINK
        color.a = 0f

        bottomEffect = FoggyBottomVeilEffect()
        topEffect = FoggyTopVeilEffect()
    }

    override fun act(dt: Float) {
        super.act(dt)
        if (start && !finished) {
            setSize(width, height + dt)
            setPosition(0f, 50f - height / 2)
            bottomEffect.setPosition(50f, 50f - height / 2)
            topEffect.setPosition(50f, 50f + height / 2)

            if (height >= 50f) {
                endChallenge()
                bottomEffect.stop()
                topEffect.stop()
            }
        }
    }

    override fun startChallengeLogic() {
        super.startChallengeLogic()
        addAction(Actions.alpha(1f, 2f, Interpolation.exp10Out))

        // effect
        bottomEffect.setScale(Gdx.graphics.height * .00004f)
        stage.addActor(bottomEffect)
        bottomEffect.start()
        topEffect.setScale(Gdx.graphics.height * .00004f)
        stage.addActor(topEffect)
        topEffect.start()
    }

    override fun resetChallengeLogic() {
        setSize(100f, 25f)
        setPosition(0f, 50f - height / 2)
        bottomEffect.stop()
        topEffect.stop()
    }
}
