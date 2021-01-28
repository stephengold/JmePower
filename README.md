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
     + `git checkout -b latest 0.2.0`
   + using a web browser:
     + browse to https://github.com/stephengold/JmePower/releases/latest
     + follow the "Source code (zip)" link
     + save the ZIP file
     + extract the contents of the saved ZIP file
     + `cd` to the extracted directory/folder
 3. Set the `JAVA_HOME` environment variable:
   + using Bash:  `export JAVA_HOME="` *path to your JDK* `"`
   + using Windows Command Prompt:  `set JAVA_HOME="` *path to your JDK* `"`
 4. Run the [Gradle] wrapper:
   + using Bash:  `./gradlew build`
   + using Windows Command Prompt:  `.\gradlew build`

After a successful build,
Maven artifacts will be found in `LemurPower/build/libs`.

You can install the Maven artifacts to your local cache:
 + using Bash:  `./gradlew install`
 + using Windows Command Prompt:  `.\gradlew install`


## How to add JmePower to an existing project

Adding JmePower to an existing [jMonkeyEngine][jme] project should be
a simple 3-step process:

 1. Add the LemurPower library and its dependencies to the classpaths.
 2. Add code to instantiate and attach a `LemurLoadingState`.
 3. Add code to await completion.

### Add to the classpaths

The LemurPower library depends on
the standard jme3-core library from jMonkeyEngine
and also on [the Lemur toolkit][lemur].

For projects built using Maven or Gradle, the build tools should automatically
resolve the compile-time dependencies.
However, the Lemur toolkit requires [Groovy][] support at runtime,
so you may also need to add a Groovy library
(such as groovy-jsr223 or groovy-all) to the runtime classpath.

#### Gradle-built projects

Add to the project’s "build.gradle" file:

    repositories {
        jcenter()
    }
    dependencies {
        compile 'com.github.stephengold:LemurPower:0.2.0'
        runtime 'org.codehaus.groovy:groovy-jsr223:3.0.7'
    }

### Instantiate and attach an AppState

The `LemurLoadingState` constructor takes an array of objects
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


[groovy]: https://groovy-lang.org/ "Groovy Project"
[jaime]: https://github.com/stephengold/JmePower/tree/master/LemurPower/src/main/resources/Models/Jaime "Jaime model"
[jme]: https://jmonkeyengine.org "JMonkeyEngine Project"
[jmepower]: https://github.com/stephengold/JmePower "JmePower Project"
[latest]: https://github.com/stephengold/JmePower/releases/latest "latest release"
[lemur]: https://github.com/jMonkeyEngine-Contributions/Lemur "Lemur toolkit"
[license]: https://github.com/stephengold/JmePower/blob/master/license.txt "JmePower license"
[openJDK]: https://openjdk.java.net "OpenJDK Project"
