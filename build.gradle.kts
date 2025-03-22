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
    
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.0")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher:1.10.0")
    
    implementation("com.squareup.okhttp3:okhttp:4.12.0")

    implementation("com.fasterxml.jackson.core:jackson-core:2.15.2")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.15.2")
    implementation("com.fasterxml.jackson.core:jackson-annotations:2.15.2")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.15.2")
}

application {
    mainClass.set("org.bee.Main")
}

val databaseDir = layout.projectDirectory.dir("database")

tasks.test {
    useJUnitPlatform()
    
    // Copy database files before running tests
    doFirst {
        // Create and copy database files using modern API
        val testDbDir = layout.buildDirectory.dir("database")
        copy {
            from(databaseDir)
            into(testDbDir)
        }
        
        // Set system property for database location
        systemProperty("database.dir", testDbDir.get().asFile.absolutePath)
    }
    
    // Configure test execution
    testLogging {
        events("passed", "skipped", "failed")
        showStandardStreams = true  // Show stdout/stderr from tests
    }

    reports {
        html.required.set(true)
        junitXml.required.set(true)
    }
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = "org.bee.Main"
    }
    
    archiveBaseName.set(project.name)
    
    from(configurations.runtimeClasspath.get().map {
        if (it.isDirectory) it else project.zipTree(it) 
    })
    
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

tasks.register<JavaExec>("runJar") {
    dependsOn(tasks.jar)
    
    doFirst {
        // Copy database folder to execution directory
        val libsDir = layout.buildDirectory.dir("libs")
        copy {
            from(databaseDir)
            into(libsDir.get().dir("database"))
        }
    }
    
    classpath = files(tasks.jar.get().outputs.files)
    mainClass.set("org.bee.Main")
    standardInput = System.`in`
    workingDir = layout.buildDirectory.dir("libs").get().asFile
}
