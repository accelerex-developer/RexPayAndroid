@file:JvmSynthetic

package com.octacore.rexpay

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.octacore.rexpay.ui.AppNavGraph
import com.octacore.rexpay.ui.theme.RexPayTheme

internal class RexPayActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            val onBackPressedDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher

            RexPayTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background,
                ) {
                    Box {
                        Image(
                            painterResource(id = R.drawable.whangaehu_bg),
                            contentDescription = "Application Background",
                            contentScale = ContentScale.FillBounds,
                            modifier = Modifier.matchParentSize(),
                            alpha = 0.08F,
                        )
                        Scaffold(
                            backgroundColor = Color.Transparent,
                            topBar = {
                                TopAppBar(
                                    title = {},
                                    backgroundColor = Color.Transparent,
                                    elevation = 0.dp,
                                    navigationIcon = {
                                        IconButton(onClick = {
                                            onBackPressedDispatcher?.onBackPressed()
                                        }) {
                                            Icon(
                                                Icons.AutoMirrored.Filled.ArrowBack,
                                                contentDescription = "Back"
                                            )
                                        }
                                    }
                                )
                            }
                        ) {
                            AppNavGraph(
                                navController = navController,
                                modifier = Modifier
                                    .matchParentSize()
                                    .padding(it),
                            )
                        }
                    }
                }
            }
        }
    }
}