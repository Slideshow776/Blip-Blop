package no.sandramoen.blipblop.utils

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input.Keys
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.viewport.*

abstract class BaseScreen : Screen, InputProcessor {
    protected var mainStage: Stage
    protected var uiStage: Stage
    private var transitionStage: Stage
    protected var uiTable: Table
    protected var camera: OrthographicCamera
    var transition: Transition

    init {
        mainStage = Stage()
        uiStage = Stage()
        transitionStage = Stage()

        uiTable = Table()
        uiTable.setFillParent(true)
        uiStage.addActor(uiTable)

        camera = mainStage.camera as OrthographicCamera

        mainStage.viewport = StretchViewport(BaseGame.WORLD_WIDTH, BaseGame.WORLD_HEIGHT, camera)
        mainStage.viewport.apply()

        camera.position.set(camera.viewportWidth / 2f, camera.viewportHeight / 2f, 0f)
        // Gdx.input.inputProcessor = this // TODO: do I need this?

        transitionStage.viewport = StretchViewport(BaseGame.WORLD_WIDTH, BaseGame.WORLD_HEIGHT, camera)
        transitionStage.viewport.apply()
        transition = Transition(0f, 0f, transitionStage)

        initialize()
    }

    abstract fun initialize()
    abstract fun update(dt: Float)

    override fun render(dt: Float) {
        // act methods
        transitionStage.act(dt)
        uiStage.act(dt)
        mainStage.act(dt)

        // defined by user
        update(dt)

        camera.update()

        // clear the screen
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        this.mainStage.batch.projectionMatrix = camera.combined

        // draw the graphics
        mainStage.draw()
        uiStage.draw()
        transitionStage.draw()
    }

    override fun show() {
        val im: InputMultiplexer = Gdx.input.inputProcessor as InputMultiplexer
        Gdx.input.setCatchKey(Keys.BACK, true)
        im.addProcessor(this)
        im.addProcessor(uiStage)
        im.addProcessor(mainStage)
    }

    override fun hide() {
        val im: InputMultiplexer = Gdx.input.inputProcessor as InputMultiplexer
        im.removeProcessor(this)
        im.removeProcessor(uiStage)
        im.removeProcessor(mainStage)
    }

    override fun resize(width: Int, height: Int) {
        mainStage.viewport.update(width, height);
        camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0f)
    }

    override fun pause() {}
    override fun resume() {}
    override fun dispose() {}

    // methods required by InputProcessor interface
    override fun keyDown(keycode: Int): Boolean {
        return false
    }

    override fun keyUp(keycode: Int): Boolean {
        return false
    }

    override fun keyTyped(character: Char): Boolean {
        return false
    }

    override fun mouseMoved(screenX: Int, screenY: Int): Boolean {
        return false
    }

    override fun scrolled(amountX: Float, amountY: Float): Boolean {
        return false
    }

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        // To translate pixel to world units:
        // worldCoordinates = camera.unproject(Vector3(screenX.toFloat(), screenY.toFloat(),0f))
        return false
    }

    override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean {
        return false
    }

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        return false
    }
}