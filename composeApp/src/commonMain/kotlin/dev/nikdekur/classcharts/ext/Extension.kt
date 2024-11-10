package dev.nikdekur.classcharts.ext

import dev.nikdekur.classcharts.ext.ui.TimeProvider
import dev.nikdekur.ornament.Application

interface Extension : Application {
    val time: TimeProvider
}