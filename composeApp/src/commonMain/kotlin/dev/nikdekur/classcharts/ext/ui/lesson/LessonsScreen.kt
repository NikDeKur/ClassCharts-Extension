package dev.nikdekur.classcharts.ext.ui.lesson

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.nikdekur.classcharts.ext.nav.Screen
import dev.nikdekur.classcharts.ext.rememberField
import dev.nikdekur.classcharts.ext.ui.TimeProvider
import dev.nikdekur.classcharts.ext.ui.error.ExceptionScreen
import kotlinx.datetime.LocalDate


data class LessonsScreen(
    val timeProvider: TimeProvider,
    val viewModel: LessonsViewModel,
    val initialDate: LocalDate
) : Screen {

    var selectedDate: LocalDate = initialDate

    @Composable
    override fun render() {
        var selectedDate by rememberField { selectedDate }

        this.selectedDate = selectedDate

        val state = viewModel.lessonsData.collectAsState()
        val lessonsState = state.value

        when (lessonsState) {
            is LessonsState.Loading -> CircularProgressIndicator()
            is LessonsState.Success -> {

                var datePickMenuVisible by rememberField { false }

                // TODO: Show date picker menu as Dialog
                if (datePickMenuVisible) {
                    DatePickMenu(
                        onChoose = { selectedDate = it; datePickMenuVisible = false },
                        current = selectedDate
                    )

                    return
                }

                Column {
                    val data = lessonsState.data
                    val quickDates = data.timetableDates

                    if (selectedDate !in quickDates)


                    println("TODAY: $")
                    println("QUICK DATES: $quickDates")

                    QuickDateChoosePane(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(108.dp),
                        selected = selectedDate,
                        quickDates = quickDates,
                        onExpandRequest = { datePickMenuVisible = true },
                        onSelect = { selectedDate = it }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    if (data.lessons.isEmpty())
                        NoLessonsNotifier()

                    else
                        LessonList(
                            timeProvider = timeProvider,
                            lessons = data.lessons,
                            homeworks = lessonsState.homeworks
                        )
                }

            }

            is LessonsState.Error -> {
                ExceptionScreen(lessonsState.error).render()
            }
        }

        LaunchedEffect(selectedDate) {
            viewModel.loadLessons(selectedDate)
        }
    }
}


