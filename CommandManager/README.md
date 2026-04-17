# Logger

## How to add to your project

## Fabric versioning

| Minecraft version | Fabric version | command-manager-fabric version |
|-------------------|----------------|--------------------------------|
| 1.20.1            | 0.15.10        | [1.0.0 - 1.0.6]                |
| 1.21.1            | 0.16.9         | [1.1.0 - ]                     |

```kotlin
repositories {
    maven("https://repo.raduvoinea.com/repository/maven-releases/")
    maven("https://repo.raduvoinea.com/") // The short version of the above (might be slower on high latency connections)
}

dependencies {
    implementation("com.raduvoinea:command-manager-common:VERSION")
    implementation("com.raduvoinea:command-manager-velocity:VERSION")
    implementation("com.raduvoinea:command-manager-fabric-1-19:VERSION")
}
```