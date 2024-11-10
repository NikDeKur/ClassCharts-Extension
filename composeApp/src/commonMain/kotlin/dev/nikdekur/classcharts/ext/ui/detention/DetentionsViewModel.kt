package dev.nikdekur.classcharts.ext.ui.detention

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.nikdekur.classcharts.detention.Detention
import dev.nikdekur.classcharts.detention.DetentionsService
import dev.nikdekur.classcharts.ext.Extension
import dev.nikdekur.classcharts.ext.ExtensionComponent
import dev.nikdekur.classcharts.ext.cc.ClassChartsService
import dev.nikdekur.ndkore.service.inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DetentionsViewModel(
    override val app: Extension
) : ViewModel(), ExtensionComponent {

    val ccService: ClassChartsService by inject()

    val detentionsService: DetentionsService by ccService.client.inject()

    private val _detentions = MutableStateFlow<DetentionsState>(DetentionsState.Loading)
    val detentions: StateFlow<DetentionsState> get() = _detentions

    fun loadDetentions() {
        viewModelScope.launch {
            try {
                val detentions = detentionsService.getDetentions()

                _detentions.value = DetentionsState.Success(detentions)
            } catch (e: Exception) {
                _detentions.value = DetentionsState.Error(e)
            }
        }
    }
}

// Классы состояния данных
sealed class DetentionsState {
    object Loading : DetentionsState()
    data class Success(val detentions: Collection<Detention>) : DetentionsState()
    data class Error(val error: Throwable) : DetentionsState()
}