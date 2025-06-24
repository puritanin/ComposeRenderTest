package com.app.benchmark

import androidx.benchmark.macro.CompilationMode
import androidx.benchmark.macro.FrameTimingMetric
import androidx.benchmark.macro.StartupMode
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Direction
import androidx.test.uiautomator.Until
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BenchmarkCircle {

    @get:Rule
    val benchmarkRule = MacrobenchmarkRule()

    @Test
    fun composeRender() = benchmarkRule.measureRepeated(
        packageName = "com.app.compose",
        metrics = listOf(FrameTimingMetric()),
        compilationMode = CompilationMode.DEFAULT,
        iterations = 3,
        startupMode = StartupMode.WARM,
        setupBlock = {
            pressHome()
            startActivityAndWait()
        },
        measureBlock = {
            val selector = By.res("DemoCircle")

            device.wait(Until.hasObject(selector), 10_000)
            val content = device.findObject(selector)

            val directions = List(20) { Direction.entries }.flatten().shuffled()

            directions.forEach { direction ->
                val percent = (5..15).random() / 100f
                content.swipe(direction, percent, 500)
            }

            device.waitForIdle()
        },
    )
}
