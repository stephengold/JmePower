# Release log for the JmePower project

## Version 1.1.0 released on 9 November 2022

+ Bugfix:  if the tasks finish quickly, the cinematic may terminate too soon.
+ Add the JmePowerExamples subproject.
+ Display status in the upper-left corner of the loading screen.
+ Save/restore the camera's state.
+ Log the latency of each task.
+ Publicize the logger of the `Preloader` class.
+ Build using Gradle v7.5.1 and Heart v8.2.0.

## Version 1.0.0 released on 27 June 2022

+ Delete the LemurPower subproject.
+ Depend on Heart v8.0.0 and JME v3.5.2-stable.
+ Add the "checkstyle" plugin to the build.

## Version 0.4.4 released on 5 April 2022

+ Widen the spotlight's outer angle in the loading screen.
+ Target Java v8+.
+ Build using Gradle v7.4.2, Heart v7.5.0, JME v3.5.1-stable,
  and Lemur v1.16.0 .

## Version 0.4.3 released on 29 August 2021

Build using Gradle v7.2 and Heart v7.0.0 .

## Version 0.4.2 released on 9 June 2021

+ Use the new (com.jme3.anim) animation system.
+ Darken shadows.
+ Build using Gradle v7.0.2, Heart v6.4.4, JME v3.4.0-stable,
  and Lemur v1.15.0 .

## Version 0.4.1 released on 11 February 2021

+ Publish to MavenCentral instead of JCenter.
+ Build using Gradle v6.8.2 and Heart v6.4.2 .

## Version 0.4.0 released on 30 January 2021

+ Build using Heart v6.4.0 and use its version of the `Loadable` class.
+ Add the `JmePowerVersion` class.
+ Add package-info files.

## Version 0.3.0 released on 29 January 2021

Split off the JmePowerLibrary sub-project.  Now there are 2 libraries
(JmePower and LemurPower) with LemurPower depending on JmePower.

## Version 0.2.0 released on 27 January 2021

+ Pass the `AssetManager` to `load()` methods. (API change)
+ Split off `JmeLoadingState` from the `LemurLoadingState` class.
+ Build using Gradle v6.8.1 and Heart v6.3.0 .

## Version 0.1.0 released on 14 January 2021

This was the initial baseline release, based largely on code formerly
included in Heart and More Advanced Vehicles.