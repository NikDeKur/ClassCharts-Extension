package dev.nikdekur.classcharts.ext.nav

import androidx.compose.runtime.Composable


class SimpleNavigator<Id : Any>(
    val screenFactory: (Id) -> Screen
) : Navigator<Id> {

    var currentScreenOrNull: Screen? = null

    override val currentScreen: Screen
        get() = currentScreenOrNull ?: error("No screen set")

    val screensMap = mutableMapOf<Id, Screen>()

    override val screens: Collection<Screen> = emptyList()


    @Composable
    override fun navigateTo(screenId: Id) {
        setScreen(getScreen(screenId))
    }

    @Composable
    fun setScreen(screen: Screen) {
        currentScreenOrNull = screen
        screen.render()
    }

    fun getScreen(id: Id): Screen {
        return screensMap.getOrPut(id) { screenFactory(id) }
    }
}