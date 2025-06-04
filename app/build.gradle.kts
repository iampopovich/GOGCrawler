plugins {
    alias(libs.plugins.android.application)
    id("org.jlleitschuh.gradle.ktlint") version "12.3.0"
}

ktlint {
    version.set("1.6.0")
    // Add any other Ktlint configurations here
}

android {
    namespace = "com.example.gogcrawler"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.gogcrawler"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.1.1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures {
        viewBinding = true
    }
}

tasks.register("ktlintCheck") {
    dependsOn("ktlintFormat")
    // This task will run ktlintFormat which includes check and format.
    // If you only want to check, you can depend on "ktlintCheck" from the plugin,
    // but since we usually want to format, depending on ktlintFormat is more practical.
}

dependencies {
    implementation(libs.glide)
    implementation(libs.volley)
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    implementation(libs.activity)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}