package com.houseofrafa.cashizard

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.arkivanov.decompose.retainedComponent
import com.houseofrafa.cashizard.presentation.root.RootComponent

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        // Retained so the component tree (and its state) survives configuration changes.
        val root = retainedComponent { RootComponent.create(it) }

        setContent {
            App(root)
        }
    }
}
