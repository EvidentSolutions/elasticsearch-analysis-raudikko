plugins {
    `java-library`
}

val baseVersion = System.getProperty("baseVersion") ?: "0.2"
val elasticsearchVersion = System.getProperty("elasticsearchVersion") ?: "9.3.0"
val luceneVersion = System.getProperty("luceneVersion") ?: "10.1.0"

group = "fi.evident.elasticsearch"
version = "$baseVersion-es$elasticsearchVersion"

repositories {
    mavenCentral()
}

dependencies {
    compileOnly("org.elasticsearch.plugin:elasticsearch-plugin-api:$elasticsearchVersion")
    compileOnly("org.elasticsearch.plugin:elasticsearch-plugin-analysis-api:$elasticsearchVersion")
    compileOnly("org.apache.lucene:lucene-analysis-common:$luceneVersion")
    implementation("fi.evident.raudikko:raudikko:0.1.4")

    testImplementation("org.elasticsearch.plugin:elasticsearch-plugin-api:$elasticsearchVersion")
    testImplementation("org.elasticsearch.plugin:elasticsearch-plugin-analysis-api:$elasticsearchVersion")
    testImplementation("org.apache.lucene:lucene-analysis-common:$luceneVersion")
    testImplementation(platform("org.junit:junit-bom:5.11.0"))
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
            "javaVersion" to java.toolchain.languageVersion.get().asInt(),
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
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}
