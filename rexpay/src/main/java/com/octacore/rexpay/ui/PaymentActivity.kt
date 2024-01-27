package com.octacore.rexpay.ui

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.navigation.compose.rememberNavController
import com.octacore.rexpay.R
import com.octacore.rexpay.RexPay
import com.octacore.rexpay.RexPay.Companion.PAYMENT_PAYLOAD
import com.octacore.rexpay.models.PayPayload
import com.octacore.rexpay.ui.theme.RexpayTheme

class PaymentActivity : ComponentActivity() {
    private val rexPay = RexPay.getInstance()

    private val payload by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(PAYMENT_PAYLOAD, PayPayload::class.java)
        } else {
            intent.getParcelableExtra(PAYMENT_PAYLOAD)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RexpayTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Box {
                        Image(
                            painterResource(id = R.drawable.whangaehu_bg),
                            contentDescription = "Application Background",
                            contentScale = ContentScale.FillBounds,
                            modifier = Modifier.matchParentSize(),
                            alpha = 0.08F,
                        )
                        AppNavGraph(
                            navController = rememberNavController(),
                            modifier = Modifier.matchParentSize(),
                            payload = payload
                        )
                    }
                }
            }
        }
    }
}