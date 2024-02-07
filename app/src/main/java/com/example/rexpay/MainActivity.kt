package com.example.rexpay

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
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
import com.google.gson.Gson
import com.octacore.rexpay.domain.models.ConfigProp
import com.octacore.rexpay.domain.models.PayResult
import org.bouncycastle.bcpg.ArmoredOutputStream
import org.bouncycastle.bcpg.BCPGOutputStream
import org.bouncycastle.bcpg.SymmetricKeyAlgorithmTags
import org.bouncycastle.openpgp.PGPEncryptedDataGenerator
import org.bouncycastle.openpgp.PGPException
import org.bouncycastle.openpgp.PGPLiteralData
import org.bouncycastle.openpgp.PGPLiteralDataGenerator
import org.bouncycastle.openpgp.PGPObjectFactory
import org.bouncycastle.openpgp.PGPPublicKey
import org.bouncycastle.openpgp.PGPPublicKeyRing
import org.bouncycastle.openpgp.PGPUtil
import org.bouncycastle.openpgp.operator.bc.BcKeyFingerprintCalculator
import org.bouncycastle.openpgp.operator.bc.BcPGPDataEncryptorBuilder
import org.bouncycastle.openpgp.operator.bc.BcPublicKeyKeyEncryptionMethodGenerator
import java.io.BufferedOutputStream
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.util.Date
import java.util.UUID

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val config = ConfigProp.Builder(this)
            .username("talk2phasahsyyahoocom")
            .password("f0bedbea93df09264a4f09a6b38de6e9b924b6cb92bf4a0c07ce46f26f85")
            .baseUrl("https://pgs-sandbox.globalaccelerex.com/api/")
            .publicKey(
                "-----BEGIN PGP PUBLIC KEY BLOCK-----\n" +
                        "Version: Keybase OpenPGP v2.0.76\n" +
                        "Comment: https://keybase.io/crypto\n" +
                        "\n" +
                        "xsBNBGW+bHQBCADlCkdoGghiYXWy59CkM/6YtoDKuibOO3igONcDY1FfBcKv+jLh" +
                        "kvXhXeHQoSJhroDZ0ls/8Gr3OAeoaoGrqwuCbrGBEvtWZAVyXr8e5oLBSvzSZafe" +
                        "54zeJDK26ZMJCFAlc8IEliavrPrK6KJ8A67ZZ2WwQ37zulY91d5GzIbCL6O6RFl5" +
                        "MihVjUzZ9726rcJrDtkWduTLjNieopWShu2aNpJouLw4KaoINurK30TO3F5ETD8R" +
                        "gOEI2n76RtXpqfIs3/pQIhgeblVLaS5+0vyQVQ9g834w0HFdktpny2ALnmwitWO1" +
                        "d0RvFO5w6ykY9JNmgPT7tWoNVBZaxaBj9Wg9ABEBAAHNKEJhYmF0dW5kZSA8YWJk" +
                        "dWxsYWhpamltb2gzLmphQGdtYWlsLmNvbT7CwHoEEwEKACQFAmW+bHQCGy8DCwkH" +
                        "AxUKCAIeAQIXgAMWAgECGQEFCQAAAAAACgkQ4MlUleFClPq5bgf9GcI0ImMgrifH" +
                        "6GCiF2xSLfmRpfzupqe0p32YUr7Dw7GxFB+VC1eyaGi6JZ1HZxr7mgQa4ZzrOKQK" +
                        "VzuLBuydueUBLH3ZDqZUoxrl43wBsPdNaLq1ANYOcLM+nEozd9iUhK1ggZzHl3u7" +
                        "Nx6lLXV8q3qt42+rrz5r7L33JnSWKXokBBGzHUJNJzqjU7/hXD5gFIimp4ozH4su" +
                        "6fVptU9u/rwm0CPg96BiFzUm9nnzvWxBut6ZtlMkpa8oYTz7lPwfwM+vRJDDGm18" +
                        "PElPIVNS02nOXG9gDf2ItQmc+RNVH3YOs3812oyhio2YNjT1RYv5s5SYIVuJfNJd" +
                        "O/9uJ5VxFc7ATQRlvmx0AQgAtQK4QqGE1dyjQDiI4VBMaCIB9O9OO9sQHq0elIFF" +
                        "85Pn2gfijxTmGG7SJHZle08Mc/MgQxj9yAqFvsff7VS6GFxb/EABdJ/YN1wEco76" +
                        "s7pw8+jpZPQhJBastZitSLvWO3KWUj6YcFcc7YnsJ3h4VZ1FyNqMMBOZvEmOHruG" +
                        "NY0MXOmexMfoEexREbNviBWvPm66baWz9w4ukjxIWkLtevIQeTUqkL5fzi4QZEIe" +
                        "+KXuxt3Cdlclj6Evdro25QnbuxcsdqYVG5R652tShLS6GTVtiWNwu2lt56gnx6Jf" +
                        "PWJC+drYFv2liGrxzMbPioKd+0LIoLJliH07kWMHLhhXYQARAQABwsGEBBgBCgAP" +
                        "BQJlvmx0BQkAAAAAAhsuASkJEODJVJXhQpT6wF0gBBkBCgAGBQJlvmx0AAoJELwc" +
                        "b75ys8s36UsH/Ro52EnjYj8AvMuHV7sonrW0FGldgZFPZjk637mMrd7LIRaSPD36" +
                        "q4lFXy6fim8Eak5F55q2/woj+fqI++IPGYYKsto9VlZ2M8GQXTv1EKfZWoWIQOyH" +
                        "AnzWBSMlXwLUdjamB6TsmvBJZ5ggGxC9+tKEoF15AHd+UX6B6jd0dPz9uAGhjXUV" +
                        "SBj66OYj2jk2Ay/7EUcgPa5uWWNQRMEbDx02PuoX/d0Vr/pU4/q0s+Mw8QViT+ZQ" +
                        "nqzHsvH3NN0/OsYqBAcv/OtycS0odaKnfoskDMNNbgUbPLRbqkfVPBIXsC+1tz0f" +
                        "CWHP8EsntCj5xjOUpRoPjln5IssJPGFY9fuRcwf/QriRa/kjXvVakU/akXjVwIfr" +
                        "DsAU4CzLnGIb+Tz0BEiZUUdsWi6X++jMd7T4ZUnKPwrSdjEpVEuX1wJ6YWemQ5nY" +
                        "/B2asRTChGwR4yztJESO2vYqPjpyC8b7VIvWaAgKw4I5Gq8wG+tmoBABR1HupZAB" +
                        "oX7DpHmXTH9mlPwv/CDCIjBVtSAEEdoWaFsMjjzixC5orb60yWQ2W7Y9JxV5E6T1" +
                        "d5V5XNLY6QGLf9qAZrxDWmJADe4cufdrz3IErIXuoZ6vMB5Xx4zfuIdz99lEOO9S" +
                        "WvfJKW0W0CRJQC5Gt339e8cCw4PLVaLQ9lGsFZX169f/78Bc7uGB+5AkD5fEac7A" +
                        "TQRlvmx0AQgAnGYQ9uby8okzLc40sLCbM9xOmvLPRe4bvQGtt1+VgLOEZkKaXCb9" +
                        "5OikyW1Eq1Twl4NWg0pgSXqN4RA4raR+RyoAtFrLiOVnnsDc5bDP3mbK+elkQj/0" +
                        "Iy8Ly7WPamI9u1NuM+7gQRNS4+edLUNu/+ha/uu0v4xWVtUlF9Lz816Ofyc3cy7A" +
                        "tWwUnMqc93Y97Z1nP+T/kgp1nLs4mGhJ3Eq+BUx9+HHyXL8KTq9Dtva3P0XCJGcL" +
                        "hs1ksRfHIzFxnEtKG0hzKAtWYM7D2Tt2cSmg7d/7Hb7IuYAAZVE1JyyCbSVrUAhQ" +
                        "qCfR19c3HHPItnX0XxrMZdNtXFx00CHCOQARAQABwsGEBBgBCgAPBQJlvmx0BQkA" +
                        "AAAAAhsuASkJEODJVJXhQpT6wF0gBBkBCgAGBQJlvmx0AAoJEEuul7Yse7pm4NwH" +
                        "+gLdnvt23skooYMgobVnWw3nxW5OcQ0UKnYxUVhjRUZBL9pJAAErWO+ArLh8Kx66" +
                        "ld1iN4lP5DbLfjb2J6jNZ0NZk2rh7gw1kjKVYlJ9sPt9A++ZqmS03vaxO9IfJJYW" +
                        "MW5bRJQNcj7tCo+TvzZX7yrvs+i1K5v4EoVuNc6xD5y/GKnNG9wvnrd6Sc80U5i4" +
                        "BQ3Kg+k++5D/yH6Gb7Y5TUtVm9v85sSaxh4Jupk2NcimQRV6tNwuyFt2oOfdG3sJ" +
                        "LVi+4lCF+jj2RTE7suF+n8lA26sx7OEMXVDKzlkAmrH7CUNKTvq4hZ1nZyd9Av1y" +
                        "Y0TBBeQ2uGPYCnDBMf2yz51lmAf6A4kbvXjCOb3JfAnsLfcgyE3sfA/1zlpD1j8W" +
                        "RwkGA0nnTv7l136iSq3xHrCr7HURY9TS5hM70LImPOFn3t2MstIav8ghkPB+MGT8" +
                        "Q7fXaMlO8IWeeNM1zwSOFpjhbl8nqwkGM2d2MmE4IZTwT0h+Noj9Jq/ImmIJ66DW" +
                        "KTZ4+2R2da3TdKKwPj5DgZl6fgflpY3vklqVmt9Sr+YMoui4++tkaTvVwa1ygekG" +
                        "1TwD0l8Ak/sem/96ASoAs4aR38kMbM3hUYar7qVQcOEt3xwiCTNFBrZp1knTx8u1" +
                        "MU+nKOOsmgTzrMfG37c1W1UvkIkoneid8hkI+lgHvFMS4ZDB8g==" +
                        "=1KRA\n" +
                        "-----END PGP PUBLIC KEY BLOCK-----"
            )
            .privateKey(
                "-----BEGIN PGP PRIVATE KEY BLOCK-----\n" +
                        "Version: Keybase OpenPGP v2.0.76\n" +
                        "Comment: https://keybase.io/crypto\n" +
                        "\n" +
                        "xcMGBGW+bHQBCADlCkdoGghiYXWy59CkM/6YtoDKuibOO3igONcDY1FfBcKv+jLh" +
                        "kvXhXeHQoSJhroDZ0ls/8Gr3OAeoaoGrqwuCbrGBEvtWZAVyXr8e5oLBSvzSZafe" +
                        "54zeJDK26ZMJCFAlc8IEliavrPrK6KJ8A67ZZ2WwQ37zulY91d5GzIbCL6O6RFl5" +
                        "MihVjUzZ9726rcJrDtkWduTLjNieopWShu2aNpJouLw4KaoINurK30TO3F5ETD8R" +
                        "gOEI2n76RtXpqfIs3/pQIhgeblVLaS5+0vyQVQ9g834w0HFdktpny2ALnmwitWO1" +
                        "d0RvFO5w6ykY9JNmgPT7tWoNVBZaxaBj9Wg9ABEBAAH+CQMIjIMxLnRdV4BgDCLd" +
                        "fCiZzmAGsWrSEv/dNfSJtVcf77W2t39m3F33r4WhCEPg9ikFzLtXOZbJTcQweOQp" +
                        "meyFbJI5wgXc+xAQyIFTWdaA6g2U2xl2xVKTw+5DWpS1N0y9IAIEtkqydmaOFd3h" +
                        "sPc/zHNilV7RvwFi7cvqnwGSXAULu7oxmhbl5myreSBgJixH+i/a4bH7XaJPAc4K" +
                        "RWibBQpN5tgSZ8LaQCY8SbAnWv7Pt2jyjTvq3whqZRY517BaDg1xzmJmLfMDoLsd" +
                        "LYz9NBTa6MSreynTO5o8ZwnBjQKbcmMZnfHwurMGQU5x2Ys5EoedQ5pvLTEE2DWq" +
                        "xuOn5zLNtCqOPOmhWDhmECgin0NvEQHSYHsC4wKWUYOpoZuO1QQcuAz/3qk2zIg0" +
                        "MjyaWmfHYXFzK4rQpOPH+BfmZpdmpPTfXHY38skLShcOJFEJDZ6slaGGAZk/qYT/" +
                        "An0vUUrtKL5XdKUC5rd9l3Zxm3cY3cOlc++fNxGh56WWNg1I+XuOhH3FX/2SHAq/" +
                        "EuRGc3FtSp3kZ1C5tMOzv3bULXHb7yvTH5RCCI9H6e9R1qaeo4Y+ilAChA2qFCKI" +
                        "RnlozvH85UuLifPS8fM+eXJ+aDtjKpKAHlHybGXdK8EFfrcp1DR3NpVmnSiIvGjK" +
                        "IE9kC5s3H5LBsBgXdmS1OyqRbZQcARNdRIvsy4tf94sSppqIsBKd3KgHba4vXHXe" +
                        "+bFa3Djn1HSzgY7rwz0ZVGgsf7ZAjZzell/kp35BNdksablaxmFdZQXa/Ckqo5Qj" +
                        "LXepCiG7k5dxJJj7Jd148JyjHJPVS1VFv1avDvlfLHQEeIZ78WeXmgCYoQF2b0z8" +
                        "m8NQHyfuVVQBgRPl8IflCP1rTjAaYFpQ37sAiXPdb8ncKoq5u7JHHowoLoCLaTel" +
                        "37TxmtBbOHdazShCYWJhdHVuZGUgPGFiZHVsbGFoaWppbW9oMy5qYUBnbWFpbC5j" +
                        "b20+wsB6BBMBCgAkBQJlvmx0AhsvAwsJBwMVCggCHgECF4ADFgIBAhkBBQkAAAAA" +
                        "AAoJEODJVJXhQpT6uW4H/RnCNCJjIK4nx+hgohdsUi35kaX87qantKd9mFK+w8Ox" +
                        "sRQflQtXsmhouiWdR2ca+5oEGuGc6zikClc7iwbsnbnlASx92Q6mVKMa5eN8AbD3" +
                        "TWi6tQDWDnCzPpxKM3fYlIStYIGcx5d7uzcepS11fKt6reNvq68+a+y99yZ0lil6" +
                        "JAQRsx1CTSc6o1O/4Vw+YBSIpqeKMx+LLun1abVPbv68JtAj4PegYhc1JvZ5871s" +
                        "QbrembZTJKWvKGE8+5T8H8DPr0SQwxptfDxJTyFTUtNpzlxvYA39iLUJnPkTVR92" +
                        "DrN/NdqMoYqNmDY09UWL+bOUmCFbiXzSXTv/bieVcRXHwwYEZb5sdAEIALUCuEKh" +
                        "hNXco0A4iOFQTGgiAfTvTjvbEB6tHpSBRfOT59oH4o8U5hhu0iR2ZXtPDHPzIEMY" +
                        "/cgKhb7H3+1UuhhcW/xAAXSf2DdcBHKO+rO6cPPo6WT0ISQWrLWYrUi71jtyllI+" +
                        "mHBXHO2J7Cd4eFWdRcjajDATmbxJjh67hjWNDFzpnsTH6BHsURGzb4gVrz5uum2l" +
                        "s/cOLpI8SFpC7XryEHk1KpC+X84uEGRCHvil7sbdwnZXJY+hL3a6NuUJ27sXLHam" +
                        "FRuUeudrUoS0uhk1bYljcLtpbeeoJ8eiXz1iQvna2Bb9pYhq8czGz4qCnftCyKCy" +
                        "ZYh9O5FjBy4YV2EAEQEAAf4JAwi0V3G/e2IeMmDI90uEqTQQlVHMhfL+NZV1gWiX" +
                        "OiySJGT8VXw9axRU/LVeTqIKGY+R8fWNfOmL/8yJ7rt1nsdsc3WG/UIXGcDfBukf" +
                        "yFl+ZtKKaZN3xKUmk69MYuMp12nViRGiwZc1FNG7/olnmecF1S4LR7k6GL4rXapw" +
                        "k6boWFgVI1DpCJhIcG1GqH/3/wbBhRWei5XqovDqyXwgUYsy4x0Psus1DZuHe+9J" +
                        "8U0u7MZTLfIxCpNe16R/JecKUYIAPedzUOU57+jwsIU80um7yaxonm5F2kFgQAeZ" +
                        "CBTEvvCtfzkpuDqPl6uNjbkhaFlm1mCu8dCXBMtAHgmivJv5YYIS32Laa1CRC90f" +
                        "07RSTBFBrIzGSMxx19U4aFPmyVZiIBYVAtLab7RcPeqT7Ah5jaif5+WwWAhudbGZ" +
                        "HAUT4xsUZrHjkY5WhbWQ+leBVRMxl1CNzqbBIUHd5trEDB9ltS7H8XwLB+Auvxwy" +
                        "d5zTRvmUx8cGXbLnpFbM7TL3pneixIzlVp3dyAO+eqUbP6/FIou7cQkA85ftouno" +
                        "LdL8sa9b1nxkU39hL8LKpNG1+LPRTcw6YSCDHtYnnMFhTa2HGfbZFW0Bw8yhLRlf" +
                        "GCTADkVK/aT02pRHGgBf0jgaUStjPw0z6v+uWhoXKubi6pn+kATbpy0rtZNxBk5M" +
                        "OJ4936yl9Bbu6BmQNLTwIYLe9QXyR4tsmC813XJg4ETBtwhuDMaLqPsl/rq/CEhb" +
                        "Phf0ZMeMoWWlnzPk7ax9UA1WjX84No6KzuC0CO7p8v5EqV2J86Me9JivCsEd5NbT" +
                        "R90eaKoLetb9k5vzI62A0sB/HYzg4a3RiYfrp85oQ8z3EwGvXWIknM3kRCrvi2UJ" +
                        "1jGbVknBRgq7pGETHgZhuaVbyzugpLTaNTjtKuVRGmZ6yG4+tC/8lA3CwYQEGAEK" +
                        "AA8FAmW+bHQFCQAAAAACGy4BKQkQ4MlUleFClPrAXSAEGQEKAAYFAmW+bHQACgkQ" +
                        "vBxvvnKzyzfpSwf9GjnYSeNiPwC8y4dXuyietbQUaV2BkU9mOTrfuYyt3sshFpI8" +
                        "PfqriUVfLp+KbwRqTkXnmrb/CiP5+oj74g8Zhgqy2j1WVnYzwZBdO/UQp9lahYhA" +
                        "7IcCfNYFIyVfAtR2NqYHpOya8ElnmCAbEL360oSgXXkAd35RfoHqN3R0/P24AaGN" +
                        "dRVIGPro5iPaOTYDL/sRRyA9rm5ZY1BEwRsPHTY+6hf93RWv+lTj+rSz4zDxBWJP" +
                        "5lCerMey8fc03T86xioEBy/863JxLSh1oqd+iyQMw01uBRs8tFuqR9U8EhewL7W3" +
                        "PR8JYc/wSye0KPnGM5SlGg+OWfkiywk8YVj1+5FzB/9CuJFr+SNe9VqRT9qReNXA" +
                        "h+sOwBTgLMucYhv5PPQESJlRR2xaLpf76Mx3tPhlSco/CtJ2MSlUS5fXAnphZ6ZD" +
                        "mdj8HZqxFMKEbBHjLO0kRI7a9io+OnILxvtUi9ZoCArDgjkarzAb62agEAFHUe6l" +
                        "kAGhfsOkeZdMf2aU/C/8IMIiMFW1IAQR2hZoWwyOPOLELmitvrTJZDZbtj0nFXkT" +
                        "pPV3lXlc0tjpAYt/2oBmvENaYkAN7hy592vPcgSshe6hnq8wHlfHjN+4h3P32UQ4" +
                        "71Ja98kpbRbQJElALka3ff17xwLDg8tVotD2UawVlfXr1//vwFzu4YH7kCQPl8Rp" +
                        "x8MGBGW+bHQBCACcZhD25vLyiTMtzjSwsJsz3E6a8s9F7hu9Aa23X5WAs4RmQppc" +
                        "Jv3k6KTJbUSrVPCXg1aDSmBJeo3hEDitpH5HKgC0WsuI5WeewNzlsM/eZsr56WRC" +
                        "P/QjLwvLtY9qYj27U24z7uBBE1Lj550tQ27/6Fr+67S/jFZW1SUX0vPzXo5/Jzdz" +
                        "LsC1bBScypz3dj3tnWc/5P+SCnWcuziYaEncSr4FTH34cfJcvwpOr0O29rc/RcIk" +
                        "ZwuGzWSxF8cjMXGcS0obSHMoC1ZgzsPZO3ZxKaDt3/sdvsi5gABlUTUnLIJtJWtQ" +
                        "CFCoJ9HX1zccc8i2dfRfGsxl021cXHTQIcI5ABEBAAH+CQMIJ9zjl2uC3yNgX4wu" +
                        "xZ0hE+MI8LTuFJA9aSNYR+wR72y1YUFIoEwS1o73SQ6EK9RHijUwh4IJdWgM6Bdz" +
                        "D5XbqUXGKM+COmYzyC3o6Dg4rCqk4JnI0fWJVoC+fF/XERZiy8eaaQ6ffZBodhh4" +
                        "rxkDGsEwhCL/QKDrzkfZTiewFP27E2CovsJsOoO0IywRdUZMvPT3gNk0gbZaMQZ5" +
                        "1snnyLqPZCxYXNSSPAQo1ChUq2JgfG5MKHCYh6HOc0gHaV1LyWmg1p5l9A3atv/G" +
                        "LTwBXZYiiavPaxoM6GIY1N2CpFFid8+cJlaoCXNPaMY+THTTAB6vXHAI804FcKLJ" +
                        "AZyyUif1Rg3JLU6GHAQx8uLD3fDvKtBt5C7wyYJosiBW/fIV+sznAKPIo6++WXAW" +
                        "e9LXKbgzdamVFr1Fzw1RAch1gx5ZvdFRA0Gxs9IHmaycqQYeKt2glN75OyxhIX+c" +
                        "0W8JfvkFZjQMCjSMBSpTw7I8GtO5+QvvrryhZ1/pAFY4wFhZMYRgw8R+kpazTpEM" +
                        "GCvtMs+knV7n8OaXRMFzNNIiwAMhZo/w6aZkN5/+J0xyOlLlmb4N3/mT6AXuae0w" +
                        "mARm7NqZ/Iz/P14AimJQ2ZGjm8hWo9hPoFstjZ11pDv7bHydDIU4LEemxvDpgKpU" +
                        "IwEBgcd8UEjfQuyPSbiV3NZsgPm9vK1OzpUshpnEQp1/FKol1ATsQ/DDHpuM+8HW" +
                        "QqhNS30Dg8UjaUYkyAU22cu622lTjTuU4SMhduDOuXLx00ejGdD2Cx+KIELZ1q0L" +
                        "O31oiXSiyqdlrlBa3HFHfHEvHXHbNAHoDmF25F9wXDg0KGOp67rNekoosECd6RVs" +
                        "czdSQLFTC4fO/40iVbbuUg8JX0TeH9Tmw/5+AQLk0ELAYag9l97A3ArdWVeMn6Hx" +
                        "BoXkLmfJzCgewsGEBBgBCgAPBQJlvmx0BQkAAAAAAhsuASkJEODJVJXhQpT6wF0g" +
                        "BBkBCgAGBQJlvmx0AAoJEEuul7Yse7pm4NwH+gLdnvt23skooYMgobVnWw3nxW5O" +
                        "cQ0UKnYxUVhjRUZBL9pJAAErWO+ArLh8Kx66ld1iN4lP5DbLfjb2J6jNZ0NZk2rh" +
                        "7gw1kjKVYlJ9sPt9A++ZqmS03vaxO9IfJJYWMW5bRJQNcj7tCo+TvzZX7yrvs+i1" +
                        "K5v4EoVuNc6xD5y/GKnNG9wvnrd6Sc80U5i4BQ3Kg+k++5D/yH6Gb7Y5TUtVm9v8" +
                        "5sSaxh4Jupk2NcimQRV6tNwuyFt2oOfdG3sJLVi+4lCF+jj2RTE7suF+n8lA26sx" +
                        "7OEMXVDKzlkAmrH7CUNKTvq4hZ1nZyd9Av1yY0TBBeQ2uGPYCnDBMf2yz51lmAf6" +
                        "A4kbvXjCOb3JfAnsLfcgyE3sfA/1zlpD1j8WRwkGA0nnTv7l136iSq3xHrCr7HUR" +
                        "Y9TS5hM70LImPOFn3t2MstIav8ghkPB+MGT8Q7fXaMlO8IWeeNM1zwSOFpjhbl8n" +
                        "qwkGM2d2MmE4IZTwT0h+Noj9Jq/ImmIJ66DWKTZ4+2R2da3TdKKwPj5DgZl6fgfl" +
                        "pY3vklqVmt9Sr+YMoui4++tkaTvVwa1ygekG1TwD0l8Ak/sem/96ASoAs4aR38kM" +
                        "bM3hUYar7qVQcOEt3xwiCTNFBrZp1knTx8u1MU+nKOOsmgTzrMfG37c1W1UvkIko" +
                        "neid8hkI+lgHvFMS4ZDB8g==" +
                        "=QwEB\n" +
                        "-----END PGP PRIVATE KEY BLOCK-----"
            )
            .build()

        RexPay.init(this, config)
        setContent {
            RexAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Column {
                        Button(
                            modifier = Modifier.wrapContentSize(),
                            onClick = {
                                val rexPay = RexPay.getInstance()
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
                                    reference = UUID.randomUUID().toString()
                                        .replace(Regex("\\W"), ""),
                                    amount = 100,
                                    currency = "NGN",
                                    userId = "random.user@email.com",
                                    callbackUrl = "",
                                    email = "random.user@email.com",
                                    customerName = "Random User"
                                )
                                rexPay.makePayment(this@MainActivity, payload)
                            }
                        ) {
                            Text(text = "PAY")
                        }

                        Button(onClick = {
                            /*val crypto = CryptoUtils.getInstance(this@MainActivity)
                            crypto.encryptPayload()*/
                            val fileBytes = readFileFromAssets(this@MainActivity, "rexpay.asc")

                            if (fileBytes != null) {
                                val inputStream = ByteArrayInputStream(fileBytes)

                                // Get PGPPublicKey from .asc file InputStream
                                val publicKey: PGPPublicKey? = getPGPPublicKey(inputStream)

                                if (publicKey != null) {
                                    val payload = mapOf(
                                        "reference" to "17072196txazV3zjUH",
                                        "amount" to "100",
                                        "customerId" to "random.user@email.com",
                                        "cardDetails" to mapOf(
                                            "authDataVersion" to "1",
                                            "pan" to "5555555555555555",
                                            "expiryDate" to "1225",
                                            "cvv2" to "555",
                                            "pin" to "5555"
                                        )
                                    )
                                    val stringifyJson = Gson().toJson(payload)

                                    // Data to be encrypted
                                    val inputData = "Your secret data".toByteArray(Charsets.UTF_8)

                                    // Encrypt data using PGPPublicKey
                                    val encryptedData = encryptData(inputData, publicKey)
                                    Log.i(
                                        "MainActivity",
                                        "EncryptedData:\n${String(encryptedData)}"
                                    )

                                    // Now you can send encryptedData to your backend
                                } else {
                                    // Handle error: Failed to get PGPPublicKey
                                }
                            } else {
                                // Handle error: Failed to read .asc file
                            }

                        }) {
                            Text(text = "Decrypt")
                        }
                    }
                }
            }
        }
    }

    private fun readFileFromAssets(context: Context, filename: String): ByteArray? {
        return try {
            context.assets.open(filename).use { inputStream ->
                inputStream.readBytes().also {
                    Log.i("MainActivity", "File:\n${String(it)}")
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // Function to get PGPPublicKey from .asc file InputStream
    private fun getPGPPublicKey(inputStream: ByteArrayInputStream): PGPPublicKey? {
        /*val publicKeyRingCollection = PGPPublicKeyRingCollection(
            PGPUtil.getDecoderStream(inputStream),
            BcKeyFingerprintCalculator()
        )
        val publicKeyRing = publicKeyRingCollection.keyRings.next() as PGPPublicKeyRing
        val kIt = publicKeyRing.publicKeys.iterator()
        while (kIt.hasNext()) {
            val key = kIt.next()
            if (key.isEncryptionKey && key.isMasterKey.not()) {
                return key
            }
        }
        return null*/
        try {
            val ais = PGPUtil.getDecoderStream(inputStream)
            val pgpObjectFactory = PGPObjectFactory(ais, BcKeyFingerprintCalculator())
            val keyRing = pgpObjectFactory.nextObject() as PGPPublicKeyRing

            val kIt = keyRing.publicKeys
            while (kIt.hasNext()) {
                val key = kIt.next() as PGPPublicKey
                if (key.isEncryptionKey) {
                    return key
                }
            }
            return null
        } catch (e: PGPException) {
            e.printStackTrace()
        }

        return null
    }

    // Function to encrypt data using PGPPublicKey
    private fun encryptData(data: ByteArray, encKey: PGPPublicKey): ByteArray {
        val bytesOutput = ByteArrayOutputStream()
        val armoredOutputStream = ArmoredOutputStream(BufferedOutputStream(bytesOutput, 1 shl 16))

        val encryptedDataGenerator =
            BcPGPDataEncryptorBuilder(SymmetricKeyAlgorithmTags.AES_256).apply {
                /*setWithIntegrityPacket(true)
                setProvider(BouncyCastleProvider.PROVIDER_NAME)*/
            }.let {
                PGPEncryptedDataGenerator(it)
            }.also {
                it.addMethod(BcPublicKeyKeyEncryptionMethodGenerator(encKey))
            }
        val encryptedOutput = encryptedDataGenerator.open(armoredOutputStream, ByteArray(1 shl 16))
        val bcpgOutputStream = BCPGOutputStream(encryptedOutput)

        val literalDataGenerator = PGPLiteralDataGenerator()
        val literalDataGeneratorOutput = literalDataGenerator.open(
            bcpgOutputStream,
            PGPLiteralData.BINARY,
            PGPLiteralData.CONSOLE,
            Date(),
            ByteArray(1 shl 16)
        )
        literalDataGeneratorOutput.write(data)
        literalDataGenerator.close()

        bcpgOutputStream.close()
        encryptedOutput.close()
        encryptedDataGenerator.close()

        //Close everything we created
        armoredOutputStream.close()
        bytesOutput.close()

        return bytesOutput.toByteArray()
    }
}