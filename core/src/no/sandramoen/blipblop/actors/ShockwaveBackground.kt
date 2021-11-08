package no.sandramoen.blipblop.actors

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.scenes.scene2d.Event
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.actions.RunnableAction
import no.sandramoen.blipblop.utils.BaseActor
import no.sandramoen.blipblop.utils.BaseGame
import no.sandramoen.blipblop.utils.GameUtils

class ShockwaveBackground(x: Float, y: Float, texturePath: String, s: Stage) : BaseActor(x, y, s) {
    private var tag: String = "ShockwaveBackground"
    private var vertexShaderCode: String
    private var fragmenterShaderCode: String
    var shaderProgram: ShaderProgram

    private var time = .0f
    private var shockWavePositionX = -5.0f
    private var shockWavePositionY = -5.0f
    private var disabled = false

    init {
        if (texturePath.isNotBlank()) loadTexture(texturePath)
        else Gdx.app.error(tag, "texturePath is blank!")
        setSize(BaseGame.WORLD_WIDTH, BaseGame.WORLD_HEIGHT)

        ShaderProgram.pedantic = false
        vertexShaderCode = BaseGame.defaultShader.toString()
        fragmenterShaderCode = BaseGame.shockwaveShader.toString()
        shaderProgram = ShaderProgram(vertexShaderCode, fragmenterShaderCode)
        if (!shaderProgram.isCompiled)
            Gdx.app.error("ShockwaveBackground", "Shader compile error: " + shaderProgram.log)

        addListener { e: Event ->
            if (GameUtils.isTouchDownEvent(e)) {
                val x = (Gdx.input.x.toFloat() - 0) / (Gdx.graphics.width - 0)
                val y = (Gdx.input.y.toFloat() - 0) / (Gdx.graphics.height - 0)
                start(x, y) // x and y are normalized
            }
            false
        }
    }

    override fun draw(batch: Batch, parentAlpha: Float) {
        if (disabled)
            super.draw(batch, parentAlpha)
        if (BaseGame.enableCustomShaders) {
            try {
                batch.shader = shaderProgram
                shaderProgram.setUniformf("time", time)
                shaderProgram.setUniformf("center", Vector2(shockWavePositionX, shockWavePositionY))
                shaderProgram.setUniformf("shockParams", Vector3(10f, .8f, .1f))
                super.draw(batch, parentAlpha)
                batch.shader = null
            } catch (error: Error) {
                super.draw(batch, parentAlpha)
            }
        } else
            super.draw(batch, parentAlpha)
    }

    override fun act(dt: Float) {
        super.act(dt)
        time += dt
    }

    private fun start(normalizedPosX: Float, normalizedPosY: Float) {
        if (time >= 1f) { // prevents interrupting previous animation
            this.shockWavePositionX = normalizedPosX
            this.shockWavePositionY = normalizedPosY
            val enable = RunnableAction()
            enable.runnable = Runnable { disabled = true }
            this.addAction(Actions.delay(1f, enable))
            disabled = false
            time = 0f
        }
    }
}
