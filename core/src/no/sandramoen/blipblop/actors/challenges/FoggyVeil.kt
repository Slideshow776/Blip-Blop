package no.sandramoen.blipblop.actors.challenges

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import no.sandramoen.blipblop.actors.particleEffects.FoggyBottomVeilEffect
import no.sandramoen.blipblop.actors.particleEffects.FoggyTopVeilEffect
import no.sandramoen.blipblop.actors.particleEffects.ParticleActor
import no.sandramoen.blipblop.utils.BaseGame

class FoggyVeil(x: Float, y: Float, s: Stage) : Challenge(x, y, s) {
    private var tag = "FoggyVeil"
    override var title = BaseGame.myBundle!!.get("foggyVeil")

    private var bottomEffect: ParticleActor
    private var topEffect: ParticleActor

    private var vertexShaderCode: String
    private var fragmenterShaderCode: String
    private var shaderProgram: ShaderProgram
    private var time = .0f

    init {
        loadImage("whitePixel_BIG")
        setSize(100f, 25f)
        setPosition(0f, 50f - height / 2)
        color = Color.BLACK
        color.a = 0f
        /*addAction(Actions.fadeOut(0f))*/

        /*val temp = BaseActor(x, y, s)
        temp.setSize(width, height)
        temp.color = Color.GOLD*/

        bottomEffect = FoggyBottomVeilEffect()
        topEffect = FoggyTopVeilEffect()

        ShaderProgram.pedantic = false
        vertexShaderCode = BaseGame.defaultShader.toString()
        fragmenterShaderCode = BaseGame.veilShader.toString()
        shaderProgram = ShaderProgram(vertexShaderCode, fragmenterShaderCode)
        if (!shaderProgram.isCompiled)
            Gdx.app.error("ShockwaveBackground", "Shader compile error: " + shaderProgram.log)
    }

    override fun draw(batch: Batch, parentAlpha: Float) {
        if (BaseGame.enableCustomShaders) {
            try {
                super.draw(batch, parentAlpha)
                batch.shader = shaderProgram
                shaderProgram.setUniformf("u_time", time)
                shaderProgram.setUniformf("u_resolution", Vector2(width*11.1f, height*11.1f))
                shaderProgram.setUniformf("u_alpha", color.a)
                super.draw(batch, parentAlpha)
                batch.shader = null
            } catch (error: Throwable) {
                super.draw(batch, parentAlpha)
            }
        } else
            super.draw(batch, parentAlpha)
    }

    override fun act(dt: Float) {
        super.act(dt)
        time += dt

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
