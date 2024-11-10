package dev.nikdekur.classcharts.ext.ui.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import dev.nikdekur.classcharts.ext.nav.Screen
import dev.nikdekur.classcharts.ext.ui.error.ExceptionScreen
import dev.nikdekur.classcharts.ext.ui.login.AuthenticationViewModel
import dev.nikdekur.classcharts.ext.ui.login.DataState

class ProfileScreen(
    val viewModel: AuthenticationViewModel
) : Screen {

    @Composable
    override fun render() {
        val state = viewModel.data.collectAsState()
        val dataState = state.value

        when (dataState) {
            is DataState.Loading -> CircularProgressIndicator()
            is DataState.Success -> {
                val data = dataState.data
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    ProfileCard(
                       data = data
                    )

                    LogoutButton(
                        onClick = { viewModel.logout() }
                    )
                }
            }
            is DataState.Error -> ExceptionScreen(dataState.error)
        }
    }
}