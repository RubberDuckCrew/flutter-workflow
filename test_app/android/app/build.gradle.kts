plugins {
    id("com.android.application")
    id("kotlin-android")
    // The Flutter Gradle Plugin must be applied after the Android and Kotlin Gradle plugins.
    id("dev.flutter.flutter-gradle-plugin")
}

android {
    namespace = "com.example.test_app"
    compileSdk = flutter.compileSdkVersion
    ndkVersion = flutter.ndkVersion

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
    }

    defaultConfig {
        // TODO: Specify your own unique Application ID (https://developer.android.com/studio/build/application-id.html).
        applicationId = "com.example.test_app"
        // You can update the following values to match your application needs.
        // For more information, see: https://flutter.dev/to/review-gradle-config.
        minSdk = flutter.minSdkVersion
        targetSdk = flutter.targetSdkVersion
        versionCode = flutter.versionCode
        versionName = flutter.versionName
    }


    // Skip signing via -PskipSigning=true
    val skipSigning = project.hasProperty("skipSigning") && project.property("skipSigning") == "true"

    signingConfigs {
        if (!skipSigning) {
            create("release") {
                val keystoreProperties = Properties()
                val keystorePropertiesFile = rootProject.file("key.properties")
                if (keystorePropertiesFile.exists()) {
                    keystoreProperties.load(FileInputStream(keystorePropertiesFile))
                }
                storeFile = file("key.jks")
                keyAlias = keystoreProperties["keyAlias"] as? String
                keyPassword = keystoreProperties["keyPassword"] as? String
                storePassword = keystoreProperties["storePassword"] as? String
            }
        }
    }

    buildTypes {
        release {
            if (!skipSigning) {
                signingConfig = signingConfigs.getByName("release")
            } else {
                println("Building unsigned release (signing skipped).")
            }
        }
    }

    flavorDimensions += "default"
    productFlavors {
        create("development") {
            dimension = "default"
            applicationIdSuffix = ".dev"
            resValue("string", "app_name", "Test App Dev")
        }
        create("staging") {
            dimension = "default"
            applicationIdSuffix = ".stg"
            resValue("string", "app_name", "Test App Test")
        }
        create("production") {
            dimension = "default"
            resValue("string", "app_name", "Test App Test")
        }
    }
}

flutter {
    source = "../.."
}
