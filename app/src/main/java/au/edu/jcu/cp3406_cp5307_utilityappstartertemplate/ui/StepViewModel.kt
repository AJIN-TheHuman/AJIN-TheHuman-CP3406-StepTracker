package au.edu.jcu.cp3406_cp5307_utilityappstartertemplate.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import au.edu.jcu.cp3406_cp5307_utilityappstartertemplate.data.Quote
import au.edu.jcu.cp3406_cp5307_utilityappstartertemplate.data.StepRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * StepViewModel manages the step counting logic and distance calculations.
 */
class StepViewModel(private val repository: StepRepository) : ViewModel() {
    // Expose step count as a StateFlow for the UI to observe
    val stepCount: StateFlow<Int> = repository.stepCount
        .map { it.toInt() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    private val _quote = MutableStateFlow<Quote?>(null)
    val quote: StateFlow<Quote?> = _quote.asStateFlow()

    fun resetSteps() {
        repository.resetSteps()
    }

    fun isSensorAvailable(): Boolean = repository.isSensorAvailable()

    fun fetchQuote() {
        viewModelScope.launch {
            _quote.value = repository.fetchRandomQuote()
        }
    }

    /**
     * Calculates distance based on steps, stride length (meters), and unit preference.
     */
    fun calculateDistance(steps: Int, strideLength: Float, useMiles: Boolean): Double {
        val distanceKm = (steps * strideLength) / 1000.0
        return if (useMiles) distanceKm * 0.621371 else distanceKm
    }

    init {
        // Start listening to sensor events when ViewModel is created
        repository.startListening()
        fetchQuote() // Fetch a motivational quote on startup
    }

    override fun onCleared() {
        super.onCleared()
        // Stop listening to prevent memory leaks or battery drain when not needed
        repository.stopListening()
    }

    // Factory to create StepViewModel with StepRepository dependency
    class Factory(private val repository: StepRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(StepViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return StepViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
