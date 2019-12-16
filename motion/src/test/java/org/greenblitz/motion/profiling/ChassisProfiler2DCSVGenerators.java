package org.greenblitz.motion.profiling;

import org.greenblitz.motion.base.Point;
import org.greenblitz.motion.base.Position;
import org.greenblitz.motion.base.State;
import org.greenblitz.motion.base.Vector2D;
import org.greenblitz.motion.pathing.Path;
import org.greenblitz.motion.profiling.curve.bazier.BezierCurve;
import org.greenblitz.motion.profiling.curve.ICurve;
import org.greenblitz.motion.profiling.curve.spline.CubicSplineGenerator;
import org.greenblitz.motion.profiling.curve.spline.PolynomialCurve;
import org.greenblitz.motion.profiling.curve.spline.QuinticSplineGenerator;
import org.greenblitz.motion.profiling.curve.spline.ThirdDegreePolynomialCurve;
import org.greenblitz.utils.CSVWrapper;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class ChassisProfiler2DCSVGenerators {

    final double EPSILON = 1E-6;

    List<State> generateCircle(){
        List<State> ret = new ArrayList<>();
        for (int i = 0; i <= 50; i++){
            double percent = i/50.0;
            ret.add(new State(Math.cos(percent*Math.PI/2), Math.sin(percent*Math.PI/2), -i*Math.PI/2, -1, 1));
        }
        return ret;
    }


    @Test
    void generate2DProfile() {
        List<State> states = new ArrayList<>();

        states.add(new State(0, 0, 0, 0, 0));
        states.add(new State(1, 2,Math.PI/2, 0, 0));
        states.add(new State(2, 4,0, 0, 0));


        long time = System.currentTimeMillis();
        MotionProfile2D brofile = ChassisProfiler2D.generateProfile(
                states//pathToState(Paths.get("LTurn", true))
                , .0001, 3,
                8, 4.5, 13, 0, 1.0, 800);
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
        locFile.flush();
        PolynomialCurve curve = QuinticSplineGenerator.generateSpline(states.get(0), states.get(1), 1);
        for (double u = 0; u <= 1; u += 0.05) {

            Point p = curve.getLocation(u);
//            System.out.println(p);
            locFile.addValues(-p.getX(), p.getY());
        }
//        locFile.flush();
//        PolynomialCurve curve2 = QuinticSplineGenerator.generateSpline(states.get(1), states.get(2), 1);

//        System.out.println("---------------------");
//        System.out.println(curve.getDerivativeInter(1) + ", " + curve2.getDerivativeInter(0));
//        System.out.println(curve.getDoubleDerivativeInter(1) + ", " + curve2.getDoubleDerivativeInter(0));
//
//
//        for (double u = 0; u <= 1; u += 0.05)  {
//            Point p = curve2.getLocation(u);
////            System.out.println(p);
//            locFile.addValues(-p.getX(), p.getY());
//        }
//        locFile.flush();

    }

    List<State> pathToState(Path<Position> pth){
        List<State> retList = new ArrayList<>();
        for (Position p : pth){
            retList.add(new State(p.getX(), p.getY(), p.getAngle()));
        }
        return retList;
    }

}
