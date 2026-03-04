plugins {
    id("java-library")
    id("maven-publish")
    jacoco
    alias(libs.plugins.jmh)
    alias(libs.plugins.jreleaser)
    alias(libs.plugins.sonar)
}

val title: String by project
val gitName: String by project

group = "org.pageseeder.diffx"
version = file("version.txt").readText().trim()
description = "A Java library for comparing and identifying differences between XML and text documents."

repositories {
    mavenCentral()
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(11))
    }
    withJavadocJar()
    withSourcesJar()
}

jmh {
    jmhVersion.set("1.37")
    fork.set(2)
    warmupIterations.set(5)
    iterations.set(10)
    profilers.add("gc")
}

dependencies {
    compileOnly(libs.jspecify)

    implementation(libs.pso.xmlwriter)

    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.bundles.junit.testing)
    testRuntimeOnly(libs.junit.jupiter.engine)
}

sonar {
    properties {
        property("sonar.projectKey", "pageseeder_berlioz-plus")
        property("sonar.organization", "pageseeder")
        // Tell SonarCloud where the JaCoCo XML report is
        property(
            "sonar.coverage.jacoco.xmlReportPaths",
            layout.buildDirectory.file("reports/jacoco/test/jacocoTestReport.xml").get().asFile.absolutePath
        )
    }
}

tasks.test {
    useJUnitPlatform()
    // make sure report generation happens after tests when requested
    finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        xml.required.set(true)   // Sonar reads this
        html.required.set(true)  // nice to have for CI artifacts/debugging
        csv.required.set(false)
    }
}

tasks.jar {
    manifest {
        attributes(
            "Main-Class" to "org.pageseeder.diffx.Main",
            "Implementation-Title" to project.name,
            "Implementation-Version" to project.version,
            "Implementation-Vendor" to "Allette Systems",
            "Specification-Title" to project.description,
            "Specification-Version" to project.version,
            "Specification-Vendor" to "Allette Systems"
        )
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
            groupId = group.toString()
            pom {
                name.set(title)
                description.set(project.description)
                url.set("https://github.com/pageseeder/${gitName}")
                licenses {
                    license {
                        name.set("The Apache Software License, Version 2.0")
                        url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }
                organization {
                    name.set("Allette Systems")
                    url.set("https://www.allette.com.au")
                }
                scm {
                    url.set("git@github.com:pageseeder/${gitName}.git")
                    connection.set("scm:git:git@github.com:pageseeder/${gitName}.git")
                    developerConnection.set("scm:git:git@github.com:pageseeder/${gitName}.git")
                }
                developers {
                    developer {
                        name.set("Christophe Lauret")
                        email.set("clauret@weborganic.com")
                    }
                    developer {
                        name.set("Philip Rutherford")
                        email.set("philipr@weborganic.com")
                    }
                }
            }
        }
    }
    repositories {
        maven {
            url = layout.buildDirectory.dir("staging-deploy").get().asFile.toURI()
        }
    }
}

jreleaser {
    configFile.set(file("jreleaser.toml"))
}
