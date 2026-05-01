plugins {
    `java-library`
    `maven-publish`
    jacoco
    id("xyz.jpenilla.run-paper") version "3.0.2"
}

group = "com.github.lukesky19"
version = "0.3.1.0"

repositories {
    mavenCentral()
    mavenLocal()

    maven("https://repo.papermc.io/repository/maven-public/") {
        name = "papermc-repo"
    }

    maven("https://oss.sonatype.org/content/groups/public/") {
        name = "sonatype"
    }

    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/") {
        name = "PlaceholderAPI Repo"
    }
}

dependencies {
    // Paper
    compileOnly("io.papermc.paper:paper-api:26.1.2.build.+")
    testImplementation("io.papermc.paper:paper-api:1.21.11-R0.1-SNAPSHOT")

    // SkyLib
    compileOnly("com.github.lukesky19:SkyLib:2.0.0.0")
    testImplementation("com.github.lukesky19:SkyLib:2.0.0.0")

    // Integration
    compileOnly("com.github.lukesky19:NewPlayerPerks:1.4.0.0")
    testImplementation("com.github.lukesky19:NewPlayerPerks:1.4.0.0")
    compileOnly("me.clip:placeholderapi:2.11.7")
    testImplementation("me.clip:placeholderapi:2.11.7")
    compileOnly("com.github.MilkBowl:VaultAPI:1.7.1")
    testImplementation("com.github.MilkBowl:VaultAPI:1.7.1")

    // Test Dependencies
    testImplementation("org.xerial:sqlite-jdbc:3.51.1.0")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.14.1")
    testImplementation("org.junit.platform:junit-platform-launcher:1.10.0")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.14.1")
    testImplementation("org.mockito:mockito-junit-jupiter:5.21.0")
    testImplementation("org.mockbukkit.mockbukkit:mockbukkit-v1.21:4.108.0")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(25))
}

tasks {
    processResources {
        val props = mapOf("version" to version)
        inputs.properties(props)
        filteringCharset = "UTF-8"
        filesMatching("plugin.yml") {
            expand(props)
        }
    }

    // This allows usage of @apiNode in javadocs
    javadoc {
        (options as StandardJavadocDocletOptions).tags("apiNote:a:API Note:", "implNote:a:IMPL Note:")
    }

    runServer {
        minecraftVersion("1.21.11")
        jvmArgs("-Xms2G", "-Xmx2G", "-Dcom.mojang.eula.agree=true")

        downloadPlugins {
            github("lukesky19", "SkyLib", "2.0.0.0", "SkyLib-2.0.0.0.jar")
            github("lukesky19", "NewPlayerPerks", "1.4.0.0", "NewPlayerPerks-1.4.0.0.jar")
            modrinth("placeholderapi", "2.12.2")
            modrinth("rosestacker", "1.5.39")
            modrinth("essentialsx", "2.21.2")
            modrinth("viaversion", "5.9.0")
            modrinth("viabackwards", "5.9.0")
            modrinth("luckperms", "v5.5.17-bukkit")
            github("MilkBowl", "Vault", "1.7.3", "Vault.jar")
        }
    }

    test {
        useJUnitPlatform()

        finalizedBy(jacocoTestReport)
    }

    jacocoTestReport {
        dependsOn(test)

        reports {
            xml.required = false
            csv.required = false
            html.outputLocation = layout.buildDirectory.dir("jacocoHtml")
        }
    }

    jar {
        manifest {
            attributes["paperweight-mappings-namespace"] = "mojang"
        }
        archiveClassifier.set("")
    }

    build {
        dependsOn(publishToMavenLocal)
        dependsOn(javadoc)
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
}