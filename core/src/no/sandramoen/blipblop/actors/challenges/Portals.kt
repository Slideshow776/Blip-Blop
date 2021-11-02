package no.sandramoen.blipblop.actors.challenges

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.VertexAttributes
import com.badlogic.gdx.graphics.g3d.Material
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.Array
import no.sandramoen.blipblop.actors.Ball
import no.sandramoen.blipblop.utils.BaseActor3D
import no.sandramoen.blipblop.utils.BaseGame
import no.sandramoen.blipblop.utils.Stage3D

class Portals(x: Float, y: Float, s: Stage, balls: Array<Ball>, s3D: Stage3D) : Challenge(x, y, s) {
    private var tag = "Portals"
    override var title = "Portals!"

    private val endTime = 30f
    private var balls = balls
    private var s3D = s3D
    private var time = 0f
    private var endFlag = false
    private var orangePortal: BaseActor3D
    private var bluePortal: BaseActor3D
    private var portalOffset = 0f

    private var portalScale = 0f
    private var portalOpen = false

    init {
        orangePortal = createPortal(Color(1f, 0.603f, 0f, 1f))
        bluePortal = createPortal(Color(0f, 0.635f, 1f, 1f))
    }

    override fun act(dt: Float) {
        super.act(dt)
        if (start && !finished) {
            // end challenge
            time += dt
            if (time >= endTime && !endFlag) {
                endFlag = true
                endChallenge()
            }

            // portals
            if (!portalOpen) {
                openPortal(orangePortal, dt)
                openPortal(bluePortal, dt)
            } else {
                for (ball in balls) { // collisions
                    if (ball.overlaps(orangePortal)) {
                        setPortalOffset(ball)
                        ball.setPosition(Vector3(bluePortal.getPosition().x, bluePortal.getPosition().y + portalOffset, ball.getPosition().z))
                        BaseGame.portal1Sound!!.play(BaseGame.soundVolume)
                    } else if (ball.overlaps(bluePortal)) {
                        setPortalOffset(ball)
                        ball.setPosition(Vector3(orangePortal.getPosition().x, orangePortal.getPosition().y + portalOffset, ball.getPosition().z))
                        BaseGame.portal2Sound!!.play(BaseGame.soundVolume)
                    }
                }
            }
        } else {
            closePortal(orangePortal, dt)
            closePortal(bluePortal, dt)
        }
    }

    override fun startChallengeLogic() {
        super.startChallengeLogic()
        orangePortal.moveBy(Vector3(0f, -0f, -50f))
        setPortalRandomPosition(orangePortal, top = true)
        orangePortal.setScale(0f, 0f, 0f)

        bluePortal.moveBy(Vector3(-0f, 0f, -50f))
        setPortalRandomPosition(bluePortal, top = false)
        bluePortal.setScale(0f, 0f, 0f)
    }

    override fun resetChallengeLogic() {
        orangePortal.setPosition(Vector3(0f, 0f, 50f))
        bluePortal.setPosition(Vector3(0f, 0f, 50f))
        time = 0f
        endFlag = false
        portalOffset = 0f
        portalOpen = false
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

    private fun setPortalRandomPosition(portal: BaseActor3D, top: Boolean) {
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
        portalScale += dt * .1f
        if (portalScale < 1f) {
            portal.setScale(portalScale, portalScale, portalScale)
        } else if (portalScale >= 1f) {
            portalOpen = true
        }
    }

    private fun closePortal(portal: BaseActor3D, dt: Float) {
        if (portalScale >= 0f) {
            portalScale -= dt * .4f
            if (portalScale < 0f) {
                portalScale = 0f
                portalOpen = false
            }
            portal.setScale(portalScale, portalScale, portalScale)
        }
    }
}