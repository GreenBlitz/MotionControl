package org.greenblitz.motion;

import org.greenblitz.motion.base.State;
import org.greenblitz.motion.profiling.curve.spline.PolynomialCurve;
import org.greenblitz.motion.profiling.curve.spline.QuinticSplineGenerator;

import java.util.ArrayList;

public class CurveTesting{
    private static final String ABC = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    public static void main(String[] args) {
        //Galactic Search Challenge
        //RED Path A
        ArrayList<State> redPathA = new ArrayList<>();
        redPathA.add(new State(inchToMeter(1), inchToMeter(4), -2.11, 0, 0));
        redPathA.add(new State(inchToMeter(3), inchToMeter(3), -2.11, 3.6, 4));
        redPathA.add(new State(inchToMeter(5), inchToMeter(2), -1.975, 3, 8));
        redPathA.add(new State(inchToMeter(6), inchToMeter(5), -1.77, 3.6, 9.5));
        redPathA.add(new State(inchToMeter(11), inchToMeter(4), -0.5*Math.PI, 0, 0));
        printPath(redPathA);
    }

    public static void printPath(ArrayList<State> path){
        int k = 0;
        for(int i = 0; i < path.size() - 1; i++){
            PolynomialCurve currCurve = QuinticSplineGenerator.generateSpline(path.get(i), path.get(i+1));
            char currLetterA = ABC.charAt(k);
            char currLetterB = ABC.charAt(k+1);
            k += 2;
            String strX = currLetterA + "(t) = ";
            String strY = currLetterB + "(t) = ";
            for(int j = currCurve.getRank(); j > 0; j--){
                if(Math.abs(currCurve.getX()[j]) >= 0.0001)
                    strX += currCurve.getX()[j] + "t^" + j + " + ";
                if(Math.abs(currCurve.getY()[j]) >= 0.0001)
                    strY += currCurve.getY()[j] + "t^" + j + " + ";
            }
            System.out.println("("+currLetterA+"(0),"+currLetterB+"(0))\n("+currLetterA+"(1),"+currLetterB+"(1))\n("+currLetterA+"(t),"+currLetterB+"(t))\n" + strX + currCurve.getX()[0] + "\n" + strY + currCurve.getY()[0]);
        }
    }

    public static double inchToMeter(double inch){
        return 0.762 * inch;
    }
}
