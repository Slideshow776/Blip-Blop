package no.sandramoen.blipblop.actors

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Stage
import no.sandramoen.blipblop.utils.BaseActor
import no.sandramoen.blipblop.utils.BaseGame

class Background(x: Float, y: Float, s: Stage, fragmentShaderCode: String = BaseGame.backgroundShader.toString()) : BaseActor(x, y, s) {
    private val tag = "Background"
    private var vertexShaderCode: String
    private var fragmenterShaderCode: String
    private var shaderProgram: ShaderProgram
    private var time = .0f
    private var disabled = false

    init {
        loadImage("whitePixel")

        setPosition(x, y)
        setSize(Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())

        ShaderProgram.pedantic = false
        vertexShaderCode = BaseGame.defaultShader.toString()
        fragmenterShaderCode = fragmentShaderCode
        shaderProgram = ShaderProgram(vertexShaderCode, fragmenterShaderCode)
        if (!shaderProgram.isCompiled)
            Gdx.app.error("Background", "Shader compile error: " + shaderProgram.log)
    }

    override fun draw(batch: Batch, parentAlpha: Float) {
        if (disabled) {
            super.draw(batch, parentAlpha)
        } else if (BaseGame.enableCustomShaders) {
            try {
                batch.shader = shaderProgram
                shaderProgram.setUniformf("u_time", time)
                shaderProgram.setUniformf("u_resolution", Vector2(width, height))
                super.draw(batch, parentAlpha)
                batch.shader = null
            } catch (error: Error) {
                super.draw(batch, parentAlpha)
            }
        }
    }

    override fun act(dt: Float) {
        super.act(dt)
        time += dt
    }
}
