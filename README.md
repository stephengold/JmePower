# JmePower Project

The [JmePower Project][jmepower] is about promoting
[the jMonkeyEngine (JME) game engine][jme].

It contains 3 subprojects:

1. JmePowerLibrary: the JmePower startup library for jMonkeyEngine applications
2. JmePowerAssets: generate assets included in the library
3. JmePowerExamples: sample applications using the library

Complete source code (in [Java]) is provided under
[a 3-clause BSD license][license].

## Licensing

The source code has [a BSD 3-Clause license][license].

[The Jamie model][jaime] has a BSD 3-Clause license.


## How to build JmePower from source

1. Install a [Java Development Kit (JDK)][adoptium],
   if you don't already have one.
2. Point the `JAVA_HOME` environment variable to your JDK installation:
   (In other words, set it to the path of a directory/folder
   containing a "bin" that contains a Java executable.
   That path might look something like
   "C:\Program Files\Eclipse Adoptium\jdk-17.0.3.7-hotspot"
   or "/usr/lib/jvm/java-17-openjdk-amd64/" or
   "/Library/Java/JavaVirtualMachines/zulu-17.jdk/Contents/Home" .)
  + using Bash or Zsh: `export JAVA_HOME="` *path to installation* `"`
  + using [Fish]: `set -g JAVA_HOME "` *path to installation* `"`
  + using Windows Command Prompt: `set JAVA_HOME="` *path to installation* `"`
  + using PowerShell: `$env:JAVA_HOME = '` *path to installation* `'`
3. Download and extract the JmePower source code from GitHub:
  + using [Git]:
    + `git clone https://github.com/stephengold/JmePower.git`
    + `cd JmePower`
    + `git checkout -b latest 1.1.1`
  + using a web browser:
    + browse to [the latest release][latest]
    + follow the "Source code (zip)" link
    + save the ZIP file
    + extract the contents of the saved ZIP file
    + `cd` to the extracted directory/folder
4. Run the [Gradle] wrapper:
  + using Bash or Fish or PowerShell or Zsh: `./gradlew build`
  + using Windows Command Prompt: `.\gradlew build`

After a successful build,
Maven artifacts will be found in `JmePowerLibrary/build/libs`.

You can install the artifacts to your local Maven repository:
+ using Bash or Fish or PowerShell or Zsh: `./gradlew install`
+ using Windows Command Prompt: `.\gradlew install`

You can restore the project to a pristine state:
+ using Bash or Fish or PowerShell or Zsh: `./gradlew clean`
+ using Windows Command Prompt: `.\gradlew clean`

## How to add JmePower to an existing project

Adding JmePower to an existing [jMonkeyEngine][jme] project should be
a simple 3-step process:

1. Add the appropriate libraries to the classpaths.
2. Add code to instantiate and attach an `AppState`.
3. Add code to await completion.

### Add to the classpaths

The JmePower library depends on the standard jme3-core library and
[the Heart library][heart].

For projects built using Maven or Gradle, the build tools should automatically
resolve the compile-time dependencies.

#### Gradle-built projects

Add to the project’s "build.gradle" or "build.gradle.kts" file:

    repositories {
        mavenCentral()
    }
    dependencies {
        implementation("com.github.stephengold:JmePower:1.1.1")
    }

### Instantiate and attach an AppState

Instantiate a `JmeLoadingState`.

The `AppState` constructor takes an array of objects
to be preloaded into the application's asset cache.
If there are none, the array can be empty.

Depending on the application's structure, the instance might be
attached explicitly in `simpleInitApp()`:

    JmeLoadingState loading = new JmeLoadingState(preloadArray);
    stateManager.attach(loading);

or it might be passed to the application's constructor:

    private MyApplication() {
        super(
                // other appstates, if desired ...
                new JmeLoadingState(preloadArray)
        );
    }

### Await completion

The appstate takes indicates completion by disabling itself.
A `SimpleApplication` might check for completion in `simpleUpdate()`:

    @Override
    public void simpleUpdate(float tpf) {
        AppState loading = stateManager.getState(JmeLoadingState.class);
        if (loading != null && !loading.isEnabled()) {
            getStateManager().detach(loading);
            // additional startup, if desired ...
        }
    }


[adoptium]: https://adoptium.net/releases.html "Adoptium Project"
[fish]: https://fishshell.com/ "Fish command-line shell"
[git]: https://git-scm.com "Git"
[gradle]: https://gradle.org "Gradle Project"
[heart]: https://github.com/stephengold/Heart "Heart Project"
[jaime]: https://github.com/stephengold/JmePower/tree/master/JmePowerLibrary/src/main/resources/Models/Jaime "Jaime model"
[java]: https://en.wikipedia.org/wiki/Java_(programming_language) "Java programming language"
[jme]: https://jmonkeyengine.org "jMonkeyEngine Project"
[jmepower]: https://github.com/stephengold/JmePower "JmePower Project"
[latest]: https://github.com/stephengold/JmePower/releases/latest "latest release"
[license]: https://github.com/stephengold/JmePower/blob/master/license.txt "JmePower license"
[openJDK]: https://openjdk.java.net "OpenJDK Project"
