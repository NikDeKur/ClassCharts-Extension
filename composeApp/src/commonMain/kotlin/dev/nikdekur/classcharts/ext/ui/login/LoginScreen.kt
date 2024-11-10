@file:Suppress("NOTHING_TO_INLINE")

package dev.nikdekur.classcharts.ext.ui.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import dev.nikdekur.classcharts.ext.login.LoginService
import dev.nikdekur.classcharts.ext.nav.Screen
import dev.nikdekur.classcharts.ext.rememberField
import kotlinx.datetime.LocalDate
import kotlinx.datetime.format.Padding
import kotlinx.datetime.format.char

val DATE_TIME_FORMAT = LocalDate.Format {
    dayOfMonth(Padding.NONE)
    char('-')
    monthNumber(Padding.NONE)
    char('-')
    year(Padding.NONE)
}


val STUDENT_CODE_WRITING_REGEX = Regex("^[a-zA-Z0-9]{0,10}\$")
inline fun isValidStudentCode(code: String): Boolean {
    return STUDENT_CODE_WRITING_REGEX.matches(code)
}


val DATE_OF_BIRTH_WRITING_REGEX = Regex("^[0-9]{0,2}-?[0-9]{0,2}-?[0-9]{0,4}\$")
inline fun isValidDateOfBirth(date: String): Boolean {
    return DATE_OF_BIRTH_WRITING_REGEX.matches(date)
}


class LoginScreen(
    val viewModel: AuthenticationViewModel
) : Screen {

    @Composable
    override fun render() {
        var studentCode by rememberField { "" }
        var dateOfBirth by rememberField { "" }
        var dateError by rememberField { false }

        fun onClick() {
            try {
                val parsedDate = LocalDate.parse(dateOfBirth, DATE_TIME_FORMAT) // Преобразование строки в LocalDate
                if (studentCode.length == 10)
                    viewModel.login(studentCode, parsedDate)

            } catch (_: IllegalArgumentException) {
                dateError = true // Показываем ошибку при неправильном формате даты
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val error = viewModel.loginState.value as? LoginState.Error
            if (error != null) {
                val error = error.result
                val errorMessage = when (error) {
                    is LoginService.LoginResult.Error.InvalidDateOfBirth -> "Invalid date of birth `${error.dob}`"
                    is LoginService.LoginResult.Error.InvalidStudentCode -> "Invalid student code `${error.code}`"
                    is LoginService.LoginResult.Error.Unexpected -> throw error.exception
                }

                Text(text = errorMessage, color = MaterialTheme.colorScheme.error)
                Spacer(modifier = Modifier.height(8.dp))
            }


            // Student Code input field
            TextField(
                value = studentCode,
                onValueChange = {
                    if (isValidStudentCode(it))
                        studentCode = it.uppercase()
                },
                label = { Text("Student Code") },
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Ascii,
                    imeAction = ImeAction.Next
                ),
                isError = studentCode.length != 10
            )



            Spacer(modifier = Modifier.height(2.dp))

            if (studentCode.length != 10) {
                Text(
                    text = "Student code must be exactly 10 characters (only digits and letters)",
                    color = MaterialTheme.colorScheme.error
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Date of Birth input field
            TextField(
                value = dateOfBirth,
                onValueChange = {
                    if (isValidDateOfBirth(it))
                        dateOfBirth = it
                },
                label = { Text("Date of Birth (dd-MM-yyyy)") },
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                isError = dateError
            )

            if (dateError) {
                Text(
                    text = "Invalid date format. Please use `dd-MM-yyyy`",
                    color = MaterialTheme.colorScheme.error
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { onClick() }
            ) {
                Text("Login")
            }
        }
    }
}


//fun Modifier.nextFocus(requester: FocusRequester): Modifier {
//    return onKeyEvent {
//        return@onKeyEvent if (it.type == KeyEventType.KeyUp && it.key == Key.Tab) {
//            requester.requestFocus()
//            true
//        } else {
//            false
//        }
//    }
//}