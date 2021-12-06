package no.sandramoen.blipblop.actors

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.scenes.scene2d.Stage
import no.sandramoen.blipblop.utils.BaseActor
import no.sandramoen.blipblop.utils.BaseGame

class Background(x: Float, y: Float, s: Stage, colour: Vector3) : BaseActor(x, y, s) {
    private val tag = "Background"
    private var vertexShaderCode: String
    private var fragmenterShaderCode: String
    private var shaderProgram: ShaderProgram
    private var time = .0f
    private var disabled = false
    private var colour = colour
    var timeMultiplier = 1f
    var timeIncrement = 1.1f
    var increaseSpeed = false
    var decreaseSpeed = false

    init {
        loadImage("whitePixel")

        setPosition(x, y)
        setSize(Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())

        ShaderProgram.pedantic = false
        vertexShaderCode = BaseGame.defaultShader.toString()
        fragmenterShaderCode = BaseGame.backgroundShader.toString()
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
                shaderProgram.setUniformf("u_time", time * timeMultiplier)
                shaderProgram.setUniformf("u_resolution", Vector2(width * .125f, height * .125f))
                shaderProgram.setUniformf("u_color", colour)
                super.draw(batch, parentAlpha)
                batch.shader = null
            } catch (error: Throwable) {
                super.draw(batch, parentAlpha)
            }
        }
    }

    override fun act(dt: Float) {
        super.act(dt)
        time += dt

        if (increaseSpeed) {
            timeMultiplier += .001f
        }

        if (increaseSpeed && timeMultiplier >= timeIncrement) {
            increaseSpeed = false
            timeIncrement += .1f
        }

        if (decreaseSpeed && timeMultiplier >= 1f) {
            timeMultiplier -= .02f
        }
    }

    fun restart() {
        timeMultiplier = 1f
        timeIncrement = 1.1f
    }
}
