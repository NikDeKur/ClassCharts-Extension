package dev.nikdekur.classcharts.ext.login

import dev.nikdekur.classcharts.StudentCode
import dev.nikdekur.classcharts.pupil.PupilData
import kotlinx.datetime.LocalDate

interface LoginService {

    suspend fun getPupilData(): PupilData

    suspend fun trySavedLogin(): Boolean
    suspend fun login(code: StudentCode, dob: LocalDate): LoginResult
    suspend fun logout()


    sealed interface LoginResult {
        object Success : LoginResult

        sealed interface Error : LoginResult {
            data class Unexpected(val exception: Throwable): Error
            data class InvalidStudentCode(val code: StudentCode) : Error
            data class InvalidDateOfBirth(val dob: LocalDate) : Error
        }
    }
}