package dev.nikdekur.classcharts.ext.login

import dev.nikdekur.classcharts.ConnectData
import dev.nikdekur.classcharts.StudentCode
import dev.nikdekur.classcharts.exception.LoginFailedException.Problem
import dev.nikdekur.classcharts.ext.Extension
import dev.nikdekur.classcharts.ext.ExtensionService
import dev.nikdekur.classcharts.ext.cc.ClassChartsService
import dev.nikdekur.classcharts.ext.login.LoginService.LoginResult
import dev.nikdekur.classcharts.pupil.PupilData
import dev.nikdekur.ndkore.service.dependencies
import dev.nikdekur.ndkore.service.inject
import kotlinx.browser.localStorage
import kotlinx.datetime.LocalDate

class ClassChartsLoginService(
    override val app: Extension
) : ExtensionService(), LoginService {

    override val dependencies = dependencies {
        +ClassChartsService::class
    }

    val ccService: ClassChartsService by inject()



    fun getSavedConnectData(): ConnectData? {
        val ccSession = localStorage.getItem("cc-session") ?: return null
        return ConnectData {
            sessionId = ccSession
            onSessionIdChange(::setSavedConnectData)
        }
    }

    fun setSavedConnectData(sessionId: String) {
        localStorage.setItem("cc-session", sessionId)
    }

    fun clearSavedConnectData() {
        localStorage.removeItem("cc-session")
    }

    override suspend fun getPupilData(): PupilData {
        return ccService.client.pupilData
    }

    override suspend fun trySavedLogin(): Boolean {
        val connectData = getSavedConnectData() ?: return false
        val result = ccService.login(connectData)

        return when (result) {
            is ClassChartsService.LoginResult.Success -> true
            is ClassChartsService.LoginResult.Error.PingFailed -> false
            else -> throw IllegalStateException("Unexpected result: $result")
        }.also {

            // Clear saved data if login failed
            // Don't clear if an exception was thrown
            if (!it)
                clearSavedConnectData()
        }
    }

    override suspend fun login(
        code: StudentCode,
        dob: LocalDate
    ): LoginResult {

        val data = ConnectData {
            studentCode = code
            dateOfBirth = dob
            onSessionIdChange(::setSavedConnectData)
        }

        val result = ccService.login(data)

        return when (result) {
            is ClassChartsService.LoginResult.Success -> LoginResult.Success

            is ClassChartsService.LoginResult.Error ->
                when (result) {
                    is ClassChartsService.LoginResult.Error.LoginFailed -> {
                        val problem = result.problem
                        when (problem) {
                            Problem.INVALID_STUDENT_CODE -> LoginResult.Error.InvalidStudentCode(code)
                            Problem.INVALID_DATE_OF_BIRTH -> LoginResult.Error.InvalidDateOfBirth(dob)
                            Problem.UNKNOWN -> unexpected(result)
                        }
                    }
                    else -> unexpected(result)
                }
        }
    }

    override suspend fun logout() {
        clearSavedConnectData()
        ccService.logout()
    }
}

class UnexpectedResultException(
    val result: ClassChartsService.LoginResult.Error
) : RuntimeException("Unexpected result: $result")

internal fun unexpected(result: ClassChartsService.LoginResult.Error): LoginResult.Error.Unexpected {
    return LoginResult.Error.Unexpected(UnexpectedResultException(result))
}