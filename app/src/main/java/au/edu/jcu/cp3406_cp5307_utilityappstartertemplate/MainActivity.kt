package au.edu.jcu.cp3406_cp5307_utilityappstartertemplate

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import java.util.Locale
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import au.edu.jcu.cp3406_cp5307_utilityappstartertemplate.data.StepRepository
import au.edu.jcu.cp3406_cp5307_utilityappstartertemplate.ui.SettingsViewModel
import au.edu.jcu.cp3406_cp5307_utilityappstartertemplate.ui.StepViewModel
import au.edu.jcu.cp3406_cp5307_utilityappstartertemplate.ui.theme.CP3406_CP5603UtilityAppStarterTemplateTheme

class MainActivity : ComponentActivity() {
    private lateinit var stepRepository: StepRepository

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        // In a production app, you'd handle the case where permission is denied
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize the repository
        stepRepository = StepRepository(applicationContext)

        // Request Activity Recognition permission for Android 10+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION)
                != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.ACTIVITY_RECOGNITION)
            }
        }

        enableEdgeToEdge()
        setContent {
            // Manual DI: creating viewmodels and passing dependencies
            val stepViewModel: StepViewModel = viewModel(
                factory = StepViewModel.Factory(stepRepository)
            )
            val settingsViewModel: SettingsViewModel = viewModel()

            CP3406_CP5603UtilityAppStarterTemplateTheme {
                UtilityApp(stepViewModel, settingsViewModel)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun UtilityAppPreview() {
    // Note: In a real preview, you might want to provide mock ViewModels
}

@Composable
fun UtilityApp(stepViewModel: StepViewModel, settingsViewModel: SettingsViewModel) {
    var selectedTab by remember { mutableStateOf("Utility") }

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = "Utility") },
                    label = { Text("Utility") },
                    selected = selectedTab == "Utility",
                    onClick = { selectedTab = "Utility" }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
                    label = { Text("Settings") },
                    selected = selectedTab == "Settings",
                    onClick = { selectedTab = "Settings" }
                )
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (selectedTab) {
                "Utility" -> UtilityScreen(stepViewModel, settingsViewModel)
                "Settings" -> SettingsScreen(settingsViewModel)
            }
        }
    }
}

@Composable
fun UtilityScreen(stepViewModel: StepViewModel, settingsViewModel: SettingsViewModel) {
    val steps by stepViewModel.stepCount.collectAsState()
    val goal = settingsViewModel.dailyGoal
    val progress = if (goal > 0) steps.toFloat() / goal else 0f
    val percentage = (progress * 100).toInt()

    val distance = stepViewModel.calculateDistance(
        steps,
        settingsViewModel.strideLength,
        settingsViewModel.useMiles
    )
    val unitLabel = if (settingsViewModel.useMiles) "miles" else "km"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(
            text = "Step Tracker",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        if (!stepViewModel.isSensorAvailable()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
            ) {
                Text(
                    text = "Step counter sensor not available on this device.",
                    modifier = Modifier.padding(16.dp),
                    color = MaterialTheme.colorScheme.error
                )
            }
        }

        // Circular Progress with Step Count
        Box(contentAlignment = Alignment.Center, modifier = Modifier.size(200.dp)) {
            CircularProgressIndicator(
                progress = { progress.coerceIn(0f, 1f) },
                modifier = Modifier.fillMaxSize(),
                strokeWidth = 12.dp,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
            )
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = steps.toString(),
                    style = MaterialTheme.typography.displayLarge,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = "Steps",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }

        // Goal and Percentage
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "$percentage% of daily goal",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Goal: $goal steps",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Distance Info
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Card(
                modifier = Modifier.padding(8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "Distance Walked", style = MaterialTheme.typography.labelMedium)
                    Text(
                        text = String.format(Locale.getDefault(), "%.2f %s", distance, unitLabel),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Button(
            onClick = { stepViewModel.resetSteps() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Reset Steps")
        }

        Spacer(modifier = Modifier.weight(1f))

        // Motivational Quote Placeholder
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Motivational quote will appear here",
                    style = MaterialTheme.typography.bodyMedium,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                )
            }
        }
    }
}

@Composable
fun SettingsScreen(viewModel: SettingsViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text(
            text = "Settings",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        HorizontalDivider()

        // Daily Goal Setting
        Column {
            Text(text = "Daily Step Goal: ${viewModel.dailyGoal}", style = MaterialTheme.typography.titleMedium)
            Slider(
                value = viewModel.dailyGoal.toFloat(),
                onValueChange = { viewModel.dailyGoal = it.toInt() },
                valueRange = 1000f..20000f,
                steps = 19
            )
        }

        // Unit Selection
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "Use Miles", style = MaterialTheme.typography.titleMedium)
            Switch(
                checked = viewModel.useMiles,
                onCheckedChange = { viewModel.useMiles = it }
            )
        }

        // Stride Length
        Column {
            Text(text = "Stride Length (meters)", style = MaterialTheme.typography.titleMedium)
            OutlinedTextField(
                value = viewModel.strideLength.toString(),
                onValueChange = {
                    val newValue = it.toFloatOrNull()
                    if (newValue != null) viewModel.strideLength = newValue
                },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )
            Text(
                text = "Default is 0.78m. This is used for distance calculation.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
