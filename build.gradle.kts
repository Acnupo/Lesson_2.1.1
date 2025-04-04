plugins {
    kotlin("jvm") version "1.9.0" // или актуальная версия Kotlin
    jacoco
}

group = "com.yourcompany" // Замените на ваш package
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib")) // Стандартная библиотека Kotlin

    // Тестирование
    testImplementation("junit:junit:4.13.2") // JUnit 4
    testImplementation(kotlin("test-junit")) // Kotlin тесты с JUnit поддержкой
}

tasks {
    test {
        useJUnit() // Используем JUnit 4
        finalizedBy(jacocoTestReport) // Генерируем отчет после тестов
    }

    jacocoTestReport {
        dependsOn(test) // Требуем выполнения тестов перед генерацией отчета
        reports {
            xml.required.set(true) // Для CI систем
            html.required.set(true) // Для локального просмотра
            csv.required.set(false)
        }
    }

    jacocoTestCoverageVerification {
        violationRules {
            rule {
                limit {
                    minimum = "0.8".toBigDecimal() // Минимальное 80% покрытие
                }
            }
        }
    }
}

jacoco {
    toolVersion = "0.8.11" // Актуальная версия JaCoCo
}

kotlin {
    jvmToolchain(11) // Версия Java (8, 11, 17 и т.д.)
}