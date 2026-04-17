# Utils

This is a collection of utilities that I use in my projects. This collection is compiled from the following projects:

- [github.com/Radu-Voinea/RedisManager](https://github.com/Radu-Voinea/RedisManager)
- [github.com/Radu-Voinea/MessageBuilder](https://github.com/Radu-Voinea/MessageBuilder)
- [github.com/Radu-Voinea/Logger](https://github.com/Radu-Voinea/Logger)
- [github.com/Radu-Voinea/FileManager](https://github.com/Radu-Voinea/FileManager)
- [github.com/Radu-Voinea/Lambda](https://github.com/Radu-Voinea/Lambda)

## How to add to your project

```kotlin
repositories {
    maven("https://repo.raduvoinea.com/repository/maven-releases/")
    maven("https://repo.raduvoinea.com/") // The short version of the above (might be slower on high latency connections)
}

dependencies {
    implementation("com.raduvoinea:utils:VERSION")

    // To use the redis_manager
    implementation("redis.clients:jedis:<JEDIS_VERSION>")
}
```