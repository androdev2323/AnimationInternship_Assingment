package com.example.animation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.DrawStyle
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.whenStarted
import com.example.animation.databinding.ActivityMainBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.pow


import kotlin.math.sin

class MainActivity : AppCompatActivity() {
    private val N = 360
    private val SPEED = 1f / 200f
    private val TWO_PI = 2 * PI
    private val SHIFT = (TWO_PI * 10) / 8f
    private val FREQUENCY = 4
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)



        setContentView(binding.root)
        binding.composeView.setContent {
            Box(modifier = Modifier.fillMaxWidth()){
                CircleWave(modifier = Modifier.size(300.dp).background(MaterialTheme.colorScheme.surface))
                Clock(modifier = Modifier.size(300.dp))
            }

        }
    }




    @Composable
    fun animationTimeMillis(): State<Long> {
        val scope = rememberCoroutineScope()
        val millisState = mutableStateOf(0L)
        val lifecycleOwner = LocalLifecycleOwner.current
        scope.launch {
            val startTime = withFrameMillis { it }
            lifecycleOwner.whenStarted {
                while (true) {
                    withFrameMillis { frameTime ->
                        millisState.value = frameTime - startTime
                    }
                }
            }
        }
        return millisState
    }


    @Composable
    fun CircleWave(modifier: Modifier = Modifier,color:Color=MaterialTheme.colorScheme.surface) {
        val state = animationTimeMillis()
        val path = remember { Path() }
        val colors = listOf(Color.Cyan)

        Canvas(modifier = modifier.background(MaterialTheme.colorScheme.surface)) {

                drawRect(color)

            val (width, height) = size
            val waveAmplitude = size.minDimension / 30
            val circleRadius = size.minDimension / 2f - waveAmplitude
            val millis = state.value

            drawCircle(
                color=Color.Black,
                radius=circleRadius-waveAmplitude-30,
                style=Fill
            )
            translate(width / 2f, height / 2f) {

                colors.forEachIndexed { colorIndex, color ->
                    path.reset()
                    for (i in 0 until N) {
                        val a = i * TWO_PI / N
                        val t = millis * SPEED
                        val c = cos(a * FREQUENCY - colorIndex * SHIFT + t)
                        val p = ((1 + cos(a - t)) / 2).pow(3)
                        val r = circleRadius + waveAmplitude * c * p - 50
                        val x = r * sin(a)
                        val y = r * -cos(a)


                        if (i == 0) {

                            path.moveTo(x.toFloat(), y.toFloat())
                        } else {
                            path.lineTo(-x.toFloat(),- y.toFloat())


                        }
                    }
                    path.close()



                    drawPath(
                        brush= Brush.verticalGradient(listOf(Color.Red, Color.Cyan,Color.Magenta), tileMode = TileMode.Repeated),
                        path = path,
                        style = androidx.compose.ui.graphics.drawscope.Fill,
                        blendMode = BlendMode.Darken,
                    )
                }
            }
        }
    }

    @Composable
    fun Clock(modifier: Modifier = Modifier) {
        val currentTime = remember { mutableStateOf(getCurrentTime()) }

        LaunchedEffect(Unit) {
            while (true) {
                currentTime.value = getCurrentTime()
                kotlinx.coroutines.delay(1000L)
            }
        }

        Box(modifier = modifier, contentAlignment = Alignment.Center) {
            Text(
                text = currentTime.value,
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }

    fun getCurrentTime(): String {
        val sdf = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        return sdf.format(Date())
    }
}
