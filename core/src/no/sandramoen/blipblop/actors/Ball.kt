package no.sandramoen.blipblop.actors

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.utils.Align
import no.sandramoen.blipblop.utils.BaseActor
import no.sandramoen.blipblop.utils.BaseGame

class Ball(s: Stage, shadowBall: Boolean = false) : BaseActor(BaseGame.WORLD_WIDTH / 2, BaseGame.WORLD_HEIGHT / 2, s) {
    private val tag = "Ball"
    private val shadowBall = shadowBall

    val ballSpeed = 90f
    var inPlay: Boolean = true

    init {
        // miscellaneous
        setSize(
                3f,
                3f * Gdx.graphics.width / Gdx.graphics.height
        )
        if (!shadowBall) BaseGame.startSound!!.play(BaseGame.soundVolume) // TODO: this needs to be in LevelScreen.kt somewhere? : )
        setOrigin(Align.center)

        // physics
        setSpeed(ballSpeed) // pixels / seconds
        setMaxSpeed(ballSpeed * 5)
        setDeceleration(0f)
        setMotionAngle(270f)
        setBoundaryRectangle()
    }

    override fun act(dt: Float) {
        super.act(dt)
        if (inPlay) {
            applyPhysics(dt)

            if (x < 0 || x + width > getWorldBounds().width) { // bounce off walls
                setVelocity(Vector2(getVelocity().x * -1f, getVelocity().y))
                boundToWorld()
                wallImpactAnimation()
                if (!shadowBall) BaseGame.deflectSound!!.play(BaseGame.soundVolume)
            }

            if (!shadowBall && (y < 0 - height || y - height > getWorldBounds().height))
                inPlay = false
        }
    }

    fun playerImpactAnimation() {
        val duration = .075f
        addAction(Actions.sequence(
                Actions.scaleBy(.75f, -.75f, duration),
                Actions.scaleTo(1f, 1f, duration)
        ))
    }

    fun toggleCollision() {
        collisionEnabled = false
        addAction(Actions.sequence(
                Actions.delay(.5f),
                Actions.run { collisionEnabled = true }
        ))
    }

    fun reset() {
        if (shadowBall) return
        setPosition(BaseGame.WORLD_WIDTH / 2 - width / 2, BaseGame.WORLD_HEIGHT / 2 - height / 2)
        setSpeed(ballSpeed / 2) // pixels / seconds
        if (MathUtils.randomBoolean()) setMotionAngle(MathUtils.random(30f, 150f))
        else setMotionAngle(MathUtils.random(210f, 330f))

        actions.clear()
        addAction(Actions.scaleTo(1f, 1f, 0f))

        BaseGame.startSound!!.play(BaseGame.soundVolume)
        collisionEnabled = true
        inPlay = true
    }

    private fun wallImpactAnimation() {
        val duration = .1f
        addAction(Actions.sequence(
                Actions.scaleBy(-.75f, .75f, duration),
                Actions.scaleTo(1f, 1f, duration)
        ))
    }
}