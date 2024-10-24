import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import net.researchgate.release.ReleaseExtension
import org.apache.tools.ant.filters.ReplaceTokens
import org.gradle.configurationcache.extensions.capitalized

plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "7.1.1"
    id("maven-publish")
    id("net.researchgate.release") version "3.0.2"
    id("org.cadixdev.licenser") version "0.6.1"

}

allprojects {
    project.extra.set("artifactId", (if (rootProject == project) project.name else "${rootProject.name}-${project.name}").toLowerCase())
}

subprojects {
    apply(plugin = "java")
    apply(plugin = "maven-publish")
    apply(plugin = "com.github.johnrengelman.shadow")
    apply(plugin = "org.cadixdev.licenser")

    group = "me.anutley"

    tasks {
        withType<Jar> {
            archiveFileName.set("DisLink-${archiveBaseName.get().capitalized()}-${archiveVersion.get()}-unshaded.jar")
        }

        withType<ShadowJar> {
            mustRunAfter("jar")

            archiveFileName.set("DisLink-${archiveBaseName.get().capitalized()}-${archiveVersion.get()}.jar")

            val copyJar = register<Copy>("copyJar") {
                from(archiveFile)
                destinationDir = rootProject.layout.projectDirectory.dir("./jars/").asFile
            }

            if (archiveBaseName.get() != "common") build.get().dependsOn(copyJar) // Ignore moving common jar to /jars/

            build.get().dependsOn(shadowJar)
        }

        clean {// Also delete /jars/ dir during clean
            delete += listOf(
                    File("${rootDir}/jars/")
            )
        }

        processResources {
            outputs.upToDateWhen { false }
            filter<ReplaceTokens>(mapOf(
                    "tokens" to mapOf("version" to project.version.toString()),
                    "beginToken" to "\${",
                    "endToken" to "}"
            ))
        }
    }

    repositories {
        mavenCentral()
    }

    publishing {
        publications {
            create<MavenPublication>("maven") {
                from(components["java"])
                artifactId = project.extra.get("artifactId").toString()
            }
        }

        repositories {
            maven {
                val repo = "https://repo.anutley.me/"
                val type = if (version.toString().endsWith("SNAPSHOT")) "snapshots" else "releases"
                url = uri(repo + type)

                credentials {
                    username = System.getenv("MAVEN_USERNAME")
                    password = System.getenv("MAVEN_PASSWORD")
                }
            }
        }
    }

    license {
        include("**/*.java")
        header(rootProject.file("LICENSE"))
    }

}

release {
    configure<ReleaseExtension> {
        preTagCommitMessage.set("[Release] - release commit: ")
        tagCommitMessage.set("[Release] - creating tag: ")
        newVersionCommitMessage.set("[Release] - new version commit: ")
        ignoredSnapshotDependencies.add("io.papermc.paper:paper-api")

        git {
            requireBranch.set("v2")
        }
    }
}