Vert.x 4 simple Gradle project example
======================================

This project shows a very simple example project using Gradle, which has an HTTP server with a very simple API and backend.

To run the service, **Java 17** needs to be available on your machine.

Use the project in your IDE
---------------------------

To use the project in **Eclipse**, first run

```
./gradlew eclipse
```

to generate an Eclipse project that you can import into your workspace.

If you prefer to use **IntelliJ Idea** you can just open the project directly (as Gradle project).

You can run or debug the service in your IDE by running the main class (`BookService`).

When running the application is available at <http://localhost:8080> with a simple "Hello world" response.

The `scripts/` folder contains some example API calls w/ `curl`.

Run using Gradle
----------------

Because the application plugin is being used, you can directly run the application:

```
./gradlew run
```

Now point your browser at <http://localhost:8080>
