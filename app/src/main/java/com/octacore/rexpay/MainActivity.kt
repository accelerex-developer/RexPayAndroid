package com.octacore.rexpay

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.octacore.rexpay.models.PayPayload
import com.octacore.rexpay.ui.theme.RexpayTheme
import java.util.UUID

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RexpayTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Button(
                        modifier = Modifier.wrapContentSize(),
                        onClick = {
                            val rexPay = RexPay.getInstance(this)
                            rexPay.setPaymentListener(object : RexPay.RexPayListener {
                                override fun onSuccess() {}
                                override fun onFailure() {}
                            })
                            val payload = PayPayload(
                                reference = UUID.randomUUID().toString(),
                                amount = 1,
                                currency = "NGN",
                                userId = "random.user@email.com",
                                callbackUrl = "",
                            )
                            rexPay.makePayment(payload)
                        }
                    ) {
                        Text(text = "PAY")
                    }
                }
            }
        }
    }
}