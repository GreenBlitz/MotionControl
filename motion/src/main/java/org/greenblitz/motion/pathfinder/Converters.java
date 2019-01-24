package org.greenblitz.motion.pathfinder;

import jaci.pathfinder.Waypoint;
import org.greenblitz.motion.base.Point;
import org.greenblitz.motion.base.Position;

public class Converters {
    public static Waypoint fromPoint(Point p) {
        return new Waypoint(p.getX(), p.getY(), 0);
    }

    public static Waypoint fromPosition(Position p) {
        return new Waypoint(p.getX(), p.getY(), p.getAngle());
    }
}
