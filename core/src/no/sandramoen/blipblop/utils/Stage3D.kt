package no.sandramoen.blipblop.utils

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.PerspectiveCamera
import com.badlogic.gdx.graphics.g3d.Environment
import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight
import com.badlogic.gdx.math.Vector3
import java.util.ArrayList;
import com.badlogic.gdx.utils.viewport.FitViewport
import com.badlogic.gdx.utils.viewport.StretchViewport


class Stage3D {
    private var environment: Environment
    var camera: PerspectiveCamera
    var viewport: StretchViewport
    private var modelBatch: ModelBatch
    private var actorList: ArrayList<BaseActor3D>

    init {
        environment = Environment()
        environment.set(ColorAttribute(ColorAttribute.AmbientLight, .7f, .7f, .7f, 1f))

        val dlight = DirectionalLight()
        val lightColor = Color(.9f, .9f, .9f, 1f)
        val lightVector = Vector3(-1f, -.75f, -.25f)
        dlight.set(lightColor, lightVector)
        environment.add(dlight)

        camera = PerspectiveCamera(67f, Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())
        // camera = PerspectiveCamera(67f, 100f, 100f)
        camera.position.set(10f, 10f, 10f)
        camera.lookAt(0f, 0f, 0f)
        camera.near = .01f
        camera.far = 20f
        camera.update()

        viewport = StretchViewport(100f, 100f, camera)
        viewport.apply()
        camera.position.set(0f, 0f, 10f)

        modelBatch = ModelBatch()

        actorList = ArrayList()
    }

    fun act(dt: Float) {
        camera.update()
        for (ba in this.actorList)
            ba.act(dt)
    }

    fun draw() {
        modelBatch.begin(camera)
        for (ba in this.actorList)
            ba.draw(modelBatch, environment)
        modelBatch.end()
    }

    fun addActor(ba: BaseActor3D) { actorList.add(ba) }
    fun removeActor(ba: BaseActor3D) { actorList.remove(ba) }
    fun getActors(): ArrayList<BaseActor3D> { return actorList }
    fun setCameraPosition(x: Float, y: Float, z: Float) { camera.position.set(x, y, z) }
    fun setCameraPosition(v: Vector3) { camera.position.set(v) }

    fun setCameraDirection(v: Vector3) {
        camera.lookAt(v)
        camera.up.set(0f, 1f, 0f)
    }

    fun setCameraDirection(x: Float, y: Float, z: Float) { setCameraDirection(Vector3(x, y, z)) }
}