plugins {
    id("java")
    id("application")
}

group = "org.bee"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    implementation("com.google.code.gson:gson:2.12.1")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
}

application {
    mainClass.set("org.bee.Main")
}

tasks.test {
    useJUnitPlatform()
    
    // Copy database files before running tests
    doFirst {
        // Create temp directory for database files
        file("build/database").mkdirs()
        // Copy database files to temp location
        copy {
            from("database")
            into("build/database")
        }
        
        // Set system property for database location
        systemProperty("database.dir", file("build/database").absolutePath)
    }
    
    // Configure test execution
    testLogging {
        events("passed", "skipped", "failed")
        showStandardStreams = true  // Show stdout/stderr from tests
    }
    
    // Enable parallel test execution if desired
    // maxParallelForks = (Runtime.getRuntime().availableProcessors() / 2).takeIf { it > 0 } ?: 1
    
    // Report aggregation
    reports {
        html.required.set(true)
        junitXml.required.set(true)
    }
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = "org.bee.Main"
    }
    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

tasks.register<JavaExec>("runJar") {
    dependsOn(tasks.jar)
    
    doFirst {
        // Copy database folder to execution directory
        copy {
            from("database")
            into("build/libs/database")
        }
    }
    
    classpath = files(tasks.jar.get().outputs.files)
    mainClass.set("org.bee.Main")
    standardInput = System.`in`
    workingDir = file("build/libs")
}
