package no.sandramoen.blipblop.utils

interface GooglePlayServices {
    fun signIn()
    fun signOut()
    fun isSignedIn(): Boolean
    fun getLeaderboard()
    fun fetchHighScore()
    fun getHighScore(): Int
    fun submitScore(score: Int)
    fun incrementClassicAchievements()
    fun incrementChallengeAchievements()
    fun showAchievements()
}
