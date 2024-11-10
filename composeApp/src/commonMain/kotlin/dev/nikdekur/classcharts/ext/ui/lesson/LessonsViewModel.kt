package dev.nikdekur.classcharts.ext.ui.lesson

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.nikdekur.classcharts.date.DateType
import dev.nikdekur.classcharts.ext.Extension
import dev.nikdekur.classcharts.ext.ExtensionComponent
import dev.nikdekur.classcharts.ext.cc.ClassChartsService
import dev.nikdekur.classcharts.homework.Homework
import dev.nikdekur.classcharts.homework.HomeworkService
import dev.nikdekur.classcharts.lesson.LessonsService
import dev.nikdekur.classcharts.lesson.LessonsService.LessonsData
import dev.nikdekur.ndkore.map.ListsMap
import dev.nikdekur.ndkore.map.MutableListsMap
import dev.nikdekur.ndkore.map.add
import dev.nikdekur.ndkore.service.inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate

class LessonsViewModel(
    override val app: Extension
) : ViewModel(), ExtensionComponent {

    val ccService: ClassChartsService by inject()

    val lessonsService: LessonsService by ccService.client.inject()
    val homeworkService: HomeworkService by ccService.client.inject()

    private val _lessonsData = MutableStateFlow<LessonsState>(LessonsState.Loading)
    val lessonsData: StateFlow<LessonsState> get() = _lessonsData

    fun loadLessons(date: LocalDate) {
        viewModelScope.launch {
            try {
                val data = lessonsService.getLessons(date)
                val homeworksList = homeworkService.getHomeworks(date, date, DateType.DUE_DATE)

                val homeworkMap: MutableListsMap<Int, Homework> = HashMap()
                homeworksList.forEach { homework ->
                    data.lessons.forEach { lesson ->
                        val belongToLesson = homework.lesson == lesson.name
                        if (!belongToLesson) return@forEach
                        homeworkMap.add(lesson.id, homework)
                    }
                }

                _lessonsData.value = LessonsState.Success(data, homeworkMap)
            } catch (e: Exception) {
                _lessonsData.value = LessonsState.Error(e)
            }
        }
    }
}

// Классы состояния данных
sealed class LessonsState {
    object Loading : LessonsState()
    data class Success(val data: LessonsData, val homeworks: ListsMap<Int, Homework>) : LessonsState()
    data class Error(val error: Throwable) : LessonsState()
}
