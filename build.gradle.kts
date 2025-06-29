plugins {
    id("java-library")
    id("maven-publish")
    alias(libs.plugins.jreleaser)
}

val title: String by project
val gitName: String by project
val website: String by project

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

dependencies {
    compileOnly(libs.jetbrains.annotations)

    implementation(libs.pso.xmlwriter)

    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.bundles.junit.testing)
    testRuntimeOnly(libs.junit.jupiter.engine)
}

tasks.test {
    useJUnitPlatform()
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
                url.set(website)
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
