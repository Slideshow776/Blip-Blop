package no.sandramoen.blipblop.actors

import com.badlogic.gdx.Application
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.utils.Align
import no.sandramoen.blipblop.utils.BaseActor

class Player(s: Stage, bottomPlayer: Boolean) : BaseActor(0f, 0f, s) {
    val bottomPlayer: Boolean = bottomPlayer
    var touchX = 0f

    private val tag = "Player"
    private val touchDeadZone = 5f // removes player twitching
    private var paddleCenter = 0f

    init {
        // miscellaneous
        setSize(15.5f, 2.5f)
        if (bottomPlayer) setPosition(50f - width / 2, 10f)
        else setPosition(50f - width / 2, 90f)
        setOrigin(Align.center)
        paddleCenter = x + width / 2
        touchX = paddleCenter

        // physics
        setAcceleration(Gdx.graphics.width * 3f) // pixels/seconds
        setMaxSpeed(Gdx.graphics.width * 0.25f)
        setDeceleration(Gdx.graphics.width * 3f)
        setBoundaryRectangle()
    }

    override fun act(dt: Float) {
        super.act(dt)

        applyPhysics(dt)

        if (x < 0 || x + width > getWorldBounds().width) { // stops the player from going off screen
            setSpeed(0f)
            boundToWorld()
        }

        /*if (Gdx.app.type == Application.ApplicationType.Android) {*/
            val paddleCenter = x + width / 2
            when {
                touchX > paddleCenter + touchDeadZone -> moveRight()
                touchX < paddleCenter - touchDeadZone -> moveLeft()
                else -> moveForward()
            }
        /*} else { // desktop controls
            when {
                bottomPlayer && Gdx.input.isKeyPressed(Input.Keys.LEFT) -> moveLeft()
                bottomPlayer && Gdx.input.isKeyPressed(Input.Keys.RIGHT) -> moveRight()
                !bottomPlayer && Gdx.input.isKeyPressed(Input.Keys.A) -> moveLeft()
                !bottomPlayer && Gdx.input.isKeyPressed(Input.Keys.D) -> moveRight()
                else -> moveForward()
            }
        }*/
    }

    fun hitAnimation() {
        val amount = .2f
        val duration = .05f
        val bounceAction =
                if (bottomPlayer)
                    Actions.sequence(
                            Actions.moveBy(0f, -amount, duration),
                            Actions.moveBy(0f, amount, duration)
                    )
                else
                    Actions.sequence(
                            Actions.moveBy(0f, amount, duration),
                            Actions.moveBy(0f, -amount, duration)
                    )
        addAction(
                Actions.parallel(
                        bounceAction,
                        Actions.sequence(
                                Actions.scaleBy(.1f, -.1f, duration * 2),
                                Actions.scaleTo(1f, 1f, duration * 2)
                        )
                )
        )
    }

    private fun moveLeft() {
        accelerateAtAngle(180f)
        addAction(Actions.rotateTo(2f, .75f))
    }

    private fun moveRight() {
        accelerateAtAngle(0f)
        addAction(Actions.rotateTo(-2f, .75f))
    }

    private fun moveForward() {
        addAction(Actions.rotateTo(0f, .5f))
    }
}