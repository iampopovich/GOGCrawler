// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    id("org.jlleitschuh.gradle.ktlint") version "12.3.0" apply false
}