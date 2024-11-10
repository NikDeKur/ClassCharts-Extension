package dev.nikdekur.classcharts.ext.ui.detention

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import dev.nikdekur.classcharts.ext.nav.Screen

class DetentionsScreen(
    val viewModel: DetentionsViewModel
) : Screen {

    @Composable
    override fun render() {
        Text("Detentions")
    }
}