<h2 align="center"><b>Var-HTTP</b></h2>

<h4 align="center">This project is named after the Norse god Vár, being the god of oaths and agreements (see <a href="https://en.wikipedia.org/wiki/V%C3%A1r" alt="Wikipedia page of Vár">here</a>).</h4>

<p align="center">
<a href="https://github.com/persequor-com/var-http/tags" alt="GitHub release"><img src="https://img.shields.io/github/v/tag/persequor-com/var-http?label=version"></a>
<a href="https://spdx.org/licenses/MIT.html" alt="License: MIT"><img src="https://img.shields.io/github/license/persequor-com/var-http"></a>
<a href="https://github.com/persequor-com/var-http/actions" alt="Build Status"><img src="https://img.shields.io/github/workflow/status/persequor-com/var-http/Java%20CI%20with%20Gradle"></a>
</p>
<hr>
<p align="center">Maintained by the <a href="https://psqr.eu/">PSQR team</a>.</p>

## Description
Var-HTTP is a lightweight annotation-based controller framework for Java, based on HTTP servlets.
It also contains a simple servlet server based on the openjdk HTTP server, but is compatible with other servlet containers as well. 

## Related Projects
Var is part of the OSS Suite developed and maintained by the PSQR Team. Find below other projects of the suite :
- [ODINJector](https://github.com/persequor-com/ODINjector), a Java injection library
- [Valqueries](https://github.com/persequor-com/valqueries-sql), a Java ORM for relational databases
- [Valqueries CQL](https://github.com/persequor-com/valqueries-cql), a Java ORM for Cassandra
- [RAN](https://github.com/persequor-com/ran), a Java library for automatically mapping objects to key/value structures

## Getting Started
The simplest way to get started is to declare a dependency on Var-HTTP in your own Java project, using your preferred dependency management tool.
Var artifacts are currently hosted on GitHub Packages.
Read more on how to work with GitHub packages [here](https://docs.github.com/en/packages/working-with-a-github-packages-registry).

***Please note that GitHub Packages requires authentication***

For Gradle : 
```groovy
dependencies {
    implementation 'io.var-http:core:0.1.0-86'
}
```

## Build from source
Var-HTTP uses [Gradle]((https://docs.gradle.org/6.7/userguide/userguide.html)) as build tool.
Its subproject "var-test" depends on another open source project called ODINjector, available [here](https://github.com/persequor-com/ODINjector).
ODIN is currently distributed using GitHub Packages, and thus requires authentication from apps to be able to access the package. 
Therefore, you will have to grant gradle the right to authenticate to GitHub using your account to be able to build the project fully.

**Note** If you do not want to add a personal access token to GitHub, you can also disable the subproject var-test.
Simply comment the line mentioning "var-test" in the file `settings.gradle`.

#### Adding a GitHub personal access token for Gradle 
Start by logging into your GitHub account, go to Settings > Developer Settings -> Personal access tokens. 
Here, click on "generate new token". Gradle only needs to access GitHub packages, so you only have to toggle "read packages".
Copy the generated token.
Go to your gradle home folder (on linux, it's located in `~/.gradle/`) and edit the file `gradle.properties`.
Add these two lines :
```properties
gpr.user=<your GitHub username>
gpr.key=<the token issued by github>
```

#### Build
To build it locally, start by cloning the repo.
Once done, simply run `./gradlew build` to build the project.

## Contributing
For contribution guidelines, see [CONTRIBUTING](./CONTRIBUTING.md).

## License
Var-HTTP is licensed under the terms of the [MIT](./license.txt) License.