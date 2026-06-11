package au.edu.jcu.cp3406_cp5307_utilityappstartertemplate.data

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * StepRepository wraps the Android SensorManager and exposes step count as a StateFlow.
 * It handles sensor registration and baseline offset for resetting steps.
 */
class StepRepository(context: Context) : SensorEventListener {
    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val stepSensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

    private val _stepCount = MutableStateFlow(0f)
    val stepCount: StateFlow<Float> = _stepCount

    private var initialSteps = -1f
    private var lastTotalSteps = 0f

    fun startListening() {
        // NOTE: To keep counting steps when the app is in the background or the screen is off,
        // you would typically move this sensor logic into a Foreground Service.
        // For this assignment, we are keeping it in the repository for simplicity.
        stepSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
    }

    fun stopListening() {
        sensorManager.unregisterListener(this)
    }

    fun resetSteps() {
        // Since we can't reset the hardware sensor, we record the current value as the new baseline
        initialSteps = lastTotalSteps
        _stepCount.value = 0f
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_STEP_COUNTER) {
            lastTotalSteps = event.values[0]
            if (initialSteps == -1f) {
                initialSteps = lastTotalSteps
            }
            _stepCount.value = lastTotalSteps - initialSteps
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Not used
    }

    fun isSensorAvailable(): Boolean = stepSensor != null
}
