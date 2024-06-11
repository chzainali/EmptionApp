plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")

}

android {
    namespace = "com.example.emotionapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.emotionapp"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
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

dependencies {

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.google.firebase:firebase-crashlytics:18.6.2")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    implementation(platform("com.google.firebase:firebase-bom:32.2.2"))

    implementation("com.google.firebase:firebase-database")
    implementation("com.google.firebase:firebase-storage")
    implementation("com.google.firebase:firebase-auth-ktx:21.1.0")
    implementation("com.googlecode.libphonenumber:libphonenumber:8.12.28")
    implementation("com.github.bumptech.glide:glide:4.14.2")

    implementation("de.hdodenhof:circleimageview:3.1.0")
    implementation("com.hbb20:ccp:2.6.0")

    val nav_version = "2.5.3"

    implementation("androidx.navigation:navigation-fragment:$nav_version")
    implementation("androidx.navigation:navigation-ui:$nav_version")

    implementation("com.applandeo:material-calendar-view:1.9.0-rc03")

    implementation(platform("org.jetbrains.kotlin:kotlin-bom:1.8.0"))
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.8.0")

    implementation("com.github.dhaval2404:imagepicker:2.1")

    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")
    implementation("com.google.code.gson:gson:2.8.8")
    implementation("com.google.android.exoplayer:exoplayer:2.19.1")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.8.0")

}