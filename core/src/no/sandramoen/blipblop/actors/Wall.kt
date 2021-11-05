package no.sandramoen.blipblop.actors

import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.VertexAttributes.Usage
import com.badlogic.gdx.graphics.g3d.Material
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder
import com.badlogic.gdx.math.Vector3
import no.sandramoen.blipblop.utils.BaseActor3D
import no.sandramoen.blipblop.utils.Stage3D

open class Wall(x: Float, y: Float, z: Float, s: Stage3D) : BaseActor3D(x, y, z, s) {
    init {
        val modelBuilder = ModelBuilder()
        val boxMaterial = Material()
        boxMaterial.set(BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA))
        val usageCode = Usage.Position + Usage.ColorPacked + Usage.Normal + Usage.TextureCoordinates
        val boxModel = modelBuilder.createBox(5f, 20f, .1f, boxMaterial, usageCode.toLong())
        val position = Vector3(0f, 0f, 0f)
        setModelInstance(ModelInstance(boxModel, position))
        setBaseRectangle()
        loadTexture("blankPixel")
    }
}