# GBMotion - Greenblitz #4590
Welcome to the motion control repository of FRC team #4590. The repository was opened in the
2018 season and is being worked on and improved every season.
## Usage
This project relies upon [jitpack](https://jitpack.io/) in order to implement it inside other projects.

In order to add this project you will need to use [gradle](https://gradle.org/). In your `build.gradle` file you should add:

    repositories {
        jcenter()
        maven { url "https://jitpack.io" }
        mavenCentral()
    }

And alongside that, in your `dependencies` section add the line:

    compile 'com.github.GreenBlitz.MotionControl:motion:master-SNAPSHOT'

This should add the most recent release of the software into your project. In case this results
in issues, replace the `master-SNAPSHOT` part with the hash the the most recent commit (If you
do it that way, you will need to update the hash manually).
## Features
### Adaptive Pure Pursuit
TODO
### Custom PID controller - WIP
TODO
### 1D Continuous Motion Profile Generation - WIP
TODO
### 2D Continuous Motion Profile Generation For Tank Drive - WIP
TODO