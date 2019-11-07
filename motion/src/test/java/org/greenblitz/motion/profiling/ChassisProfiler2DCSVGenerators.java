package org.greenblitz.motion.profiling;

import org.greenblitz.motion.base.Point;
import org.greenblitz.motion.base.Position;
import org.greenblitz.motion.base.State;
import org.greenblitz.motion.base.Vector2D;
import org.greenblitz.motion.profiling.curve.bazier.BezierCurve;
import org.greenblitz.motion.profiling.curve.ICurve;
import org.greenblitz.motion.profiling.curve.spline.CubicSplineGenerator;
import org.greenblitz.motion.profiling.curve.spline.ThirdDegreePolynomialCurve;
import org.greenblitz.utils.CSVWrapper;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class ChassisProfiler2DCSVGenerators {

    final double EPSILON = 1E-6;

    @Test
    void generate2DProfile() {
        List<State> states = new ArrayList<>();
        states.add(new org.greenblitz.motion.base.State(0, 0, 0, 0, 0));
        states.add(new org.greenblitz.motion.base.State(0.8, 3, Math.PI/4, 0, 0));
        states.add(new org.greenblitz.motion.base.State(2, 3, Math.PI/2, 0, 0));

        long time = System.currentTimeMillis();
        MotionProfile2D brofile = ChassisProfiler2D.generateProfile(states, 0.005, 0.7,
                2.1, 4.6, 10, 0, .97f);
        System.out.println("Full Generation");
        System.out.println(System.currentTimeMillis() - time);

        CSVWrapper broFile = CSVWrapper.generateWrapper("profile.csv", 0, "t", "x", "y", "linearV", "angularV", "linearA", "angularA");

        System.out.println("Tend " + brofile.getTEnd());

        Position loc = null;
        final double jmp = 0.001;
        Vector2D vel, acc;
        for (double t = 0; t < brofile.getTEnd(); t += jmp) {
            if (t == 0)
                loc = new Position(0, 0, 0);
            else
                loc = brofile.getActualLocation(t, loc, t - jmp, EPSILON);
//            System.out.println(loc);
            vel = brofile.getVelocity(t);
            acc = brofile.getAcceleration(t);
            broFile.addValues(t, loc.getX(), loc.getY(), vel.getX(), vel.getY(), acc.getX(), acc.getY());
        }
        broFile.flush();

        CSVWrapper locFile = CSVWrapper.generateWrapper("location.csv", 0, "x", "y");
        for (double t = 0; t < brofile.getTEnd(); t += jmp) {
            if (t == 0)
                loc = new Position(0, 0, 0);
            else
                loc = brofile.getActualLocation(t, loc, t - jmp, EPSILON);
            locFile.addValues(loc.getX(), loc.getY());
//            System.out.println(loc);
        }
        ICurve curve = CubicSplineGenerator.generateSpline(states.get(0), states.get(1), 0.8);
        for (double u = 0; u <= 1; u += 0.001) {
            Point p = curve.getLocation(u);
            locFile.addValues(-p.getX(), p.getY());
        }
        locFile.flush();

    }

}
