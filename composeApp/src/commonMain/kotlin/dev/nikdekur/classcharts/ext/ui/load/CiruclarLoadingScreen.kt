package dev.nikdekur.classcharts.ext.ui.load

import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import dev.nikdekur.classcharts.ext.nav.Screen

class CircularLoadingScreen : Screen {
    @Composable
    override fun render() {
        CircularProgressIndicator()
    }
}