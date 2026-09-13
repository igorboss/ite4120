// ITE4120 course template — animals register.
//
// A single, standalone Gradle build. Every Helex dependency resolves as a PUBLISHED
// Maven artifact from GitHub Packages — there is no monorepo checkout beside this
// project and none is ever needed.
//
// Credentials: a GitHub token with `read:packages`, either as environment variables
// (GITHUB_ACTOR + GITHUB_TOKEN) or as Gradle properties (gpr.user + gpr.key in
// ~/.gradle/gradle.properties). See README.md § Prerequisites.
rootProject.name = "animals-register"

dependencyResolutionManagement {
    repositories {
        mavenLocal()
        mavenCentral()

        // Published Helex libraries. The blocks are added only when credentials exist,
        // so a missing token fails with "could not resolve org.helex.emr:..." rather
        // than a confusing 401 — and mavenLocal can satisfy the build on machines
        // where the jars were published locally.
        val gprUser = System.getenv("GITHUB_ACTOR") ?: providers.gradleProperty("gpr.user").orNull
        val gprKey = System.getenv("GITHUB_TOKEN") ?: providers.gradleProperty("gpr.key").orNull
        if (gprUser != null && gprKey != null) {
            maven {
                name = "HelexEmrPackages"
                url = uri("https://maven.pkg.github.com/helex-solutions/emr-repo")
                credentials { username = gprUser; password = gprKey }
            }
            maven {
                name = "HelexForgePackages"
                url = uri("https://maven.pkg.github.com/helex-solutions/forge")
                credentials { username = gprUser; password = gprKey }
            }
        }
    }
}
