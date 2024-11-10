package dev.nikdekur.classcharts.ext.cc

import dev.nikdekur.classcharts.ClassChartsClient
import dev.nikdekur.classcharts.ConnectData
import dev.nikdekur.classcharts.KtorClassChartsClient
import dev.nikdekur.classcharts.exception.LoginFailedException
import dev.nikdekur.classcharts.exception.PingFailedException
import dev.nikdekur.classcharts.ext.Extension
import dev.nikdekur.classcharts.ext.ExtensionService
import dev.nikdekur.classcharts.ext.cc.ClassChartsService.LoginResult
import dev.nikdekur.ornament.Application
import dev.nikdekur.classcharts.ext.cc.ClassChartsService as IClassChartsService


class WebClassChartsService(
    override val app: Extension
) : ExtensionService(), IClassChartsService {

    private var clientOrNull: ClassChartsClient? = null
    override val client: ClassChartsClient
        get() = clientOrNull ?: error("Client is not initialized!")



    override suspend fun onDisable() {
        client.stop()
        clientOrNull = null
    }



    override suspend fun login(
        connectData: ConnectData
    ): LoginResult {
        try {
            clientOrNull = KtorClassChartsClient {
                this.connectData = connectData
            }

            client.start()
        } catch (e: Exception) {
            return LoginResult.Error.Unexpected(e)
        }

        val state = client.state
        if (state is Application.State.ErrorStarting) {
            val exception = state.error
            return when (exception) {
                is LoginFailedException ->
                    LoginResult.Error.LoginFailed(exception.message.toString(), exception.problem)
                is PingFailedException ->
                    LoginResult.Error.PingFailed(exception.message.toString(), exception.resetSession)
                else -> LoginResult.Error.Unexpected(state.error)
            }
        }

        return LoginResult.Success
    }

    override suspend fun logout() {
        onDisable()
    }
}