package dev.nikdekur.classcharts.ext.ui

import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn

interface TimeProvider {

    val clock: Clock
    val timeZone: TimeZone

    fun today() = clock.todayIn(timeZone)
}