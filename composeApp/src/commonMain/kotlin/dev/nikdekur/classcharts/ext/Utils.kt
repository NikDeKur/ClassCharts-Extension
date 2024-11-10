@file:Suppress("NOTHING_TO_INLINE")
@file:OptIn(ExperimentalContracts::class)

package dev.nikdekur.classcharts.ext

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

@Composable
inline fun <T> rememberField(crossinline initialValue: () -> T): ReadWriteProperty<Any?, T> {
    val original = remember { mutableStateOf(initialValue()) }

    return object : ReadWriteProperty<Any?, T> {
        override fun getValue(thisRef: Any?, property: KProperty<*>): T {
            return original.value
        }

        override fun setValue(
            thisRef: Any?,
            property: KProperty<*>,
            value: T
        ) {
            original.value = value
        }
    }
}

inline fun <T> Modifier.ifNotNull(value: T?, crossinline block: Modifier.(T) -> Modifier): Modifier {
    contract {
        returns() implies (value != null)
        callsInPlace(block, InvocationKind.AT_MOST_ONCE)
    }
    return if (value != null) block(value) else this
}

