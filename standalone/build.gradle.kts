plugins {
    id("application")
}

application {
    mainClass.set("me.anutley.dislink.standalone.DisLinkStandaloneLoader")
}

dependencies {
    implementation(project(":common"))
    implementation("ch.qos.logback:logback-classic:1.4.6")
}