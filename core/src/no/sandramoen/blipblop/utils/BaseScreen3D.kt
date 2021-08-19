package no.sandramoen.blipblop.utils

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.viewport.StretchViewport

abstract class BaseScreen3D : Screen, InputProcessor {
    protected var mainStage3D: Stage3D
    protected var uiStage: Stage
    protected var uiTable: Table
    protected var foreground2DStage: Stage
    protected var camera: OrthographicCamera

    init {
        mainStage3D = Stage3D()
        uiStage = Stage()
        foreground2DStage = Stage()

        // foreground
        camera = foreground2DStage.camera as OrthographicCamera
        foreground2DStage.viewport = StretchViewport(BaseGame.WORLD_WIDTH, BaseGame.WORLD_HEIGHT, camera)
        foreground2DStage.viewport.apply()
        camera.position.set(camera.viewportWidth / 2f, camera.viewportHeight / 2f, 0f)

        // ui
        uiTable = Table()
        uiTable.setFillParent(true)
        // uiTable.debug = true
        uiStage.addActor(uiTable)

        initialize()
    }

    abstract fun initialize()
    abstract fun update (dt: Float)

    // gameloop method
    override fun render(dt: Float) {
        // limit amount of time that can pass while window is being dragged
        var delta = Math.min(dt, 1/30f)

        // act methods
        uiStage.act(delta)
        foreground2DStage.act(delta)
        mainStage3D.act(delta)

        // defined by game-specific classes
        update(delta)

        camera.update()

        // clear the screen
        Gdx.gl.glClearColor(.5f, .5f, .5f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT + GL20.GL_DEPTH_BUFFER_BIT)

        this.foreground2DStage.batch.projectionMatrix = camera.combined

        // draw the graphics
        mainStage3D.draw()
        foreground2DStage.draw()
        uiStage.draw()
    }

    override fun resize(width: Int, height: Int) {
        foreground2DStage.viewport.update(width, height, true)
        uiStage.viewport.update(width, height, true)
        mainStage3D.viewport.update(width, height)
        mainStage3D.camera.position.set(0f, 0f, 10f)
        camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0f);
    }

    override fun pause() {}
    override fun resume() {}
    override fun dispose() {}

    override fun show() {
        val im = Gdx.input.inputProcessor as InputMultiplexer
        im.addProcessor(this)
        im.addProcessor(uiStage)
    }

    override fun hide() {
        val im = Gdx.input.inputProcessor as InputMultiplexer
        im.removeProcessor(this)
        im.removeProcessor(uiStage)
    }

    override fun keyDown(keycode: Int): Boolean { return false}
    override fun keyUp(keycode: Int): Boolean { return false }
    override fun keyTyped(character: Char): Boolean { return false }
    override fun mouseMoved(screenX: Int, screenY: Int): Boolean { return false }
    /*override fun scrolled(amount: Int): Boolean { return false }*/
    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean { return false }
    override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean { return false }
    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean { return false }
}