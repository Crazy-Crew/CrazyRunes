plugins {
    alias(libs.plugins.userdev)

    id("xyz.jpenilla.run-paper") version "2.1.0"

    `java-library`
}

rootProject.version = "0.3"
rootProject.group = "me.badbones69.crazyrunes"

dependencies {
    paperweight.paperDevBundle("1.20.2-R0.1-SNAPSHOT")
}

tasks {
    reobfJar {
        outputJar.set(file("$buildDir/libs/${project.name}-${project.version}.jar"))
    }

    assemble {
        val jarsDir = File("$rootDir/jars")
        if (jarsDir.exists()) jarsDir.delete()

        dependsOn(reobfJar)

        doLast {
            if (!jarsDir.exists()) jarsDir.mkdirs()

            val file = file(reobfJar.get().outputJar.get())

            copy {
                from(file)
                into(jarsDir)
            }
        }
    }

    runServer {
        minecraftVersion("1.20.2")

        jvmArgs("-Dnet.kyori.ansi.colorLevel=truecolor")
    }
}