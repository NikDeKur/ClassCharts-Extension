package dev.nikdekur.classcharts.ext.ui.error

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import dev.nikdekur.classcharts.ext.nav.Screen

class ExceptionScreen(
    val exception: Throwable
) : Screen {

    @Composable
    override fun render() {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Произошла ошибка",
                style = MaterialTheme.typography.titleLarge
            )

            Text(
                text = exception.message ?: "Неизвестная ошибка",
                style = MaterialTheme.typography.titleMedium
            )

            val scroll = rememberScrollState()

            Column(
                modifier = Modifier.verticalScroll(scroll)
            ) {
                Text(
                    text = exception.stackTraceToString(),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}