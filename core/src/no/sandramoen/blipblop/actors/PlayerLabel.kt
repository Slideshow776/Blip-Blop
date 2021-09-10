package no.sandramoen.blipblop.actors

import com.badlogic.gdx.Application
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.VertexAttributes
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g3d.Material
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.Align
import no.sandramoen.blipblop.utils.BaseActor
import no.sandramoen.blipblop.utils.BaseActor3D
import no.sandramoen.blipblop.utils.BaseGame
import no.sandramoen.blipblop.utils.Stage3D

class PlayerLabel(x: Float, y: Float, s: Stage, bottomPlayer: Boolean) : BaseActor(x, y, s) {
    private var vertexShader: String? = null
    private var fragmentShader: String? = null
    private var shaderProgram: ShaderProgram? = null
    private var time = 0f

    init {
        // set-up
        if (bottomPlayer) {
            loadImage("touchToPlay!")
            color = Color.FIREBRICK
        } else {
            loadImage("touchToPlay!")
            color = Color(0.052f,0.329f,1f, 1f)
        }

        setPosition(0f, 0f)
        setSize(35f, 8f * Gdx.graphics.width / Gdx.graphics.height)
        setOrigin(Align.center)
        var originalRotation =
            if (Gdx.app.type == Application.ApplicationType.Android && !bottomPlayer) 180f
            else 0f
        rotateBy(originalRotation)

        // animation
        val wobbleDuration = .05f
        val wobbleAmount = 2.5f
        val wobbleFrequency = 5f
        addAction(
            Actions.forever(
                Actions.sequence( // wobbling
                    Actions.rotateBy(wobbleAmount, wobbleDuration),
                    Actions.rotateTo(originalRotation, wobbleDuration),
                    Actions.rotateBy(-wobbleAmount, wobbleDuration),
                    Actions.rotateTo(originalRotation, wobbleDuration),
                    Actions.rotateBy(wobbleAmount, wobbleDuration),
                    Actions.rotateTo(originalRotation, wobbleDuration),
                    Actions.rotateBy(-wobbleAmount, wobbleDuration),
                    Actions.rotateTo(originalRotation, wobbleDuration),
                    Actions.delay(wobbleFrequency)
                )
            )
        )

        // shaders
        vertexShader = BaseGame.defaultShader.toString()
        fragmentShader = BaseGame.glowShader.toString()
        shaderProgram = ShaderProgram(vertexShader, fragmentShader)

        // to detect errors in GPU compilation
        if (!shaderProgram!!.isCompiled) println("Couldn't compile shader: " + shaderProgram!!.log)

        time = 0f
    }

    override fun act(dt: Float) {
        super.act(dt)
        time += dt / 4
    }

    override fun draw(batch: Batch, parentAlpha: Float) {
        try {
            batch.shader = shaderProgram
            shaderProgram!!.setUniformf("u_time", time)
            shaderProgram!!.setUniformf("u_imageSize", Vector2(width, height))
            shaderProgram!!.setUniformi("u_glowRadius", 7)
            super.draw(batch, parentAlpha)
            batch.shader = null
        } catch (error: Error) {
            super.draw(batch, parentAlpha)
        }
    }

    fun calculatePosition(x: Float, bottomPlayer: Boolean) {
        val yPosition = if (bottomPlayer) 3f else 93f
        setPosition(x * 100 - width / 2, yPosition)

        if (this.x < 1) this.x = 1f
        else if (this.x + width > 99) this.x = 99f - width
    }
}
