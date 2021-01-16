The [JmePower Project][jmepower] is about promoting
the [jMonkeyEngine game engine][jme].

It contains 2 sub-projects:

 1. LemurPower: a startup library for applications that use Lemur
 2. JmePowerAssets: generate assets included in LemurPower

Complete source code (in Java) is provided.

## Licensing

The source code has [a BSD 3-Clause license][license].

[The Jamie model][jaime] has a BSD 3-Clause license.


## How to build JmePower from source

 1. Install a [Java Development Kit (JDK)][openJDK],
    if you don't already have one.
 2. Download and extract the source code from GitHub:
   + using Git:
     + `git clone https://github.com/stephengold/JmePower.git
     + `cd JmePower`
     + `git checkout -b latest 0.1.0`
   + using a web browser:
     + browse to [https://github.com/stephengold/JmePower/releases/latest][latest]
     + follow the "Source code (zip)" link
     + save the ZIP file
     + unzip the saved ZIP file
     + `cd` to the extracted directory/folder
 3. Set the `JAVA_HOME` environment variable:
   + using Bash:  `export JAVA_HOME="` *path to your JDK* `"`
   + using Windows Command Prompt:  `set JAVA_HOME="` *path to your JDK* `"`
 4. Run the Gradle wrapper:
   + using Bash:  `./gradlew build`
   + using Windows Command Prompt:  `.\gradlew build`

After a successful build,
Maven artifacts will be found in `LemurPower/build/libs`.

You can install the Maven artifacts to your local cache:
 + using Bash:  `./gradlew install`
 + using Windows Command Prompt:  `.\gradlew install`


[jaime]: https://github.com/stephengold/JmePower/tree/master/LemurPower/src/main/resources/Models/Jaime "Jaime model"
[jme]: https://jmonkeyengine.org  "JMonkeyEngine Project"
[jmepower]: https://github.com/stephengold/JmePower "JmePower Project"
[latest]: https://github.com/stephengold/JmePower/releases/latest "latest release"
[license]: https://github.com/stephengold/JmePower/blob/master/license.txt "JmePower license"
[openJDK]: https://openjdk.java.net "OpenJDK Project"
