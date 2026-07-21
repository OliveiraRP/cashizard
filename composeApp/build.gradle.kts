import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.Properties

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.buildkonfig)
}

kotlin {
    listOf(
        iosArm64(),
        iosSimulatorArm64(),
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }

    android {
        namespace = "com.houseofrafa.cashizard.shared"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()

        compilerOptions {
            jvmTarget = JvmTarget.JVM_11
        }
        androidResources {
            enable = true
        }
        withHostTest {
            isIncludeAndroidResources = true
        }
        withDeviceTestBuilder {
            sourceSetTreeName = "test"
        }.configure {
            instrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.ui)
            implementation(libs.compose.components.resources)
            implementation(libs.compose.uiToolingPreview)

            // Navigation + lifecycle
            // api: Decompose types appear in composeApp's public surface (App takes a
            // RootComponent), so the platform modules need them on their classpath.
            api(libs.decompose)
            implementation(libs.decompose.extensionsCompose)
            implementation(libs.essenty.lifecycleCoroutines)

            // ViewModels. Decompose owns navigation only; screen state and logic
            // live in androidx ViewModels, retained across configuration changes
            // by the component's InstanceKeeper (see presentation/arch).
            api(libs.androidx.lifecycle.viewmodel)

            // DI
            implementation(libs.koin.core)
            implementation(libs.koin.compose)

            // Backend
            implementation(project.dependencies.platform(libs.supabase.bom))
            implementation(libs.supabase.auth)
            implementation(libs.supabase.postgrest)
            implementation(libs.ktor.client.core)

            // Kotlinx
            implementation(libs.kotlinx.datetime)
            implementation(libs.kotlinx.serialization.json)

            // Icons (Lucide)
            implementation(libs.icons.lucide)
        }
        androidMain.dependencies {
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.compose.uiTooling)
            implementation(libs.ktor.client.okhttp)
        }
        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

dependencies {
    androidRuntimeClasspath(libs.compose.uiTooling)
}

// supabase-kt 3.1.4 is compiled against kotlinx-datetime 0.6.x and calls
// InstantIso8601Serializer, which 0.7 removed. A transitive 0.7 wins on some
// configurations, which compiles fine and then dies with NoClassDefFoundError
// restoring the session. Pin one version so compile and runtime agree.
configurations.configureEach {
    resolutionStrategy {
        force("org.jetbrains.kotlinx:kotlinx-datetime:${libs.versions.kotlinxDatetime.get()}")
    }
}

// ---------------------------------------------------------------------------
// Supabase credentials -> BuildKonfig constants.
// Never commit real values: they live in local.properties (gitignored) or env.
// ---------------------------------------------------------------------------
val localProperties = Properties().apply {
    val file = rootProject.file("local.properties")
    if (file.exists()) file.inputStream().use { load(it) }
}

fun secret(propertyKey: String, envKey: String): String =
    (localProperties.getProperty(propertyKey) ?: System.getenv(envKey) ?: "").trim()

val supabaseUrl = secret("supabase.url", "SUPABASE_URL")
val supabaseAnonKey = secret("supabase.anonKey", "SUPABASE_ANON_KEY")

require(supabaseUrl.isNotBlank() && supabaseAnonKey.isNotBlank()) {
    """

    Cashizard build failed: missing Supabase credentials.
    Add these to local.properties (see local.properties.example):

        supabase.url=https://YOUR-PROJECT.supabase.co
        supabase.anonKey=YOUR-ANON-KEY

    (or export SUPABASE_URL / SUPABASE_ANON_KEY environment variables).
    """.trimIndent()
}

buildkonfig {
    packageName = "com.houseofrafa.cashizard.config"
    objectName = "BuildKonfig"

    defaultConfigs {
        buildConfigField(STRING, "SUPABASE_URL", supabaseUrl)
        buildConfigField(STRING, "SUPABASE_ANON_KEY", supabaseAnonKey)
    }
}
