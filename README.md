# ConnectTag DeepLink Library (KMP)

[![JitPack](https://jitpack.io/v/connecttag/deeplink-lib-kmp.svg)](https://jitpack.io/#connecttag/deeplink-lib-kmp)
[![Kotlin](https://img.shields.io/badge/kotlin-2.4.20-blue.svg)](https://kotlinlang.org)
[![Platform](https://img.shields.io/badge/platform-android%20%7C%20jvm%20%7C%20ios-lightgrey.svg)](https://kotlinlang.org/docs/multiplatform.html)

A lightweight, multiplatform deep link parsing, URL scheme handling, and navigation route resolution engine for Kotlin Multiplatform (Android, JVM, iOS).

---

## Features

- **DeepLinkEngine**: Safe URI parsing for custom schemes (`app://host/path?query`).
- **DeepLinkRouter**: Pattern matching and route dispatching with flexible handler mapping.
- **RouteStringCodec**: Query string and argument encoding/decoding with RFC-compliant percent codec.
- **SharedRouteStringParser**: Normalization, alias resolution, and multi-package prefix stripping.
- **Android Intent Support**: Direct `Intent.dataString` resolution extensions.

---

## Installation

Add JitPack repository to your root `settings.gradle.kts`:

```kotlin
dependencyResolutionManagement {
    repositories {
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}
```

Add the dependency to your module's `build.gradle.kts`:

```kotlin
kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation("com.github.connecttag:deeplink-lib-kmp:1.0.0")
        }
    }
}
```

---

## Usage

### 1. Parsing Deep Links
```kotlin
val result = DeepLinkEngine.parseResult("app://payment/checkout?amount=250&currency=YER")

when (result) {
    is DeepLinkResult.Success -> {
        val host = result.action.host // "payment"
        val path = result.action.path // "/checkout"
        val amount = result.action.getIntParam("amount") // 250
    }
    DeepLinkResult.Unrecognized -> { /* Handle unrecognized URI */ }
    is DeepLinkResult.Error -> { /* Handle parsing error */ }
}
```

### 2. Deep Link Routing
```kotlin
val router = DeepLinkRouter()

router.register("payment/checkout") { action ->
    val amount = action.getIntParam("amount") ?: 0
    navigateToCheckout(amount)
    true
}

router.register("profile") { action ->
    navigateToProfile()
    true
}

// Automatically dispatches to matching handler:
router.handle("app://payment/checkout?amount=250")
```

---

## License

```
Copyright 2026 ConnectTag.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0
```
