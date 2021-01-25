plugins {
    `java-library`
}

val baseVersion = "0.1"
val elasticsearchVersion = "7.10.0"

group = "fi.evident.elasticsearch"
version = "$baseVersion-es$elasticsearchVersion"

repositories {
    mavenCentral()
}

dependencies {
    compileOnly("org.elasticsearch:elasticsearch:$elasticsearchVersion")
    implementation("fi.evident.raudikko:raudikko:0.1.1")

    testImplementation("org.elasticsearch:elasticsearch:$elasticsearchVersion")
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
    archiveBaseName.set("elasticsearch-analysis-raudikko")
    from(file("src/main/plugin-metadata")) {
        expand(
            "pluginVersion" to project.version,
            "javaVersion" to System.getProperty("java.specification.version"),
            "elasticsearchVersion" to elasticsearchVersion
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
        languageVersion.set(JavaLanguageVersion.of(8))
        vendor.set(JvmVendorSpec.ADOPTOPENJDK)
    }
}
