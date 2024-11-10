@file:OptIn(ExperimentalRichTextApi::class)
@file:Suppress("INVISIBLE_MEMBER", "INVISIBLE_REFERENCE")

package dev.nikdekur.classcharts.ext.ui.lesson

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.FirstBaseline
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import classcharts_ext.composeapp.generated.resources.Res
import classcharts_ext.composeapp.generated.resources.calendar
import classcharts_ext.composeapp.generated.resources.mc_green_book
import classcharts_ext.composeapp.generated.resources.mc_orange_book
import com.mohamedrejeb.richeditor.annotation.ExperimentalRichTextApi
import com.mohamedrejeb.richeditor.model.RichTextState
import com.mohamedrejeb.richeditor.ui.material3.RichText
import dev.nikdekur.classcharts.ext.ifNotNull
import dev.nikdekur.classcharts.ext.rememberField
import dev.nikdekur.classcharts.ext.ui.TimeProvider
import dev.nikdekur.classcharts.homework.Homework
import dev.nikdekur.classcharts.homework.isSubmitted
import dev.nikdekur.classcharts.lesson.Lesson
import dev.nikdekur.classcharts.lesson.getTimeLeft
import dev.nikdekur.classcharts.lesson.isActiveNow
import dev.nikdekur.ndkore.ext.capitalizeFirstLetter
import dev.nikdekur.ndkore.ext.delay
import dev.nikdekur.ndkore.ext.toHoursPart
import dev.nikdekur.ndkore.ext.toMinutesPart
import dev.nikdekur.ndkore.map.ListsMap
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import kotlinx.datetime.minus
import kotlinx.datetime.number
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.imageResource
import org.jetbrains.compose.resources.painterResource
import kotlin.time.Duration.Companion.minutes


@Composable
fun BasicDateCard(
    modifier: Modifier = Modifier,
    colors: CardColors = CardDefaults.cardColors(),
    onClick: (() -> Unit)? = null,
    content: @Composable (() -> Unit) = {},
) {
    val shape = RoundedCornerShape(12.dp)
    Card(
        modifier = modifier
            .clip(shape)
            .ifNotNull(onClick) { clickable(onClick = it) },
        shape = shape,
        colors = colors,
        content = {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                content()
            }
        }
    )
}

@Composable
fun PickableDateCard(
    modifier: Modifier = Modifier,
    day: LocalDate,
    selected: LocalDate,
    onChoose: (LocalDate) -> Unit
) {
    val backgroundColor =
        if (day == selected) MaterialTheme.colorScheme.primaryContainer
        else MaterialTheme.colorScheme.secondaryContainer

    BasicDateCard(
        modifier = modifier,
        onClick = { onChoose(day) },
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        )
    ) {
        Text(
            text = day.dayOfMonth.toString(),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = day.dayOfWeek.name.lowercase().capitalizeFirstLetter().take(3),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}


@Composable
fun DatePickMenu(
    modifier: Modifier = Modifier,
    onChoose: (LocalDate) -> Unit = {},
    current: LocalDate
) {
    var selectedDate by rememberField { current }
    var displayedYear by rememberField { selectedDate.year }
    var displayedMonth by rememberField { selectedDate.month.number }

    val daysInMonth = remember(displayedYear, displayedMonth) {
        getAllDaysInMonth(displayedYear, displayedMonth)
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Шапка с переключателями месяцев и годов
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = {
                displayedMonth = if (displayedMonth == 1) 12 else displayedMonth - 1
                if (displayedMonth == 12) displayedYear--
            }) {
                Text("<", style = MaterialTheme.typography.headlineLarge)
            }

            Text(
                text = "${Month(displayedMonth)} $displayedYear",
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.align(Alignment.CenterVertically)
            )

            IconButton(onClick = {
                displayedMonth = if (displayedMonth == 12) 1 else displayedMonth + 1
                if (displayedMonth == 1) displayedYear++
            }) {
                Text(">", style = MaterialTheme.typography.headlineLarge)
            }
        }

        // Отображение дней недели (понедельник - воскресенье)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            Text("Mon", modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
            Text("Tue", modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
            Text("Wed", modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
            Text("Thu", modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
            Text("Fri", modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
            Text("Sat", modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
            Text("Sun", modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Отображение дней в месяце
        daysInMonth.chunked(7).forEach { week ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                val size = 48.dp

                week.forEach { day ->
                    // Show empty space if day is null
                    if (day == null) {
                        Spacer(modifier = Modifier.size(size))
                    } else {

                        val backgroundColor =
                            if (day == selectedDate) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.secondary

                        BasicDateCard(
                            modifier = Modifier
                                .size(size)
                                .padding(4.dp),
                            onClick = {
                                selectedDate = day
                                onChoose(day)
                            },
                            colors = CardDefaults.cardColors(
                                containerColor = backgroundColor
                            )
                        ) {
                            Text(
                                text = day.dayOfMonth.toString(),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}

// Функция для получения всех дней месяца с учетом смещения для начала недели
fun getAllDaysInMonth(year: Int, month: Int): List<LocalDate?> {
    val firstDayOfMonth = LocalDate(year, month, 1)
    val lastDayOfMonth = firstDayOfMonth.plus(1, DateTimeUnit.MONTH).minus(1, DateTimeUnit.DAY)

    val startDayOfWeek = firstDayOfMonth.dayOfWeek
    val totalDaysInMonth = (firstDayOfMonth..lastDayOfMonth).toList()

    // Добавляем null до понедельника, если месяц не начинается с него
    val leadingEmptyDays = List(startDayOfWeek.ordinal - DayOfWeek.MONDAY.ordinal) { null }

    // Заполняем дни месяца и завершаем неделю, если она не полная
    return (leadingEmptyDays + totalDaysInMonth + List(7 - (leadingEmptyDays.size + totalDaysInMonth.size) % 7) { null })
}

fun getAllDaysInWeek(date: LocalDate): List<LocalDate> {
    val startOfWeek = date.minus(date.dayOfWeek.ordinal.toLong(), DateTimeUnit.DAY)
    return startOfWeek.rangeTo(startOfWeek.plus(6, DateTimeUnit.DAY)).toList()
}

// Helper для диапазона дат
operator fun LocalDate.rangeTo(other: LocalDate) = generateSequence(this) { date ->
    date.takeIf { it < other }?.plus(1, DateTimeUnit.DAY)
}



@Composable
fun DatePickMenuCard(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {

    BasicDateCard(
        modifier = modifier.size(50.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary
        ),
        onClick = onClick
    ) {

        Image(
            painter = painterResource(Res.drawable.calendar),
            contentDescription = "Calendar image",
            modifier = Modifier.size(35.dp)
        )
    }
}


@Composable
fun MonthNameCard(
    modifier: Modifier = Modifier,
    month: Month
) {
    BasicDateCard(
        modifier = modifier.size(50.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary
        ),
    ) {
        Text(
            text = month.name.take(3).lowercase().capitalizeFirstLetter(),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onPrimary,
        )
    }
}


@Composable
fun DateCardAndMonthCard(
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal,
    month: Month,
    onExpandRequest: () -> Unit,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = horizontalArrangement
    ) {

        DatePickMenuCard(
            modifier = Modifier.weight(1f),
            onClick = onExpandRequest
        )

        MonthNameCard(
            modifier = Modifier.weight(1f),
            month = month
        )

        repeat(5) {
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}


@Composable
fun QuickDatesRow(
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal,
    quickDates: Collection<LocalDate>,
    selected: LocalDate,
    onSelect: (LocalDate) -> Unit
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = horizontalArrangement
    ) {
        var cards = 0

        val dates =
            if (selected in quickDates) quickDates
            else getAllDaysInWeek(selected)

        dates.forEach { day ->
            PickableDateCard(
                modifier = Modifier.weight(1f),
                day = day,
                selected = selected,
                onChoose = onSelect
            )
            cards++
        }

        val left = 7 - cards
        repeat(left) {
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Composable
fun QuickDateChoosePane(
    modifier: Modifier = Modifier,
    selected: LocalDate,
    quickDates: Collection<LocalDate>,
    onExpandRequest: () -> Unit,
    onSelect: (LocalDate) -> Unit,
) {

    val arrangement = Arrangement.spacedBy(8.dp)

    Column(
        modifier = modifier,
        verticalArrangement = arrangement
    ) {
        DateCardAndMonthCard(
            month = selected.month,
            horizontalArrangement = arrangement,
            onExpandRequest = onExpandRequest
        )

        QuickDatesRow(
            quickDates = quickDates,
            horizontalArrangement = arrangement,
            selected = selected,
            onSelect = onSelect
        )
    }
}


@Composable
fun NoLessonsNotifier(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        BasicDateCard(
            modifier = Modifier.fillMaxSize(0.75f),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Text(
                text = "No lessons for this day",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun LessonList(
    modifier: Modifier = Modifier,
    timeProvider: TimeProvider,
    lessons: Collection<Lesson>,
    homeworks: ListsMap<Int, Homework>
) {

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(scrollState),

        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        lessons.forEach { lesson ->

            LessonField(
                timeProvider = timeProvider,
                lesson = lesson,
                homeworks = homeworks[lesson.id] ?: emptyList(),
            )
        }
    }
}


@Composable
fun HomeworkDialog(
    homework: Homework,
    initialCompleted: Boolean,
    onClose: () -> Unit
) {

    var ticked by rememberField { initialCompleted } // состояние выполнения

    // Флаг для отслеживания процесса отметки
    var isTicking by rememberField { false }


    Dialog(
        onDismissRequest = onClose,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {

        val shape = RoundedCornerShape(12.dp)

        Box(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .fillMaxHeight(0.9f)
                .clip(shape)
                .border(4.dp,
                    if (ticked) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.outline,
                    shape
                )
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp) // Padding for dialog content from dialog window,
        ) {

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxHeight() // Используем всю доступную высоту
            ) {

                Text(
                    text = homework.lesson,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                )

                Text(
                    text = homework.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                )

                Spacer(modifier = Modifier.height(16.dp))

                val scrollState = rememberScrollState()
                Column(
                    modifier = Modifier
                        .weight(1f) // Устанавливаем вес для прокручиваемого контента
                        .verticalScroll(scrollState)
                ) {
                    RichText(
                        state = RichTextState().setHtml(homework.description),
                        color = MaterialTheme.colorScheme.onBackground,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }

                Spacer(modifier = Modifier.height(16.dp)) // Добавляем немного пространства перед кнопками

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly // Распределяем кнопки по ширине
                ) {
                    Button(onClick = {
                        isTicking = true
                    }) {
                        Text("Tick")
                    }

                    Button(onClick = onClose) {
                        Text("Close")
                    }
                }
            }
        }
    }

    // Запускаем отметку, если установлен флаг isMarking
    LaunchedEffect(isTicking) {
        if (isTicking) {
            homework.tick()
            ticked = homework.ticked // обновляем статус выполнения
            isTicking = false // сбрасываем флаг отметки
        }
    }
}


@Composable
fun HomeworkMark(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    homework: Homework
) {
    val drawableResource =
        if (homework.ticked || homework.state.isSubmitted) Res.drawable.mc_green_book
        else Res.drawable.mc_orange_book

    Image(
        painter = BitmapPainter(imageResource(drawableResource)),
        contentDescription = "exclamation mark",
        modifier = modifier
            .clip(RoundedCornerShape(25.dp))
            .ifNotNull(onClick) { clickable(onClick = it) }
    )
}

@Composable
fun LessonField(
    modifier: Modifier = Modifier,
    timeProvider: TimeProvider,
    lesson: Lesson,
    homeworks: Collection<Homework>
) {

    // Функции для получения состояния урока
    fun timeLeft() = lesson.getTimeLeft(timeProvider.clock)
    fun isActiveNow() = lesson.isActiveNow(timeProvider.clock)

    var remainingActiveTime by rememberField(::timeLeft)
    var isActive by rememberField(::isActiveNow)

    var showHomework by rememberField<Homework?> { null }

    val homework = showHomework
    if (homework != null) {
        HomeworkDialog(
            homework = homework,
            initialCompleted = homework.ticked || homework.state.isSubmitted,
            onClose = { showHomework = null }
        )
    }

    // Обновляем состояние активности и оставшееся время
    LaunchedEffect(lesson.id) {
        while (isActive) {
            isActive = isActiveNow()
            if (isActive) {
                remainingActiveTime = timeLeft()
            }
            delay(5000)
        }
    }

    val containerColor = if (isActive)
        MaterialTheme.colorScheme.primaryContainer
    else
        MaterialTheme.colorScheme.surfaceContainer

    val contentColor = if (isActive)
        MaterialTheme.colorScheme.onPrimaryContainer
    else
        MaterialTheme.colorScheme.onSurface


    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = containerColor
        ),
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .fillMaxWidth(0.95f)
                .padding(top = 8.dp, bottom = 8.dp),
        ) {
            // Первая строка: название урока и время
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = lesson.name,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = contentColor,
                    modifier = Modifier.alignBy(FirstBaseline)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "(${lesson.subject})",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.alignBy(FirstBaseline)
                )

                Spacer(modifier = Modifier.weight(1f))

                if (isActive) {
                    val remainingTime = (remainingActiveTime + 1.minutes)
                    val hours = remainingTime.toHoursPart().toString().padStart(2, '0')
                    val minutes = remainingTime.toMinutesPart().toString().padStart(2, '0')

                    Text(
                        text = "$hours:$minutes left",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Вторая строка: информация о кабинете, преподавателе и времени
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = lesson.roomName.ifEmpty { "No room" },
                    style = MaterialTheme.typography.bodyMedium,
                    color = contentColor
                )

                if (lesson.teacherName.isNotEmpty()) {
                    Text(
                        text = " | ${lesson.teacherName}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = contentColor
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                if (homeworks.isNotEmpty()) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        homeworks.forEach { homework ->
                            HomeworkMark(
                                modifier = Modifier.size(20.dp),
                                homework = homework,
                                onClick = { showHomework = homework }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                val startTime = lesson.startTime.toLocalDateTime(timeProvider.timeZone).time
                val endTime = lesson.endTime.toLocalDateTime(timeProvider.timeZone).time
                Text(
                    text = "$startTime - $endTime",
                    style = MaterialTheme.typography.bodyMedium,
                    color = contentColor
                )
            }
        }
    }
}
