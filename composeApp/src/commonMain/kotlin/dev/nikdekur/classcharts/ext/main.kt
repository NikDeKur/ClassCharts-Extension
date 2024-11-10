@file:OptIn(ExperimentalComposeUiApi::class)

package dev.nikdekur.classcharts.ext

import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.window.CanvasBasedWindow
import dev.nikdekur.classcharts.ext.ui.App
import dev.nikdekur.classcharts.ext.ui.TimeProvider
import dev.nikdekur.classcharts.ext.ui.login.AuthenticationViewModel
import dev.nikdekur.classcharts.ext.ui.theme.ClassChartsExtensionTheme
import dev.nikdekur.ornament.environment.Environment
import kotlinx.coroutines.delay
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import org.jetbrains.skiko.wasm.onWasmReady

fun main() {
    onWasmReady {
        // Default styles required to remove padding (white is added by default)
        CanvasBasedWindow(
            canvasElementId = "root",
            requestResize = {
                delay(1)
                IntSize(
                    width = 420,
                    height = 539
                )
            }
        ) {
            MainApp()
        }
    }
}

@Composable
fun MainApp() {
    var ext by rememberField<Extension?> { null }

    val time = object : TimeProvider {
        override val clock: Clock =
            // OffsetClock(Clock.System, (-5).days)
            Clock.System
        override val timeZone: TimeZone = TimeZone.currentSystemDefault()
    }

    // Создаём Extension асинхронно через LaunchedEffect
    LaunchedEffect(Unit) {
        ext = ClassChartsExtension(
            Environment.Empty,
            time
        ).also {
            it.init()
            it.start()
        }
    }

    val extension = ext
    if (extension != null) {
        val viewModel = AuthenticationViewModel(extension)
        println("CREATED AUTH VIEW MODEL")
        ClassChartsExtensionTheme(
            darkTheme = true
        ) {
            App(extension, viewModel)
        }
    } else {
        CircularProgressIndicator()
    }
}