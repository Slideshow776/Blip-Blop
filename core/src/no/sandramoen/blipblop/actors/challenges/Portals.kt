package no.sandramoen.blipblop.actors.challenges

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.VertexAttributes
import com.badlogic.gdx.graphics.g3d.Material
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.Array
import no.sandramoen.blipblop.actors.Ball
import no.sandramoen.blipblop.actors.particleEffects.BluePortalEffect
import no.sandramoen.blipblop.actors.particleEffects.OrangePortalEffect
import no.sandramoen.blipblop.actors.particleEffects.ParticleActor
import no.sandramoen.blipblop.utils.BaseActor3D
import no.sandramoen.blipblop.utils.BaseGame
import no.sandramoen.blipblop.utils.GameUtils
import no.sandramoen.blipblop.utils.Stage3D

class Portals(x: Float, y: Float, s: Stage, balls: Array<Ball>, s3D: Stage3D) : Challenge(x, y, s) {
    private var tag = "Portals"
    override var title = BaseGame.myBundle!!.get("portals")

    private val endTime = 40f
    private var balls = balls
    private var s3D = s3D
    private var time = 0f
    private var endFlag = false
    private var orangePortal: BaseActor3D
    private var bluePortal: BaseActor3D
    private var portalOffset = 0f

    private var portalScale = 0f
    private var portalOpen = false

    private var bluePortalEffect: ParticleActor
    private var orangePortalEffect: ParticleActor

    private var isLooping = false
    private var loopTime = 0f
    private val loopThreshold = 3f
    private var portalOpenDelay = 0f
    private val portalOpenDelayThreshold = 2f
    private var orangePortalLastBallVelocity = Vector2(0f, 0f)
    private var bluePortalLastBallVelocity = Vector2(0f, 0f)

    init {
        orangePortal = createPortal(Color(1f, 0.603f, 0f, 1f))
        bluePortal = createPortal(Color(0f, 0.635f, 1f, 1f))

        bluePortalEffect = BluePortalEffect()
        orangePortalEffect = OrangePortalEffect()
    }

    override fun act(dt: Float) {
        super.act(dt)
        if (start && !finished) {
            // end challenge
            time += dt
            if (time >= endTime && !endFlag) {
                endFlag = true
                clearActions() // TODO: hard to replicate this bug, hope this fixes is. Possible duplicate bug in other challenges.
                endChallenge()
            }

            // loop detection
            if (isLooping && loopTime <= loopThreshold) {
                loopTime += dt
            } else if (isLooping && portalOpen) {
                closePortal(orangePortal, true, dt)
                closePortal(bluePortal, false, dt)
            } else if (isLooping && !portalOpen) {
                setPortalsRandomPosition(orangePortal, top = true)
                setPortalsRandomPosition(bluePortal, top = false)
                loopTime = 0f
                isLooping = false
            }

            // portals
            if (!portalOpen) {
                openPortal(orangePortal, dt)
                openPortal(bluePortal, dt)
            } else if (portalOpen && loopTime <= loopThreshold) {
                for (ball in balls) { // collisions
                    if (ball.overlaps(orangePortal)) {
                        // loop detection
                        if (orangePortalLastBallVelocity == ball.getVelocity()) isLooping = true
                        orangePortalLastBallVelocity = Vector2(ball.getVelocity().x, ball.getVelocity().y)

                        // ball
                        setPortalOffset(ball)
                        ball.setPosition(Vector3(bluePortal.getPosition().x, bluePortal.getPosition().y + portalOffset, ball.getPosition().z))

                        // miscellaneous
                        BaseGame.portal1Sound!!.play(BaseGame.soundVolume * .5f)
                    } else if (ball.overlaps(bluePortal)) {
                        // loop detection
                        if (bluePortalLastBallVelocity == ball.getVelocity()) isLooping = true
                        bluePortalLastBallVelocity = Vector2(ball.getVelocity().x, ball.getVelocity().y)

                        // ball
                        setPortalOffset(ball)
                        ball.setPosition(Vector3(orangePortal.getPosition().x, orangePortal.getPosition().y + portalOffset, ball.getPosition().z))

                        // miscellaneous
                        BaseGame.portal2Sound!!.play(BaseGame.soundVolume * .5f)
                    }
                }
            }
        } else {
            closePortal(orangePortal, true, dt)
            closePortal(bluePortal, false, dt)
        }
    }

    override fun startChallengeLogic() {
        super.startChallengeLogic()
        orangePortal.moveBy(Vector3(0f, -0f, -50f))
        setPortalsRandomPosition(orangePortal, top = true)
        orangePortal.setScale(0f, 0f, 0f)

        bluePortal.moveBy(Vector3(-0f, 0f, -50f))
        setPortalsRandomPosition(bluePortal, top = false)
        bluePortal.setScale(0f, 0f, 0f)
    }

    override fun resetChallengeLogic() {
        orangePortal.setPosition(Vector3(0f, 0f, 50f))
        bluePortal.setPosition(Vector3(0f, 0f, 50f))
        time = 0f
        endFlag = false
        portalOffset = 0f
        portalOpen = false
        portalOpenDelay = 0f
        isLooping = false
        loopTime = 0f
        portalOpenDelay = 0f
        orangePortalLastBallVelocity = Vector2(0f, 0f)
        bluePortalLastBallVelocity = Vector2(0f, 0f)
    }

    private fun createPortal(color: Color): BaseActor3D {
        // 3D model
        val modelBuilder = ModelBuilder()
        val boxMaterial = Material()
        boxMaterial.set(BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA))
        val usageCode = VertexAttributes.Usage.Position + VertexAttributes.Usage.ColorPacked + VertexAttributes.Usage.Normal + VertexAttributes.Usage.TextureCoordinates
        val boxModel = modelBuilder.createBox(3f, .2f, .1f, boxMaterial, usageCode.toLong())
        val position = Vector3(0f, 0f, 0f)
        val baseActor = BaseActor3D(0f, 0f, 0f, s3D)
        baseActor.setModelInstance(ModelInstance(boxModel, position))
        baseActor.setBaseRectangle()
        baseActor.moveBy(Vector3(0f, 0f, 50f))

        // miscellaneous
        baseActor.loadTexture("whitePixel")
        baseActor.setColor(color)
        return baseActor
    }

    private fun setPortalOffset(ball: Ball) {
        if (ball.getVelocity().y <= 0) portalOffset = -.25f
        else if (ball.getVelocity().y > 0) portalOffset = .25f
    }

    private fun setPortalsRandomPosition(portal: BaseActor3D, top: Boolean) {
        if (top)
            portal.setPosition(Vector3(
                    MathUtils.random(-5f, 5f),
                    MathUtils.random(0f, 3f),
                    0f
            ))
        else
            portal.setPosition(Vector3(
                    MathUtils.random(-5f, 5f),
                    MathUtils.random(-3f, 0f),
                    0f
            ))
    }

    private fun openPortal(portal: BaseActor3D, dt: Float) {
        portalOpenDelay += dt
        if (portalOpenDelay > portalOpenDelayThreshold) {
            portalScale += dt * .2f
            if (portalScale < 1f) {
                portal.setScale(portalScale, portalScale, portalScale)
            } else if (portalScale >= 1f) {
                portalOpen = true
                BaseGame.portalWorkingSound!!.play(BaseGame.soundVolume)
                startPortalParticleEffects(orangePortal, orangePortal = true)
                startPortalParticleEffects(bluePortal, orangePortal = false)
                portalOpenDelay = 0f
            }
        }
    }

    private fun closePortal(portal: BaseActor3D, orangePortal: Boolean, dt: Float) {
        if (orangePortal) orangePortalEffect.stop()
        else bluePortalEffect.stop()
        if (portalScale >= 0f) {
            portalScale -= dt * .5f
            if (portalScale < 0f) {
                portalScale = 0f
                portalOpen = false
            }
            portal.setScale(portalScale, portalScale, portalScale)
        }
    }

    private fun startPortalParticleEffects(portal: BaseActor3D, orangePortal: Boolean) {
        val portalPosition2D = Vector2(
                GameUtils.normalizeValues(portal.getPosition().x, -9.1f, 9.1f) * 100 - 2.0f / 2,
                GameUtils.normalizeValues(portal.getPosition().y, -6.5f, 6.5f) * 100
        )

        if (orangePortal) {
            orangePortalEffect.setScale(Gdx.graphics.height * .00004f)
            orangePortalEffect.setPosition(portalPosition2D.x, portalPosition2D.y)
            stage.addActor(orangePortalEffect)
            orangePortalEffect.start()
        } else {
            bluePortalEffect.setScale(Gdx.graphics.height * .00004f)
            bluePortalEffect.setPosition(portalPosition2D.x, portalPosition2D.y)
            stage.addActor(bluePortalEffect)
            bluePortalEffect.start()
        }
    }
}
