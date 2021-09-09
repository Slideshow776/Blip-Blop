package no.sandramoen.blipblop.actors

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.scenes.scene2d.Event
import com.badlogic.gdx.scenes.scene2d.Stage
import no.sandramoen.blipblop.utils.BaseActor
import no.sandramoen.blipblop.utils.BaseGame
import no.sandramoen.blipblop.utils.GameUtils

class Background2D(x: Float, y: Float, s: Stage) : BaseActor(x, y, s) {
    private val tag = "Background2D"
    private var vertexShaderCode: String
    private var fragmenterShaderCode: String
    var shaderProgram: ShaderProgram

    private var time = .0f
    private var disabled = false

    init {
        loadImage("whitePixel")

        setPosition(x, y)
        setSize(Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())

        ShaderProgram.pedantic = false
        vertexShaderCode = BaseGame.defaultShader.toString()
        fragmenterShaderCode = BaseGame.backgroundShader.toString()
        shaderProgram = ShaderProgram(vertexShaderCode, fragmenterShaderCode)
        if (!shaderProgram.isCompiled)
            Gdx.app.error("Shadertoy", "Shader compile error: " + shaderProgram.log)
        shaderProgram.setUniformf("u_resolution", Vector2(width*.5f, height*.5f))
    }

    override fun draw(batch: Batch, parentAlpha: Float) {
        if (disabled) {
            println("$tag: shader disabled!")
            super.draw(batch, parentAlpha)
        }
        else {
            try {
                println("$tag: rendering shader successfully!")
                batch.shader = shaderProgram
                shaderProgram.setUniformf("u_time", time)
                shaderProgram.setUniformf("u_resolution", Vector2(width, height))
                super.draw(batch, parentAlpha)
                batch.shader = null
            } catch (error: Error) {
                println("$tag: unable to render shader!")
                super.draw(batch, parentAlpha)
            }
        }
    }

    override fun act(dt: Float) {
        super.act(dt)
        time += dt
    }
}
