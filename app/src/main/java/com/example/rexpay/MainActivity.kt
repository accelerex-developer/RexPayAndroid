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
import com.example.rexpay.ui.theme.RexAppTheme
import com.octacore.rexpay.RexPay
import com.octacore.rexpay.domain.models.ConfigProp
import com.octacore.rexpay.domain.models.PayPayload
import com.octacore.rexpay.domain.models.PayResult
import java.util.UUID

class MainActivity : ComponentActivity(), RexPay.RexPayListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val config = ConfigProp.Builder(this)
            .apiUsername("talk2phasahsyyahoocom")
            .apiPassword("f0bedbea93df09264a4f09a6b38de6e9b924b6cb92bf4a0c07ce46f26f85")
            .baseUrl("https://pgs-sandbox.globalaccelerex.com/api/")
            .clientPGPPrivateKey(assets.open("0xE14294FA-sec.asc"))
            .clientPGPPublicKey(assets.open("0xE14294FA-pub.asc"))
            .rexPayPGPPublicKey(assets.open("0xE14294FA-rex.asc"))
            .passphrase("pgptool77@@")
            .build()

        RexPay.init(this, config)

        val payload = PayPayload(
            reference = UUID.randomUUID().toString()
                .replace(Regex("\\W"), ""),
            amount = 100,
            currency = "NGN",
            userId = "random.user@email.com",
            callbackUrl = "",
            email = "random.user@email.com",
            customerName = "Random User"
        )

        val rexPay = RexPay.getInstance()
        rexPay.setPaymentListener(this@MainActivity)

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
                            rexPay.makePayment(this@MainActivity, payload)
                        }
                    ) {
                        Text(text = "PAY")
                    }
                }
            }
        }
    }

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
}