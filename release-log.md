# Release log for the JmePower project

## Version 0.4.1 released on TBD

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