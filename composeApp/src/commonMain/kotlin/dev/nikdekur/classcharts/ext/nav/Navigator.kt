@file:Suppress("NOTHING_TO_INLINE")

package dev.nikdekur.classcharts.ext.nav

import androidx.compose.runtime.Composable

interface Navigator<Id : Any> {

    val screens: Collection<Screen>
    val currentScreen: Screen

    @Composable
    fun navigateTo(screenId: Id)
}