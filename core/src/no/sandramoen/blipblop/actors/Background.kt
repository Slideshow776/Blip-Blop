package no.sandramoen.blipblop.actors

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.VertexAttributes
import com.badlogic.gdx.graphics.g3d.Material
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder
import com.badlogic.gdx.math.Vector3
import no.sandramoen.blipblop.utils.BaseActor3D
import no.sandramoen.blipblop.utils.Stage3D

open class Background (x: Float = 0f, y: Float = 0f, z: Float = 0f, s: Stage3D) : BaseActor3D(x, y, z, s) {
    init {
        val modelBuilder = ModelBuilder()
        val boxMaterial = Material()
        val usageCode = VertexAttributes.Usage.Position + VertexAttributes.Usage.ColorPacked + VertexAttributes.Usage.Normal + VertexAttributes.Usage.TextureCoordinates
        val boxModel = modelBuilder.createBox(14.3f, 14.3f, .5f, boxMaterial, usageCode.toLong())
        val position = Vector3(0f, 0f, 0f)
        setModelInstance(ModelInstance(boxModel, position))
        setBaseRectangle()
        setColor(Color.BLACK)
    }
}
