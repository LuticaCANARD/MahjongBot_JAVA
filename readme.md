## 제목.

### gradle

```gradle
plugins {
    id 'java'
    id 'org.jetbrains.kotlin.jvm' version '1.7.21'
}
apply plugin: 'java'
apply plugin: 'application'

repositories {
    mavenCentral()
    maven {
        name 'm2-dv8tion'
        url 'https://m2.dv8tion.net/releases'
    }
}

dependencies {
    testImplementation 'org.jetbrains.kotlin:kotlin-test'
    implementation('org.junit.jupiter:junit-jupiter-api:5.9.2')
    testRuntimeOnly 'org.junit.jupiter:junit-jupiter-engine:5.9.2'
    implementation("net.dv8tion:JDA:5.0.0-beta.3")

    implementation 'org.slf4j:slf4j-simple:2.0.5'
    implementation 'com.opencsv:opencsv:5.7.1'
    implementation group: 'com.googlecode.json-simple', name: 'json-simple', version: '1.1.1'

    implementation("com.google.api-client:google-api-client:1.31.3")
    implementation("com.google.apis:google-api-services-drive:v3-rev197-1.25.0")
    implementation("com.google.apis:google-api-services-sheets:v4-rev612-1.25.0")
    implementation("com.google.auth:google-auth-library-oauth2-http:0.25.2")
    implementation("com.google.http-client:google-http-client-jackson2:1.39.1")
    implementation("org.apache.poi:poi:5.0.0")
    implementation "org.jetbrains.kotlin:kotlin-stdlib:1.7.10"
}

compileKotlin {
    kotlinOptions.jvmTarget = '18'
}

compileTestKotlin {
    kotlinOptions.jvmTarget = '18'
}
```

### TODO

- sql 데이터베이스 적용
- 대기패 계산 알고리즘 개선
- 테스트클래스 작성
