plugins {
    alias(libs.plugins.userdev)

    id("xyz.jpenilla.run-paper") version "2.1.0"

    `java-library`
}

rootProject.version = "1.0"
rootProject.group = "me.badbones69.crazyrunes"

dependencies {
    paperweight.paperDevBundle("1.20.1-R0.1-SNAPSHOT")
}

tasks {
    reobfJar {
        outputJar.set(file("$buildDir/libs/${project.name}-${project.version}.jar"))
    }

    assemble {
        dependsOn(reobfJar)
    }

    runServer {
        minecraftVersion("1.20.1")

        jvmArgs("-Dnet.kyori.ansi.colorLevel=truecolor")
    }
}