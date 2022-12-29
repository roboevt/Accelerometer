package com.example.accelerometer

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.accelerometer.ui.theme.AccelerometerTheme

class MainActivity : ComponentActivity(), SensorEventListener {

    private lateinit var accelerometer: Sensor
    private lateinit var sensorManager: SensorManager

    private val accelData = mutableStateOf(floatArrayOf(1f,1f,1f))
    private val lastFrameTime = mutableStateOf(1L)
    private val frameRate = mutableStateOf(1f)

    private val X = 0;
    private val Y = 1;
    private val Z = 2;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)?.let {
            this.accelerometer = it
            sensorManager.registerListener(this, this.accelerometer, 1000)//1000hz
        }

        setContent {
            AccelerometerTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Display()
                }
            }
        }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {}

    override fun onSensorChanged(event: SensorEvent) {
        when(event?.sensor?.type) {
            Sensor.TYPE_ACCELEROMETER -> {
                this.accelData.value = event.values
                val currentFrameTime = event.timestamp
                val frameTime = currentFrameTime - this.lastFrameTime.value
                this.lastFrameTime.value = currentFrameTime
                this.frameRate.value = 1000000000f / frameTime.toFloat()
            }
        }
    }

    @Composable
    fun Display() {
        Column(Modifier.padding(top = 10.dp, start = 10.dp)) {
            Text(text = "AccelerationX: " + accelData.value[X].toString() + "ms^2")
            Text(text = "AccelerationY: " + accelData.value[Y].toString() + "ms^2")
            Text(text = "AccelerationZ: " + accelData.value[Z].toString() + "ms^2")
            Text(text = "Refresh rate: " + frameRate.value.toString() + "hz")
            IndicatorBox()
        }
    }

    @Composable
    fun IndicatorBox() {
        Box(Modifier.graphicsLayer(
            translationY = accelData.value[Y].toFloat() * 100.0f + 200f,
            translationX = accelData.value[X].toFloat() * -50f + 600f,
            scaleX = accelData.value[Z].toFloat() / 10f + 1f,
            scaleY = accelData.value[Z].toFloat() / 10f + 1f
        )) {
            Box (
                Modifier
                    .background(Color.Blue)
                    .width(25.dp)
                    .height(25.dp)) {
            }
        }
    }
}
