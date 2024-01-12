import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import com.google.cloud.tools.gradle.appengine.appyaml.AppEngineAppYamlExtension

val ktorVersion: String by project
val kotlinVersion: String by project
val logbackVersion: String by project
val kmongoVersion: String by project
val commonsCodecVersion: String by project
val versionApp: String by project
val uberJarFileName: String = "rmc-api-2-all.jar"

// The `apply` approach of adding plugins is the older, yet more flexible method of adding a plugin to
// your build. This is the required approach unless your desired plugin is available on Gradle's Plugin Repo.
// Unfortunately the appengine gradle plugin is not available on Gradle's Plugin Repository:
// https://github.com/GoogleCloudPlatform/app-gradle-plugin
buildscript {
    repositories { mavenCentral() }
    dependencies { classpath("com.google.cloud.tools:appengine-gradle-plugin:2.5.0") }
}
apply {
    plugin("com.google.cloud.tools.appengine")
}

// Note: The `plugins` block is the newer method of applying plugins, but in order to be able to add a plugin
// via this mechanism they must be available on the Gradle Plugin Repository: http://plugins.gradle.org/
// where possible, plugins should be added via this section
plugins {
    kotlin("jvm") version "1.9.22"
    id("io.ktor.plugin") version "2.3.7"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.22"

    // Shadow plugin - enable support for building our UberJar
    id("com.github.johnrengelman.shadow") version "5.2.0"
}

group = "com.digitalarchitects"
version = versionApp

application {
    mainClass.set("io.ktor.server.netty.EngineMain")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    implementation("io.ktor:ktor-server-core-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-auth-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-auth-jwt-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-content-negotiation-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-call-logging-jvm:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm:$ktorVersion")
    implementation("org.litote.kmongo:kmongo-id-serialization:4.11.0")
    implementation("io.ktor:ktor-server-netty-jvm:$ktorVersion")
    implementation("ch.qos.logback:logback-classic:$logbackVersion")
    implementation("org.mongodb:bson-kotlinx:4.11.0")
    implementation("org.jetbrains.exposed:exposed-kotlin-datetime:0.44.0")
    testImplementation("io.ktor:ktor-server-tests-jvm:$ktorVersion")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlinVersion")

    implementation("org.litote.kmongo:kmongo:$kmongoVersion")
    implementation("org.litote.kmongo:kmongo-coroutine:$kmongoVersion")

    implementation("commons-codec:commons-codec:$commonsCodecVersion")
}

// Configure the "shadowJar" task to properly build our UberJar
// we effectively want a jar with zero dependencies we can run and will "just work"
tasks {
    val shadowJarTask = named<ShadowJar>("shadowJar") {
        // explicitly configure the filename of the resulting UberJar
        archiveFileName.set(uberJarFileName)

        // Appends entries in META-INF/services resources into a single resource. For example, if there are several
        // META-INF/services/org.apache.maven.project.ProjectBuilder resources spread across many JARs the individual
        // entries will all be concatenated into a single META-INF/services/org.apache.maven.project.ProjectBuilder
        // resource packaged into the resultant JAR produced by the shading process -
        // Effectively ensures we bring along all the necessary bits from Jetty
        mergeServiceFiles()

        // As per the App Engine java11 standard environment requirements listed here:
        // https://cloud.google.com/appengine/docs/standard/java11/runtime
        // Your Jar must contain a Main-Class entry in its META-INF/MANIFEST.MF metadata file
        manifest {
            attributes(mapOf("Main-Class" to application.mainClass.get()))
        }
    }

    // because we're using shadowJar, this task has limited value
    named("jar") {
        enabled = false
    }

    // update the `assemble` task to ensure the creation of a brand new UberJar using the shadowJar task
    named("assemble") {
        dependsOn(shadowJarTask)
    }
}

configure<AppEngineAppYamlExtension> {
    tools {
        // configure the Cloud Sdk tooling
    }

    stage {
        setAppEngineDirectory("appengine")          // where to find the app.yaml
        setArtifact("build/libs/$uberJarFileName")  // where to find the artifact to upload
    }

    deploy {
        projectId = "rmc-ktor-api-2"
        version = versionApp        // maintain meaningful application versions
        stopPreviousVersion = true  // stop the current version
        promote = true              // & make this the current version
    }
}