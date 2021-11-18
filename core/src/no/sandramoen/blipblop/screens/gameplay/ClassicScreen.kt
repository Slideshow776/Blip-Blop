package no.sandramoen.blipblop.screens.gameplay

class ClassicScreen :  LevelScreen() {
    private val tag = "ClassicScreen"

    override fun initialize() {
        super.initialize()
        startAchievements(true)
    }
}
