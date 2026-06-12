package au.edu.jcu.cp3406_cp5307_utilityappstartertemplate.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

/**
 * SettingsViewModel holds user preferences for the step tracker.
 * In a real app, these would be persisted using DataStore or SharedPreferences.
 */
class SettingsViewModel : ViewModel() {
    var dailyGoal by mutableIntStateOf(10000)
    var useMiles by mutableStateOf(false)
    var strideLength by mutableFloatStateOf(0.78f) // Default in meters
}
