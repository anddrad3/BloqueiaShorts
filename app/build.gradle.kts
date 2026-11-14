import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

/*
 * Chave de assinatura oficial (fica FORA do repositório).
 * Procura em ~/.bloqueiashorts/keystore.properties. Sem ela, o release é assinado
 * com a chave de teste (debug), para qualquer pessoa conseguir compilar o projeto.
 */
val keystoreFile = File(System.getProperty("user.home"), ".bloqueiashorts/keystore.properties")
val keystoreProps = Properties().apply {
    if (keystoreFile.exists()) keystoreFile.inputStream().use { load(it) }
}

android {
    namespace = "com.bloqueiashorts"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.bloqueiashorts"
        minSdk = 24
        targetSdk = 35
        versionCode = 4
        versionName = "2.0"
    }

    signingConfigs {
        if (keystoreFile.exists()) {
            create("release") {
                storeFile = file(keystoreProps.getProperty("storeFile"))
                storePassword = keystoreProps.getProperty("storePassword")
                keyAlias = keystoreProps.getProperty("keyAlias")
                keyPassword = keystoreProps.getProperty("keyPassword")
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            signingConfig = signingConfigs.findByName("release") ?: signingConfigs.getByName("debug")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("androidx.transition:transition:1.5.1")
    implementation("com.google.android.material:material:1.12.0")
}
