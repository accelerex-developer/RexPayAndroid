package com.example.rexpay

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.ui.Modifier
import com.octacore.rexpay.RexPay
import com.octacore.rexpay.domain.models.PayPayload
import com.example.rexpay.ui.theme.RexAppTheme
import com.octacore.rexpay.domain.models.PayResult
import java.util.UUID

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val rexPay = RexPay.getInstance(this)
        setContent {
            RexAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Button(
                        modifier = Modifier.wrapContentSize(),
                        onClick = {
                            rexPay.setPaymentListener(object : RexPay.RexPayListener {
                                override fun onResult(result: PayResult) {
                                    when (result) {
                                        is PayResult.Error -> {
                                            Log.e("RexPay", "Transaction Failed: $result")
                                        }

                                        is PayResult.Success -> {
                                            Log.i("RexPay", "Transaction Success: $result")
                                        }
                                    }
                                }
                            })
                            val payload = PayPayload(
                                reference = UUID.randomUUID().toString().replace(Regex("\\W"), ""),
                                amount = 100,
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