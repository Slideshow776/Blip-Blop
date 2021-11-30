package no.sandramoen.blipblop.actors

import com.badlogic.gdx.Application
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.utils.Align
import no.sandramoen.blipblop.utils.BaseActor
import no.sandramoen.blipblop.utils.BaseGame

class PlayerLabel(x: Float, y: Float, s: Stage, bottomPlayer: Boolean) : BaseActor(x, y, s) {
    private var vertexShader: String? = null
    private var fragmentShader: String? = null
    private var shaderProgram: ShaderProgram? = null
    private var time = 0f

    init {
        // set-up
        if (bottomPlayer) { color = BaseGame.bottomPlayerColor }
        else { color = BaseGame.topPlayerColor }

        if (BaseGame.myBundle!!.get("locale") == "no") loadImage("touchToPlay!_no")
        else loadImage("touchToPlay!")

        setPosition(0f, 0f)
        setSize(37f, 8f * BaseGame.RATIO)
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
        if (BaseGame.enableCustomShaders) {
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
        } else {
            super.draw(batch, parentAlpha)
        }
    }

    fun calculatePosition(x: Float, bottomPlayer: Boolean) {
        val yPosition = if (bottomPlayer) 4f else 92f
        setPosition(x * 100 - width / 2, yPosition)

        if (this.x < 1) this.x = 1f
        else if (this.x + width > 99) this.x = 99f - width
    }
}
