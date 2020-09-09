import com.github.spotbugs.SpotBugsTask
import kotlin.text.Charsets.UTF_8

// Both 'group' and 'version' are default Gradle properties so they need to be set here
group = "nl.vodafoneziggo.ccam"
version = "0.1.0-SNAPSHOT"

// Other, non-default Gradle, properties need to be defined here
val artifactName = "sim"
val artifactDescription = "C-CAM SIM service"
var vendor = "ilionx"
val user = null
val pwd = null
val springBootVersion = "2.2.5.RELEASE"

buildscript {
    // Do not add new repositories here. The artifactory repository here acts as a proxy/cache. Add missing repos to the artifactory
    // instance in case you are missing something. Note that this line is replaced by the ansible/jenkins scripts to make sure the code
    // runs on these environments.
    repositories {
        maven {
            url = uri("http://127.0.0.1:8088/artifactory/repo/")
        }
    }
    dependencies {
        classpath(group = "org.springframework.boot", name = "spring-boot-gradle-plugin", version = "2.2.5.RELEASE")
        classpath(group = "com.commercehub.gradle.plugin", name = "gradle-avro-plugin", version = "0.21.0")
    }
}

plugins {
    java
    // The project-report plugin provides file reports on dependencies, tasks, etc.
    // See https://docs.gradle.org/current/userguide/project_report_plugin.html.
    `project-report`
    // the spring boot plugin reacts to other plugins such as the java plugin.
    // In case dependency management is also available, the Spring Bill Of Materials will be injected containing versions of spring libraries.
    // see https://docs.spring.io/spring-boot/docs/current/gradle-plugin/reference/html/
    id("org.springframework.boot") version "2.2.4.RELEASE"

    // Quality plugins. These are embedded plugins of gradle and their version come with the gradle version.
    checkstyle
    pmd

    // embedded java code coverage by testing, formerly known as EclEmma.
    jacoco

    // adds intelliJ tasks to the build file and creates the settings in intellij correctly
    idea

    // our tests are using groovy with the spock framework, this adds the compileTestGroovy task and the watch to the test/groovy folder
    groovy

    // this plugin adds the integration test task to our build file with the default 'itest' directory.
    // see https://github.com/Softeq/itest-gradle-plugin
    id("com.softeq.gradle.itest") version "1.0.4"

    // non standard plugins quality plugins for gradle
    id("com.github.spotbugs") version "3.0.0"

    // adds the asciidoctor commands to our build which enables us to generate output formats for our documentation.
    id("org.asciidoctor.jvm.convert") version "3.1.0"

    // Code generation from Apache Avro files.
    //id("com.commercehub.gradle.plugin:gradle-avro-plugin") version "0.21.0"
}

// The Spring Boot plugin’s dependency on the dependency management plugin means that you can use the dependency management plugin
// without having to declare a dependency on it. This also means that you will automatically use the same version of the
// dependency management plugin as Spring Boot uses.
// NOTE:   Each Spring Boot release is designed and tested against a specific set of third-party dependencies. Overriding versions may cause
//         compatibility issues and should be done with care.
apply(plugin = "io.spring.dependency-management")
apply(plugin = "com.commercehub.gradle.plugin.avro")


springBoot {
    buildInfo {
        properties {
            artifact = artifactName
            version = project.version.toString()
            group = project.group.toString()
            name = artifactDescription
        }
    }
}

repositories {
    mavenLocal()
    maven {
        url = uri("http://127.0.0.1:8088/artifactory/repo/")
    }
}

// make sure you discuss with a peer before adding a dependency here.
dependencies {

    val vfzCommonsVersion = "0.11.0"
    val hawaiiFrameworkVersion = "3.0.0.M15"
    val mapStructVersion = "1.3.1.Final"
    val spockFrameWorkVersion = "1.3-groovy-2.5"

    // to indicate this is a web/rest application
    implementation(group = "org.springframework.boot", name = "spring-boot-starter-web")

    // Spring Boot provides a spring-boot-starter-security starter that aggregates Spring Security related dependencies together.
    implementation(group = "org.springframework.boot", name = "spring-boot-starter-security")
    // Spring Boot provides a spring-boot-starter-actuator starter that creates a (among others) /health endpoint which k8s uses for monitoring.
    implementation(group = "org.springframework.boot", name = "spring-boot-starter-actuator")
    // webflux provides the reactive web client
    implementation(group = "org.springframework.boot", name = "spring-boot-starter-webflux")
    // Spring 2.* uses Micrometer for Metrics by default. We are going to use the prometheus implementation so we can gather information used for K8s monitoring.
    implementation(group = "io.micrometer", name = "micrometer-registry-prometheus")

    // Library for String manipulations
    implementation(group = "org.apache.commons", name = "commons-lang3", version = "3.9")

    // to indicate this is an oauth2 resource server
    implementation(group = "org.springframework.security", name = "spring-security-oauth2-resource-server")

    // to be able to use JWT tokens
    implementation(group = "org.springframework.security", name = "spring-security-oauth2-jose")

    // Dependency to obtain the flexible attributes converter.
    implementation(group = "nl.vodafoneziggo", name = "vfz-common-infra-localdb", version = vfzCommonsVersion)

    // contains the handling of sql source files
    implementation(group = "org.hawaiiframework", name = "hawaii-core", version = hawaiiFrameworkVersion)

    // contains the auto configuration of the exception mapping to http error codes.
    implementation(group = "org.hawaiiframework", name = "hawaii-starter-rest", version = hawaiiFrameworkVersion)

    // contains the auto configuration for Hawaii logging.
    implementation(group = "org.hawaiiframework", name = "hawaii-starter-logging", version = hawaiiFrameworkVersion)

    // contains the BaseJdbcRepository code.
    implementation(group = "org.hawaiiframework", name = "hawaii-framework-incubator", version = "1.0.0.A3")

    // Dependency to obtain the check on the authorization (temporary part of this service until moved to the gateway) .
    implementation(group = "nl.vodafoneziggo", name = "vfz-common-authorization-boot-starter", version = vfzCommonsVersion)

    // datasource proxy for logging
    implementation(group = "net.ttddyy", name = "datasource-proxy", version = "1.4.2")

    // provides an async framework which enables us to control query timeouts etc.
    implementation("org.hawaiiframework", name = "hawaii-async", version = hawaiiFrameworkVersion)

    // jdbc template is used to connect to the database.
    implementation(group = "org.springframework", name = "spring-jdbc")
    // the oracle jdbc driver.
    implementation(group = "com.oracle.jdbc", name = "ojdbc10", version = "19.3")
    // apache commons pool 2 and dbcp 2 handle the pooled connections.
    implementation(group = "org.apache.commons", name = "commons-pool2", version = "2.8.0")
    implementation(group = "org.apache.commons", name = "commons-dbcp2", version = "2.7.0")

    // json path to parse the simtypes.json values.
    implementation(group = "com.jayway.jsonpath", name = "json-path", version = "2.4.0")

    // mapstruct is used to generate code to map from domain model classes to rest application model classes
    implementation(group = "org.mapstruct", name = "mapstruct-jdk8", version = mapStructVersion)
    annotationProcessor(group = "org.mapstruct", name = "mapstruct-processor", version = mapStructVersion)

    // Logstash encoder for logging in JSON format
    implementation(group = "net.logstash.logback", name = "logstash-logback-encoder", version = "6.3")

    // Overridden to get rid of the vulnerability in version 7.8
    implementation(group = "com.nimbusds", name = "nimbus-jose-jwt", version = "8.10")

    // Support for encrypted configuration properties
    implementation("com.github.ulisesbocchio", name = "jasypt-spring-boot-starter", version = "3.0.2")

    // Avro to format messages that are sent over Kafka.
    implementation("org.apache.avro:avro:1.10.0")

    testImplementation(group = "org.springframework.boot", name = "spring-boot-starter-test") {
        exclude(group = "com.vaadin.external.google")
    }

    // due to the dependency to spock, we also need groovy
    testImplementation(group = "org.codehaus.groovy", name = "groovy-all", version = "2.5.9")
    // www.spockframework.org is the groovy based test framework providing the specifications for our tests.
    testImplementation(group = "org.spockframework", name = "spock-core", version = spockFrameWorkVersion)
    testImplementation(group = "org.spockframework", name = "spock-spring", version = spockFrameWorkVersion)
    // spring security test support
    testImplementation(group = "org.springframework.security", name = "spring-security-test")
    // supports to mock a web server, similar to wiremock, recommended by the spring team
    testImplementation(group= "com.squareup.okhttp3", name = "mockwebserver")

    // Spring Boot test support, among others the @SpringBootTest annotation
    itestImplementation("org.springframework.boot:spring-boot-starter-test")
    // Spock framework is also required for itest, which apparently does not inherit from test
    itestImplementation(group = "org.spockframework", name = "spock-core", version = spockFrameWorkVersion)
    // Spock Spring bindings
    // See https://docs.spring.io/spring-boot/docs/current/reference/html/spring-boot-features.html#boot-features-testing-spring-boot-applications-with-spock
    itestImplementation(group = "org.spockframework", name = "spock-spring", version = spockFrameWorkVersion)
    // This delivers the @AutoconfigureWiremock annotation, wiremock itself is pulled in transitively
    itestImplementation(group = "org.springframework.cloud", name = "spring-cloud-contract-wiremock", version = "2.2.1.RELEASE")
    //
    itestImplementation(group = "nl.vodafoneziggo", name = "vfz-common-test-support", version = vfzCommonsVersion)
}

/**
 * Make the itest source set contain the main and test classpath, so we can use test (helper) code in itest as well.
 */
itestSourceSet {
    compileClasspath = sourceSets["main"].compileClasspath +
            sourceSets["main"].output +
            sourceSets["test"].compileClasspath +
            sourceSets["test"].output
    runtimeClasspath = sourceSets["main"].runtimeClasspath +
            sourceSets["main"].output +
            sourceSets["test"].runtimeClasspath +
            sourceSets["test"].output
}

/**
 * Java 11 is long term supported and therefore chosen as the default.
 */
java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

/**
 * Override default java compiler settings
 */
tasks.withType<JavaCompile> {
    // override default false
    options.isDeprecation = true
    // defaults to use the platform encoding
    options.encoding = UTF_8.name()
    // add Xlint to our compiler options (but disable processing because of Spring warnings in code)
    // and make warnings be treated like errors
    options.compilerArgs.addAll(arrayOf("-Xlint:all", "-Xlint:-processing", "-Werror", "-Amapstruct.defaultComponentModel=spring"))
}

/**
 * Create manifest settings.
 */
tasks.getByName<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
    manifest {
        attributes("Specification-Title" to artifactDescription)
        attributes("Specification-Vendor" to vendor)

        attributes("Name" to artifactDescription)
        attributes("Implementation-Title" to artifactDescription)
        attributes("Implementation-Version" to project.version.toString())
        attributes("Implementation-Vendor" to vendor)
    }
}

/**
 * Suppress checkstyle for generated code.
 *
 * This is done by configuring a SuppressionFilter in the checkstyle config. Unfortunately, this doesn't work
 * with relative paths. As a workaround, this section creates a property 'suppressionFile', which holds the absolute
 * path to the suppression file. The checkstyle configuration file then references this property.
 *
 * See https://stackoverflow.com/questions/18013322/checkstyle-exclude-folder.
 */
checkstyle {
    // use one common config file for all subprojects
    configFile = project(":").file("config/checkstyle/checkstyle.xml")
    configProperties.put("suppressionFile", project(":").file("config/checkstyle/suppressions.xml"))
}

/**
 * Configuration of PMD.
 */
pmd {
    // as a development team we want pmd failures to break the build and keep the code clean.
    isIgnoreFailures = false
    // directly show the failures in the output
    isConsoleOutput = true
    // the configuration of the custom rules
    ruleSetConfig = resources.text.fromFile(projectDir.path + "/config/pmd/pmd.xml")
    // clear the default list of rules, otherwise this will override our custom configuration.
    ruleSets = listOf<String>()
}

/**
 * Configuration of spotbugs with our exclusion configuration.
 */
spotbugs {
    // the exclude filter
    excludeFilterConfig = resources.text.fromFile(projectDir.path + "/config/spotbugs/exclude.xml")
}

/**
 * Configuration of the Spot bugs task.
 * The default is xml enabled, html is preferred so we can use a browser to view the report.
 */
tasks.withType<SpotBugsTask> {
    val format = findProperty("spotbugsReportFormat")
    val xmlFormat = (format == "xml")
    reports {
        html.isEnabled = !xmlFormat
        xml.isEnabled = xmlFormat
    }
}

/**
 * Configures the attributes that are now known to asciidoc by default.
 */
tasks.asciidoctor {
    baseDirFollowsSourceDir()
    options(mapOf("doctype" to "book", "ruby" to "erubis"))
    attributes(
            mapOf(
                    "source-highlighter" to "coderay",
                    "toc" to "left",
                    "errorsdir" to "reference/errors",
                    "umldir" to  "uml",
                    "numbered" to "true"
            )
    )
    asciidoctorj {
        modules.pdf.use()
        modules.diagram.use()
    }

}

tasks.jacocoTestReport {
    reports {
        xml.isEnabled = true
    }
}
