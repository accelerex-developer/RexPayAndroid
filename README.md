# :credit_card: Rexpay Plugin for Android

![Screenshot of My App](screenshots/screenshot_1.png)

Android SDK to process RexPay payment.

## :rocket: Installation

More information will be out soon on this.

## :rocket: Initializing the SDK

Create your config class.

```kotlin
    val config = ConfigProp.Builder(this)
        .apiUsername("Authorization Username")
        .apiPassword("Authorization Password")
        .isTest(true)
        .clientPGPPrivateKey(assets.open("client secret key FileInputStream"))
        .clientPGPPublicKey(assets.open("client public key FileInputStream"))
        .rexPayPGPPublicKey(assets.open("RexPay public key FileInputStream"))
        .passphrase("Encryption Passphrase")
        .build()
```

You can also create your config with:

```kotlin
    val config = ConfigProp.Builder(this)
        .apiUsername("Authorization Username")
        .apiPassword("Authorization Password")
        .isTest(true)
        .clientPGPPrivateKey("client secret key string")
        .clientPGPPublicKey("client public key string")
        .rexPayPGPPublicKey("RexPay public key string")
        .passphrase("Encryption Passphrase")
        .build()
```

You can also create your config with:

```kotlin
    val config = ConfigProp.Builder(this)
        .apiUsername("Authorization Username")
        .apiPassword("Authorization Password")
        .isTest(true)
        .clientPGPPrivateKey("client secret key file")
        .clientPGPPublicKey("client public key file")
        .rexPayPGPPublicKey("RexPay public key file")
        .passphrase("Encryption Passphrase")
        .build()
```