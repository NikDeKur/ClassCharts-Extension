package dev.nikdekur.classcharts.ext.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.nikdekur.classcharts.StudentCode
import dev.nikdekur.classcharts.ext.Extension
import dev.nikdekur.classcharts.ext.ExtensionComponent
import dev.nikdekur.classcharts.ext.login.LoginService
import dev.nikdekur.classcharts.ext.login.LoginService.LoginResult
import dev.nikdekur.classcharts.pupil.PupilData
import dev.nikdekur.ndkore.service.inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate

class AuthenticationViewModel(override val app: Extension) : ViewModel(), ExtensionComponent {

    val loginService: LoginService by inject()

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Loading)
    val loginState: StateFlow<LoginState> = _loginState

    private val _data = MutableStateFlow<DataState>(DataState.Loading)
    val data: StateFlow<DataState> get() = _data


    fun login(code: StudentCode, dob: LocalDate) {
        viewModelScope.launch {
            val result = loginService.login(code, dob)

            if (result is LoginResult.Error) {
                _loginState.value = LoginState.Error(result)
            } else {
                _loginState.value = LoginState.LoggedIn
            }
        }
    }

    fun trySavedLogin() {
        viewModelScope.launch {
            val success = loginService.trySavedLogin()
            if (success) {
                _loginState.value = LoginState.LoggedIn
            } else {
                _loginState.value = LoginState.LoggedOut
            }
        }
    }

    fun loadProfile() {
        viewModelScope.launch {
            try {
                val pupilData = loginService.getPupilData()
                _data.value = DataState.Success(pupilData)
            } catch (e: Exception) {
                _data.value = DataState.Error(e)
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            loginService.logout()
            _loginState.value = LoginState.LoggedOut
        }
    }


}

// Состояние экрана
sealed class LoginState {
    object Loading : LoginState()
    object LoggedOut : LoginState()
    object LoggedIn : LoginState()
    data class Error(val result: LoginResult.Error) : LoginState()
}

// Классы состояния данных
sealed class DataState {
    object Loading : DataState()
    data class Success(val data: PupilData) : DataState()
    data class Error(val error: Throwable) : DataState()
}