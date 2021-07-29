package no.sandramoen.blipblop.actors

import com.badlogic.gdx.Application
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input.Keys
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.utils.Align
import no.sandramoen.blipblop.utils.BaseActor

class Player(s: Stage, bottomPlayer: Boolean) : BaseActor(0f, 0f, s) {
    private val tag = "Player"
    private val touchDeadZone = .25f
    private var activationDelayForAI = 5f
    private var enableAIWithDelay = Actions.sequence(
            Actions.delay(activationDelayForAI),
            Actions.run {
                enableAI = true
                setSpeed(getSpeed() / 2)
            }
    )
    private var paddleCenter = 0f
    private var androidSpeed = 30f
    private var shadowBall: Ball
    private var topPlayerYPosition = 85f
    private var bottomPlayerYPosition = 15f
    private var aiSpeed = 5f

    val bottomPlayer: Boolean = bottomPlayer
    var touchX = 0f
    var enableAI = true
    var aiMovementDisabled = false
    var aiShouldMoveToX = -10f
    var score = 0
    var miss = 0
    var hit = 0

    init {
        // miscellaneous
        setSize(15.5f, 2.75f)
        if (bottomPlayer) setPosition(50f - width / 2, bottomPlayerYPosition)
        else setPosition(50f - width / 2, topPlayerYPosition)
        setOrigin(Align.center)
        paddleCenter = x + width / 2
        touchX = paddleCenter
        shadowBall = Ball(this.stage, shadowBall = true)
        shadowBall.debug = true
        shadowBall.setVelocity(Vector2(0f, 0f))
        shadowBall.setPosition(-10f, -10f)

        // physics
        setAcceleration(Gdx.graphics.width * 3f) // pixels/seconds
        setMaxSpeed(Gdx.graphics.width * .1f) // .07f // .25f
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

        // controls
        paddleCenter = x + width / 2
        if (Gdx.app.type == Application.ApplicationType.Android && !enableAI) {
            when {
                touchX > paddleCenter + touchDeadZone -> moveRight()
                touchX < paddleCenter - touchDeadZone -> moveLeft()
                else -> standStill()
            }
        } else if (Gdx.app.type == Application.ApplicationType.Desktop) {
            when {
                bottomPlayer && Gdx.input.isKeyPressed(Keys.LEFT) -> moveLeft()
                bottomPlayer && Gdx.input.isKeyPressed(Keys.RIGHT) -> moveRight()
                !bottomPlayer && Gdx.input.isKeyPressed(Keys.A) -> moveLeft()
                !bottomPlayer && Gdx.input.isKeyPressed(Keys.D) -> moveRight()
                else -> standStill()
            }
        }

        // miscellaneous
        if (bottomPlayer && shadowBall.y < bottomPlayerYPosition && shadowBall.y != -10f) {
            aiShouldMoveToX = shadowBall.x
            shadowBall.setVelocity(Vector2(0f, 0f))
            shadowBall.setPosition(-10f, -10f)
        } else if (!bottomPlayer && shadowBall.y > topPlayerYPosition && shadowBall.y != -10f) {
            aiShouldMoveToX = shadowBall.x + shadowBall.width / 2 // centered shadow ball destination
            shadowBall.setVelocity(Vector2(0f, 0f))
            shadowBall.setPosition(-10f, -10f)
        }

        if (enableAI) playAsAi()
    }

    fun ballImpactAnimation() {
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
        addAction(Actions.parallel(
                bounceAction,
                Actions.sequence(
                        Actions.scaleBy(.1f, -.1f, duration * 2),
                        Actions.scaleTo(1f, 1f, duration * 2)
                )
        ))
    }

    fun enableAIWithDelay() {
        enableAIWithDelay = Actions.sequence(
                Actions.delay(activationDelayForAI),
                Actions.run {
                    enableAI = true
                    setMaxSpeed((Gdx.graphics.width * 0.25f) / 2)
                }
        )
        if (!actions.contains(enableAIWithDelay))
            addAction(enableAIWithDelay)
    }

    fun disableAI() {
        enableAI = false
        setMaxSpeed(Gdx.graphics.width * 0.25f)
        if (actions.contains(enableAIWithDelay))
            actions.removeValue(enableAIWithDelay, true)
    }

    fun spawnShadowBall(ball: Ball) { // https://www.rharel.com/projects/pong-ai
        shadowBall.debug = false // convenient debug option
        shadowBall.setPosition(ball.x, ball.y)
        shadowBall.setVelocity(Vector2(
                ball.getVelocity().x * 100,
                ball.getVelocity().y * 100
        ))
    }

    private fun playAsAi() { // an AI needs to know where the ball is
        var temp = if (aiShouldMoveToX > 0f) aiShouldMoveToX
        else 50f
        when {
            temp > paddleCenter + width / 4 -> moveRight(temp)
            temp < paddleCenter - width / 4 -> moveLeft(temp)
            else -> standStill()
        }
    }

    private fun moveRight(incomingX: Float = touchX) {
        if (enableAI) {
            accelerateAtAngle(0f)
            /*x += ((incomingX - paddleCenter) / 100) * aiSpeed
            if (x + width > 100f) x = 100f - width // world bounds*/
        } else if (Gdx.app.type == Application.ApplicationType.Android) {
            x += ((incomingX - paddleCenter) / 100) * androidSpeed
            if (x + width > 100f) x = 100f - width // world bounds
        } else if (Gdx.app.type == Application.ApplicationType.Desktop)
            accelerateAtAngle(0f)
        addAction(Actions.rotateTo(-2f, .75f))
    }

    private fun moveLeft(incomingX: Float = touchX) {
        if (enableAI) {
            accelerateAtAngle(180f)
            /*x -= ((paddleCenter - incomingX) / 100) * aiSpeed
            if (x < 0) x = 0f // world bounds*/
        } else if (Gdx.app.type == Application.ApplicationType.Android) {
            x -= ((paddleCenter - incomingX) / 100) * androidSpeed
            if (x < 0) x = 0f // world bounds
        } else if (Gdx.app.type == Application.ApplicationType.Desktop)
            accelerateAtAngle(180f)
        addAction(Actions.rotateTo(2f, .75f))
    }

    private fun standStill() {
        addAction(Actions.rotateTo(0f, .5f))
    }
}
