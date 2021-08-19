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
            loadImage("p1Missing")
            color = Color.FIREBRICK
        } else {
            loadImage("p2Missing")
            color = Color.CYAN
        }
        setPosition(0f, 0f)
        setSize(15f, 15f * Gdx.graphics.width / Gdx.graphics.height)
        setOrigin(Align.center)
        var originalRotation =
            if (Gdx.app.type == Application.ApplicationType.Android && !bottomPlayer) 180f
            else 0f
        rotateBy(originalRotation)

        // animation
        val wobbleDuration = .05f
        addAction(
            Actions.forever(
                Actions.sequence( // wobbling
                    Actions.rotateBy(5f, wobbleDuration),
                    Actions.rotateTo(originalRotation, wobbleDuration),
                    Actions.rotateBy(-5f, wobbleDuration),
                    Actions.rotateTo(originalRotation, wobbleDuration),
                    Actions.rotateBy(5f, wobbleDuration),
                    Actions.rotateTo(originalRotation, wobbleDuration),
                    Actions.rotateBy(-5f, wobbleDuration),
                    Actions.rotateTo(originalRotation, wobbleDuration),
                    Actions.delay(5f)
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
        val yPosition = if (bottomPlayer) 1f else 92f
        setPosition(x * 100 - width / 2, yPosition)

        if (this.x < 0) this.x = 0f
        else if (this.x + width > 100) this.x = 100f - width
    }
}
