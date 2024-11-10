package dev.nikdekur.classcharts.ext.cc

import dev.nikdekur.classcharts.ClassChartsClient
import dev.nikdekur.classcharts.ConnectData
import dev.nikdekur.classcharts.exception.LoginFailedException

interface ClassChartsService {

    val client: ClassChartsClient

    suspend fun login(data: ConnectData): LoginResult
    suspend fun logout()

    sealed interface LoginResult {
        object Success : LoginResult

        sealed interface Error : LoginResult {
            data class Unexpected(val exception: Throwable): Error
            data class LoginFailed(val message: String, val problem: LoginFailedException.Problem) : Error
            data class PingFailed(val message: String, val resetSession: Boolean) : Error
        }
    }
}