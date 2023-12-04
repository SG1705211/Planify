pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        maven("https://jogamp.org/deployment/maven")
    }

    plugins {
        id("org.gradle.toolchains.foojay-resolver-convention") version("0.7.0")
        kotlin("jvm").version("1.9.0") apply false
        kotlin("plugin.serialization").version("3.6.1") apply false
        id("org.jetbrains.compose").version("1.5.3")apply false
        id("io.ktor.plugin").version("2.3.6")apply false
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "CS346-project"
include( "KtorServer", "application","models")
