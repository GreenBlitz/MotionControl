package org.greenblitz.motion.profiling;

import org.greenblitz.motion.base.Point;
import org.greenblitz.motion.base.Position;
import org.greenblitz.motion.base.State;
import org.greenblitz.motion.base.Vector2D;
import org.greenblitz.motion.profiling.curve.BezierCurve;
import org.greenblitz.motion.profiling.curve.ICurve;
import org.greenblitz.utils.CSVWrapper;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class ChassisProfiler2DCSVGenerators {

    final double EPSILON = 1E-6;

    @Test
    void generate2DProfile() {
        List<State> states = new ArrayList<>();
        states.add(new State(0, 0, 0, 0, 0));
        states.add(new State(3, 5, 0, 0, 0));

        MotionProfile2D brofile = ChassisProfiler2D.generateProfile(states, 0.000001, 0.0002, 5,
                Math.PI, 5 * 2, 2 * Math.PI);

        CSVWrapper broFile = CSVWrapper.generateWrapper("profile.csv", 0, "t", "x", "y", "linearV", "angularV", "linearA", "angularA");

        Position loc = null;
        Vector2D vel, acc;
        for (double t = 0; t < brofile.getTEnd(); t += 0.01) {
            System.out.println(brofile.getTEnd());
            if (t == 0)
                loc = new Position(0, 0, 0);
            else
                loc = brofile.getActualLocation(t, loc, t - 0.01, EPSILON);
            vel = brofile.getVelocity(t);
            acc = brofile.getAcceleration(t);
            broFile.addValues(t, loc.getX(), loc.getY(), vel.getX(), vel.getY(), acc.getX(), acc.getY());
            System.out.println(loc.getAngle() + ", " + vel.getY());
        }
        broFile.flush();
        CSVWrapper locFile = CSVWrapper.generateWrapper("location.csv", 0, "x", "y");

        for (double t = 0; t < brofile.getTEnd(); t += 0.01) {
            if (t == 0)
                loc = new Position(0, 0, 0);
            else
                loc = brofile.getActualLocation(t, loc, t - 0.01, EPSILON);
            locFile.addValues(loc.getX(), loc.getY());
            System.out.println(loc.getAngle());
        }
        ICurve curve = new BezierCurve(states.get(0), states.get(1));
        for (double u = 0; u <= 1; u += 0.001) {
            Point p = curve.getLocation(u);
            locFile.addValues(p.getX(), p.getY());
        }
        locFile.flush();

        System.out.println(brofile.getTEnd());

        System.out.println(brofile);

    }

}
