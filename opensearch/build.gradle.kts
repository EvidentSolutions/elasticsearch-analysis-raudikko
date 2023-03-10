plugins {
    `java-library`
}

val baseVersion = System.getProperty("baseVersion") ?: "0.1"
val opensearchVersion = System.getProperty("opensearchVersion") ?: "2.5.0"

group = "fi.evident.opensearch"
version = "$baseVersion-os$opensearchVersion"

repositories {
    mavenCentral()
}

dependencies {
    compileOnly("org.opensearch:opensearch:$opensearchVersion")
    implementation("fi.evident.raudikko:raudikko:0.1.2")

    testImplementation("org.opensearch:opensearch:$opensearchVersion")
    testImplementation(platform("org.junit:junit-bom:5.7.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.test {
    useJUnitPlatform()
}

val distributionZip = tasks.register<Zip>("distributionZip") {
    archiveBaseName.set("opensearch-analysis-raudikko")
    from(file("src/main/plugin-metadata")) {
        expand(
            "pluginVersion" to project.version,
            "javaVersion" to java.toolchain.languageVersion.get().asInt(),
            "opensearchVersion" to opensearchVersion
        )
    }
    from(tasks.jar)
    from(configurations.runtimeClasspath)
}

artifacts {
    add(configurations.archives.name, distributionZip)
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}
