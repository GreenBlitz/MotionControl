package org.greenblitz.motion;

import org.greenblitz.motion.base.State;
import org.greenblitz.motion.profiling.curve.spline.PolynomialCurve;
import org.greenblitz.motion.profiling.curve.spline.QuinticSplineGenerator;

import java.io.*;
import java.nio.charset.StandardCharsets;

import java.util.ArrayList;

import java.util.Scanner;

public class CurveTesting{
    public static void main(String[] args) {
        //Galactic Search Challenge
        //RED Path A
        ArrayList<State> redPathA = new ArrayList<>();
        redPathA.add(new State(inchToMeter(1), inchToMeter(4), -2.11, 0, 0));
        redPathA.add(new State(inchToMeter(3), inchToMeter(3), -2.11, 3.6, 4));
        redPathA.add(new State(inchToMeter(5), inchToMeter(2), -1.975, 3, 8));
        redPathA.add(new State(inchToMeter(6), inchToMeter(5), -1.77, 3.6, 9.5));
        redPathA.add(new State(inchToMeter(11), inchToMeter(4), -0.5*Math.PI, 0, 0));

        //BLUE Path A
        ArrayList<State> bluePathA = new ArrayList<>();
        bluePathA.add(new State(inchToMeter(1),inchToMeter(2),-1.33,0,0));
        bluePathA.add(new State(inchToMeter(6),inchToMeter(1),-1.33,3.6,2));
        bluePathA.add(new State(inchToMeter(7),inchToMeter(4),-1.9,3.6,2));
        bluePathA.add(new State(inchToMeter(9),inchToMeter(3),-1.9,3.6,2));
        bluePathA.add(new State(inchToMeter(11),inchToMeter(2),-1.9,0,0));

        //RED Path B
        ArrayList<State> redPathB = new ArrayList<>();
        redPathB.add(new State(inchToMeter(1),inchToMeter(5.5),-2.5,0,0));
        redPathB.add(new State(inchToMeter(3),inchToMeter(4),-2.52,3.6,7)); //-3.12
        redPathB.add(new State(inchToMeter(5),inchToMeter(2),-1.55,3.6,9.5));
        redPathB.add(new State(inchToMeter(7),inchToMeter(4),-1.15,3.6,9.5));
        redPathB.add(new State(inchToMeter(11),inchToMeter(5),-1.15,0,0));

        //BLUE Path B
        ArrayList<State> bluePathB = new ArrayList<>();
        bluePathB.add(new State(inchToMeter(1),inchToMeter(2),-0.5*Math.PI,0,0));
        bluePathB.add(new State(inchToMeter(6),inchToMeter(2),-1.55,3.6,9.5));
        bluePathB.add(new State(inchToMeter(8),inchToMeter(4),-1.75,3.6,9.5));
        bluePathB.add(new State(inchToMeter(10),inchToMeter(2),-1.85,3.6,6.5));
        bluePathB.add(new State(inchToMeter(11),inchToMeter(2),-0.5*Math.PI,0,0));

        new Thread(() -> {plotPath(redPathA, "Red Path A");}).start();
        new Thread(() -> {plotPath(bluePathA, "Blue Path A");}).start();
        new Thread(() -> {plotPath(redPathB,  "Red Path B");}).start();
        new Thread(() -> {plotPath(bluePathB,  "Blue Path B");}).start();
    }

    public static void plotPath(ArrayList<State> path, String pathName){
        // arguments for new process
        String[] args = new String[path.size() + 2];
        args[0] = "python";
        args[1] = System.getProperty("user.dir") + "\\motion\\src\\test\\java\\org\\greenblitz\\motion\\Plotter.py";
        args[2] = pathName;

        // creating the polynomial strings for the x and y
        for(int i = 0; i < path.size() - 1; i++){
            PolynomialCurve currCurve = QuinticSplineGenerator.generateSpline(path.get(i), path.get(i+1));
            String strX = "";
            String strY = "";
            for(int j = 0; j < currCurve.getRank(); j++){
                strX += currCurve.getX()[j] + ",";
                strY += currCurve.getY()[j] + ",";
            }
            strX += currCurve.getX()[currCurve.getRank()];
            strY += currCurve.getY()[currCurve.getRank()];
            args[i+3] = strX + "|" + strY;
        }

        // calling the python plotter
        try {
            Process process = Runtime.getRuntime().exec(args);
        } catch (Exception e) {
            System.out.println("Exception Raised " + e.toString());
        }
    }

    public static double inchToMeter(double inch){
        return 0.762 * inch;
    }
}
