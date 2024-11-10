plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlinSerialization)
}

kotlin {
    js(IR) {
        moduleName = "composeApp"
        browser {
            useEsModules()

            webpackTask {
                mainOutputFileName = "composeApp.js"
                sourceMaps = false
            }
        }

        binaries.executable()
    }

    sourceSets {
        
        commonMain.dependencies {
            implementation(libs.kotlinx.coroutines)
            implementation(libs.kotlinx.datetime)

            implementation(libs.ndkore)
            implementation(libs.ornament)
            implementation(libs.classcharts.api)

            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)

            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.runtime.compose)

            implementation(libs.compose.rich.editor)
            implementation(libs.compose.coil)
            implementation(libs.compose.coil.network.ktor3)
        }
    }
}