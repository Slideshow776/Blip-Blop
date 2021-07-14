package no.sandramoen.blipblop.actors

import com.badlogic.gdx.Application
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import no.sandramoen.blipblop.utils.BaseActor

class Player(s: Stage, player1: Boolean) : BaseActor(0f, 0f, s) {
    private var player1: Boolean = player1

    init {
        // miscellaneous
        setSize(15.5f, 2.5f)
        if (player1) setPosition(10f, 10f)
        else setPosition(90f, 90f)

        // physics
        setAcceleration(Gdx.graphics.width / 1f) // pixels/seconds
        setMaxSpeed(Gdx.graphics.width / 8f)
        setDeceleration(Gdx.graphics.width / 1f)
        setBoundaryRectangle()
    }

    override fun act(dt: Float) {
        super.act(dt)

        applyPhysics(dt)

        if (x < 0 || x + width > getWorldBounds().width) { // stops the player from going off screen
            setSpeed(0f)
            boundToWorld()
        }

        if (Gdx.app.type == Application.ApplicationType.Android) {
            // TODO: implement this
        } else { // desktop controls
            when {
                player1 && Gdx.input.isKeyPressed(Input.Keys.LEFT) -> moveLeft()
                player1 && Gdx.input.isKeyPressed(Input.Keys.RIGHT) -> moveRight()
                !player1 && Gdx.input.isKeyPressed(Input.Keys.A) -> moveLeft()
                !player1 && Gdx.input.isKeyPressed(Input.Keys.D) -> moveRight()
                else -> moveForward()
            }
        }
    }

    private fun moveLeft() {
        accelerateAtAngle(180f)
        addAction(Actions.rotateTo(1f, 1f))
    }

    private fun moveRight() {
        accelerateAtAngle(0f)
        addAction(Actions.rotateTo(-1f, 1f))
    }

    private fun moveForward() {
        addAction(Actions.rotateTo(0f, 1f))
    }
}