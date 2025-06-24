package com.app.compose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId

class MainActivity : ComponentActivity() {

    private val currentScreen = mutableStateOf(Screen.HEADER)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            Box(
                modifier = Modifier
                    .semantics { testTagsAsResourceId = true }
                    .fillMaxSize()
                    .systemBarsPadding()
            ) {
                when (currentScreen.value) {
                    Screen.HEADER -> {
                        DemoHeader(modifier = Modifier.fillMaxSize())
                    }
                    Screen.CIRCLE_BLUR -> {
                        DemoCircle(modifier = Modifier.fillMaxSize(), effect = Effect.BLUR)
                    }
                    Screen.CIRCLE_MAGNIFIER -> {
                        DemoCircle(modifier = Modifier.fillMaxSize(), effect = Effect.MAGNIFIER)
                    }
                }

                Navigation(
                    onClick = { currentScreen.value = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter),
                )
            }
        }
    }
}
