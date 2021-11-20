package no.sandramoen.blipblop.utils

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g3d.Environment
import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute
import com.badlogic.gdx.math.*
import java.lang.Exception
import java.util.ArrayList
import com.badlogic.gdx.math.Intersector.MinimumTranslationVector
import com.badlogic.gdx.math.collision.BoundingBox
import com.badlogic.gdx.math.Intersector
import com.badlogic.gdx.math.Vector3

open class BaseActor3D(x: Float, y: Float, z: Float, s: Stage3D) {
    private var modelData: ModelInstance?
    private var position: Vector3
    var rotation: Quaternion
    private var scale: Vector3
    protected var stage: Stage3D
    var collisionEnabled = true
    var id: Float = MathUtils.random(0f, 10_000f)

    private lateinit var boundingPolygon: Polygon

    protected var velocityVec: Vector2 = Vector2(0f, 0f)
    private var accelerationVec: Vector2 = Vector2(0f, 0f)
    private var acceleration: Float = 0f
    private var maxSpeed: Float = 1000f
    private var deceleration: Float = 0f

    init {
        modelData = null
        position = Vector3(x, y, z)
        rotation = Quaternion()
        scale = Vector3(1f, 1f, 1f)
        stage = s
        s.addActor(this)
    }

    fun setModelInstance(m: ModelInstance) { modelData = m }
    fun calculateTransform():Matrix4 { return Matrix4(position, rotation, scale)}

    fun setColor(c: Color) {
        for (m in modelData?.materials!!)
            m.set(ColorAttribute.createDiffuse(c))
    }

    fun loadTexture(filename: String) {
        val atlasRegion = BaseGame.textureAtlas!!.findRegion(filename)
        for (m in modelData?.materials!!)
            m.set(TextureAttribute.createDiffuse(atlasRegion))
    }

    open fun act(dt: Float) { modelData?.transform?.set(calculateTransform()) }
    fun draw(batch: ModelBatch, env: Environment) { batch.render(modelData, env) }

    fun getPosition():Vector3 { return position }
    fun setPosition(v: Vector3) { position.set(v) }
    fun moveBy(v: Vector3) { position.add(v) }
    fun moveBy(x: Float, y: Float, z: Float) { moveBy(Vector3(x, y, z)) }

    fun getTurnAngle(): Float { return rotation.getAngleAround(0f, -1f, 0f) }

    fun setTurnAngleZ(degrees: Float) { rotation.set(Quaternion(Vector3.Z, degrees)) }

    fun turnX(degrees: Float) { rotation.mul(Quaternion(Vector3.X, -degrees)) }
    fun turnZ(degrees: Float) { rotation.mul(Quaternion(Vector3.Z, -degrees)) }

    fun moveForward(dist: Float) { moveBy(rotation.transform(Vector3(0f, 0f, -1f)).scl(dist)) }
    fun moveUp(dist: Float) { moveBy(rotation.transform(Vector3(0f, 1f, 0f)).scl(dist)) }
    fun moveRight(dist: Float) { moveBy(rotation.transform(Vector3(1f, 0f, 0f)).scl(dist)) }
    fun setScale(x: Float, y: Float, z: Float) { scale.set(x, y, z) }

    // 2D collision detection
    fun setBaseRectangle() {
        val modelBounds = modelData?.calculateBoundingBox(BoundingBox())
        val max = modelBounds?.max
        val min = modelBounds?.min

        val vertices = floatArrayOf(max?.x!!, max.y, min?.x!!, max.y, min.x, min.y, max.x, min.y)
        boundingPolygon = Polygon(vertices)
        boundingPolygon.setOrigin(0f, 0f)
    }

    fun setBasePolygon() {
        val modelBounds = modelData?.calculateBoundingBox(BoundingBox())
        val max = modelBounds?.max
        val min = modelBounds?.min

        val a = 0.75f // offset amount.
        val vertices = floatArrayOf(
                max?.x!!,
                0f,
                a * max.x,
                a * max.y,
                0f,
                max.y,
                a * min!!.x,
                a * max.y,
                min.x,
                0f,
                a * min.x,
                a * min.y,
                0f,
                min.y,
                a * max.x,
                a * min.y
        )
        boundingPolygon = Polygon(vertices)
        boundingPolygon.setOrigin(0f, 0f)
    }

    fun getBoundaryPolygon(): Polygon {
        boundingPolygon.setPosition(position.x, position.y)
        boundingPolygon.rotation = getTurnAngle()
        boundingPolygon.setScale(scale.x, scale.y)
        return boundingPolygon
    }

    fun overlaps(other: BaseActor3D): Boolean {
        if (!collisionEnabled) return false
        val poly1 = this.getBoundaryPolygon()
        val poly2 = other.getBoundaryPolygon()

        // initial test to improve performance
        if (!poly1.boundingRectangle.overlaps(poly2.boundingRectangle))
            return false

        val mtv = MinimumTranslationVector()

        return Intersector.overlapConvexPolygons(poly1, poly2, mtv)
    }

    fun preventOverlap(other: BaseActor3D): Vector2? {
        val poly1 = this.getBoundaryPolygon()
        val poly2 = other.getBoundaryPolygon()

        // initial test to improve performance
        if (!poly1.boundingRectangle.overlaps(poly2.boundingRectangle))
            return null

        val mtv = MinimumTranslationVector()
        val polygonOverlap = Intersector.overlapConvexPolygons(poly1, poly2, mtv)

        if(!polygonOverlap)
            return null

        if (polygonOverlap)
            this.moveBy(mtv.normal.x * mtv.depth, mtv.normal.y * mtv.depth, 0f)
        // return Vector2(0f, -.1f)
        return mtv.normal
    }

    companion object {
        private lateinit var worldBounds: Rectangle

        fun setWorldBounds(width: Float, height: Float) { worldBounds = Rectangle(0f, 0f, width, height) }

        fun getList(stage: Stage3D, className: String): ArrayList<BaseActor3D> {
            val list = ArrayList<BaseActor3D>()

            var theClass: Class<*>? = null
            try { theClass = Class.forName(className) }
            catch (error: Exception) { error.printStackTrace() }

            for (ba3d in stage.getActors()) {
                if (theClass!!.isInstance(ba3d))
                    list.add(ba3d)
            }
            return list
        }

        fun count(stage: Stage3D, className: String):Int { return getList(stage, className).size }
    }

    // Physics ---------------------------------------------------------------------------------------------------
    fun setSpeed(speed: Float) {
        // If length is zero, then assume motion angle is zero degrees
        if (velocityVec.len() == 0f)
            velocityVec.set(speed, 0f)
        else
            velocityVec.setLength(speed)
    }

    fun getSpeed() = velocityVec.len()
    fun setMotionAngle(angle: Float): Vector2 = velocityVec.setAngle(angle)
    fun getMotionAngle() = velocityVec.angle()
    fun getVelocity() = velocityVec
    fun setVelocity(vel: Vector2) { velocityVec = vel }
    fun isMoving() = getSpeed() > 0

    fun setAcceleration(acc: Float) { acceleration = acc }
    fun accelerateAtAngle(angle: Float): Vector2 = accelerationVec.add( Vector2(acceleration, 0f).setAngleDeg(angle))
    fun accelerateForward(): Vector2 = accelerateAtAngle(getTurnAngle())
    fun setMaxSpeed(ms: Float) { maxSpeed = ms }
    fun setDeceleration(dec: Float) { deceleration = dec }

    fun remove() { stage.removeActor(this) }

    fun applyPhysics(dt: Float) {
        // apply acceleration
        velocityVec.add(accelerationVec.x * dt, accelerationVec.y * dt)

        var speed = getSpeed()

        // decrease speed (decelerate) when not accelerating
        if (accelerationVec.len() == 0f)
            speed-= deceleration * dt

        // keep speed within set bounds
        speed = MathUtils.clamp(speed, 0f, maxSpeed)

        // update velocity
        setSpeed(speed)

        // apply velocity
        moveBy(velocityVec.x* dt, velocityVec.y * dt, 0f)

        // reset acceleration
        accelerationVec.set(0f, 0f)
    }
}