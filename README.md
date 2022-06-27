# JmePower Project

The [JmePower Project][jmepower] is about promoting
[the jMonkeyEngine (JME) game engine][jme].

It contains 3 sub-projects:

1. JmePowerLibrary: the JmePower startup library for jMonkeyEngine applications
2. LemurPower: an extension of the JmePower library
   for applications that use Lemur
3. JmePowerAssets: generate assets included in JmePowerLibrary

Complete source code (in Java) is provided under
[a 3-clause BSD license][license].

## Licensing

The source code has [a BSD 3-Clause license][license].

[The Jamie model][jaime] has a BSD 3-Clause license.


## How to build JmePower from source

1. Install a [Java Development Kit (JDK)][adoptium],
   if you don't already have one.
2. Point the `JAVA_HOME` environment variable to your JDK installation:
   (The path might be something like "C:\Program Files\Java\jre1.8.0_301"
   or "/usr/lib/jvm/java-8-openjdk-amd64/" or
   "/Library/Java/JavaVirtualMachines/liberica-jdk-17-full.jdk/Contents/Home" .)
  + using Bash or Zsh: `export JAVA_HOME="` *path to installation* `"`
  + using Windows Command Prompt: `set JAVA_HOME="` *path to installation* `"`
  + using PowerShell: `$env:JAVA_HOME = '` *path to installation* `'`
3. Download and extract the JmePower source code from GitHub:
  + using [Git]:
    + `git clone https://github.com/stephengold/JmePower.git`
    + `cd JmePower`
    + `git checkout -b latest 0.4.4`
  + using a web browser:
    + browse to [the latest release][latest]
    + follow the "Source code (zip)" link
    + save the ZIP file
    + extract the contents of the saved ZIP file
    + `cd` to the extracted directory/folder
4. Run the [Gradle] wrapper:
  + using Bash or PowerShell or Zsh: `./gradlew build`
  + using Windows Command Prompt: `.\gradlew build`

After a successful build,
Maven artifacts will be found
in `JmePowerLibrary/build/libs` and `LemurPower/build/libs`.

You can install the artifacts to your local Maven repository:
+ using Bash or PowerShell or Zsh: `./gradlew install`
+ using Windows Command Prompt: `.\gradlew install`

You can restore the project to a pristine state:
+ using Bash or PowerShell or Zsh: `./gradlew clean`
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
The LemurPower library depends on the JmePower library
and also on [the Lemur toolkit][lemur].

For projects built using Maven or Gradle, the build tools should automatically
resolve the compile-time dependencies.
However, the Lemur toolkit requires [Groovy] support at runtime,
so you may also need to add a Groovy library
(such as groovy-jsr223 or groovy-all) to the runtime classpath.

#### Gradle-built projects

Add to the project’s "build.gradle" file:

    repositories {
        mavenCentral()
    }
    dependencies {
        implementation 'com.github.stephengold:JmePower:0.4.4'
    }

OR

    repositories {
        mavenCentral()
    }
    dependencies {
        implementation 'com.github.stephengold:LemurPower:0.4.4'
        runtime 'org.codehaus.groovy:groovy-jsr223:3.0.11'
    }

### Instantiate and attach an AppState

For an application that don't use Lemur, instantiate a `JmeLoadingState`.
For an application that uses Lemur, instantiate a `LemurLoadingState`.

The `AppState` constructor takes an array of objects
to be preloaded into the application's asset cache.
If there are none, the array can be empty.

Depending on the application's structure, the instance might be
attached explicitly in `simpleInitApp()`:

    LemurLoadingState loading = new LemurLoadingState(preloadArray);
    stateManager.attach(loading);

or it might be passed to the application's constructor:

    private MyApplication() {
        super(
                // other appstates, if desired ...
                new LemurLoadingState(preloadArray)
        );
    }

### Await completion

The appstate takes indicates completion by disabling itself.
A `SimpleApplication` might check for completion in `simpleUpdate()`:

    @Override
    public void simpleUpdate(float tpf) {
        AppState loading = stateManager.getState(LemurLoadingState.class);
        if (loading != null && !loader.isEnabled()) {
            getStateManager().detach(loading);
            // additional startup, if desired ...
        }
    }


[adoptium]: https://adoptium.net/releases.html "Adoptium Project"
[git]: https://git-scm.com "Git"
[gradle]: https://gradle.org "Gradle Project"
[groovy]: https://groovy-lang.org/ "Groovy Project"
[heart]: https://github.com/stephengold/Heart "Heart Project"
[jaime]: https://github.com/stephengold/JmePower/tree/master/JmePowerLibrary/src/main/resources/Models/Jaime "Jaime model"
[jme]: https://jmonkeyengine.org "jMonkeyEngine Project"
[jmepower]: https://github.com/stephengold/JmePower "JmePower Project"
[latest]: https://github.com/stephengold/JmePower/releases/latest "latest release"
[lemur]: https://github.com/jMonkeyEngine-Contributions/Lemur "Lemur toolkit"
[license]: https://github.com/stephengold/JmePower/blob/master/license.txt "JmePower license"
[openJDK]: https://openjdk.java.net "OpenJDK Project"
