package org.greenblitz.motion;

import jaci.pathfinder.Trajectory;
import jaci.pathfinder.Waypoint;
import org.greenblitz.motion.pathfinder.PathfinderException;

import java.util.Arrays;
import java.util.TimerTask;

/**
 * This is the Ben-Guruion controller. It makes a path and it always stays on it by changing it.
 * BGC, Just like Ben-Guruion, is never wrong. It's the truth that's wrong.
 * TODO - Atsmon doesn't approve the name
 */
@Deprecated
public class BGController {

    protected Trajectory current;
    long miliDelay;
    protected TimerTask task;

    public BGController(Trajectory initial, long delay){
        this.current = initial;
        this.miliDelay = delay;
    }

    /*private void updatePath(Trajectory.Segment desired, Waypoint currentloc){
        Waypoint desiredWaypoint = new Waypoint(desired.x, desired.y, desired.heading);
        Trajectory newPart;
        try {
             newPart = GenerateTrajectory.generateTrajectory(new Waypoint[]{currentloc, desiredWaypoint},
                    Trajectory.FitMethod.HERMITE_CUBIC, Trajectory.Config.SAMPLES_FAST, miliDelay / 1000.0);
        } catch (PathfinderException e){
            e.printStackTrace();
            return;
            // TODO Handle this somehow???
        }

        Trajectory.Segment[] currentPath = current.segments;
        Trajectory.Segment[] newPath = newPart.segments;
        int cutOffPoint = Arrays.asList(currentPath).indexOf(desired) + 1;
        int newArrLen = currentPath.length - cutOffPoint + newPath.length;

        Trajectory.Segment[] newFullPath = new Trajectory.Segment[newArrLen];

        System.arraycopy(newPath, 0, newFullPath, 0, newPath.length);
        System.arraycopy(currentPath, cutOffPoint , newFullPath, newPath.length, currentPath.length - cutOffPoint);

        current = new Trajectory(newFullPath);
    }*/

}
