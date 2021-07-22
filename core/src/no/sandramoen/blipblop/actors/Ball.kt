package no.sandramoen.blipblop.actors

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.utils.Align
import no.sandramoen.blipblop.utils.BaseActor
import no.sandramoen.blipblop.utils.BaseGame

class Ball(s: Stage) : BaseActor(BaseGame.WORLD_WIDTH / 2, BaseGame.WORLD_HEIGHT / 2, s) {
    private val tag = "Ball"
    val ballSpeed = Gdx.graphics.height / 20f
    var canBePaddled = true

    init {
        // miscellaneous
        setSize(
            3f,
            3f * Gdx.graphics.width / Gdx.graphics.height
        )
        BaseGame.startSound!!.play(BaseGame.soundVolume) // TODO: this needs to be in LevelScreen.kt somewhere? : )
        setOrigin(Align.center)

        // physics
        setSpeed(ballSpeed / 2) // pixels / seconds
        setMaxSpeed(ballSpeed * 2)
        setDeceleration(0f)
        setMotionAngle(270f)
        setBoundaryRectangle()
    }

    override fun act(dt: Float) {
        super.act(dt)
        applyPhysics(dt)

        if (x < 0 || x + width > getWorldBounds().width) { // bounce off walls
            setVelocity(Vector2(getVelocity().x * -1f, getVelocity().y))
            boundToWorld()
            wallHit()
            BaseGame.deflectSound!!.play(BaseGame.soundVolume)
        }

        if (y < 0 || y + height > getWorldBounds().height) { // stops the ball from going off screen // TODO: this needs to be in LevelScreen.kt somewhere? : )
            reset()
        }
    }

    fun hitAnimation() {
        val duration = .075f
        addAction(Actions.sequence(
            Actions.scaleBy(.75f, -.75f, duration),
            Actions.scaleTo(1f, 1f, duration)
        ))
    }

    private fun wallHit() {
        val duration = .1f
        addAction(Actions.sequence(
            Actions.scaleBy(-.75f, .75f, duration),
            Actions.scaleTo(1f, 1f, duration)
        ))
    }

    private fun reset() {
        setPosition(BaseGame.WORLD_WIDTH / 2, BaseGame.WORLD_HEIGHT / 2)
        setSpeed(ballSpeed / 2) // pixels / seconds
        if (MathUtils.randomBoolean())
            setMotionAngle(MathUtils.random(30f, 150f))
        else
            setMotionAngle(MathUtils.random(210f, 330f))
        BaseGame.startSound!!.play(BaseGame.soundVolume) // TODO: this needs to be in LevelScreen.kt somewhere? : )
        actions.clear()
        addAction(Actions.scaleTo(1f, 1f, 0f))
    }
}