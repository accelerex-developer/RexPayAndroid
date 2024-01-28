package com.octacore.rexpay

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.ui.Modifier
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
                    color = MaterialTheme.colors.background
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
                                reference = UUID.randomUUID().toString().replace(Regex("\\W"), ""),
                                amount = 1,
                                currency = "NGN",
                                userId = "random.user@email.com",
                                callbackUrl = "",
                                email = "random.user@email.com",
                                customerName = "Random User"
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