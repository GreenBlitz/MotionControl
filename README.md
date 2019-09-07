# GBMotion - Greenblitz #4590
Welcome to the motion control repository of the FRC team #4590. The repository was opened in the
2018 season and is being worked on and improved every season.
## Usage
This project relies upon [jitpack](https://jitpack.io/) in order to implement itself inside other projects.

In order to add this project you will need to use [gradle](https://gradle.org/). In your `build.gradle` file you should add the section:

    repositories {
        jcenter()
        maven { url "https://jitpack.io" }
        mavenCentral()
    }

And alongside that, in your `dependencies` section add the line:

    compile 'com.github.GreenBlitz.MotionControl:motion:master-SNAPSHOT'

This should add the most recent release of the software into your project. In case this results
in issues, try to replace the `master-SNAPSHOT` part with the hash the the most recent commit (If you
do it that way, you will need to update the hash manually).
## Features
### Adaptive Pure Pursuit
The repository contains an implementation of the pure pursuit algorithm. The algorithm requires to know the 
current location of the robot, and for that a `Localizer` class is supplied.
#### Localizer
The `Localizer` will calculate the movement of the robot when given the amount of meters each side of
the chassis has covered since the last encoder reset. It is optionally possible to pass an angle to the
algorithm (for example from a gyro sensor) but if no angle is passed the algorithm will calculate the angle
on its on (note that this may be less accurate).

The localizer has been tested thoroughly and was shown to be accurate enough for the _APPC_ to work.
Non the less it is still recommended to use an angle value from an external sensor such as a gyro. 

As with all of out features, the localizer is in essence just a collection of commands, and the use
of those command in the robot code is left as a decision for the user.
#### Paths
The paths supplied to the _APPC_ are simple lists of `Point` objects. It is recommended to generate the paths
with an external tool such as [PathWeaver](https://github.com/wpilibsuite/PathWeaver).
#### The _APPC_ itself
The controller object is essentially an interface of commands, out of which the most essential command to the 
user is `iteration(T robotLocation)`. This function returns a double array who's first value is the power
you need to supply to the left motor and the second value is for the right motor.

Using this command you can easily incorporate the calculation of the controller into your main robot loop.
### Custom PID controller - WIP
TODO
### 1D Continuous Motion Profile Generation - WIP
TODO
### 2D Continuous Motion Profile Generation For Tank Drive - WIP
TODO
## Contributing
TODO