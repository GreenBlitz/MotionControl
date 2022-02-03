package org.greenblitz.motion.auto;

import org.greenblitz.motion.base.State;

import java.util.ArrayList;

import static org.greenblitz.motion.CurveTesting.plotPath;

public class RapidReeee {
    public static void main(String[] args) {
        //Rapid React Challenge

        //Path for the first 2 balls
        ArrayList<State> firstTwo = new ArrayList<>();
        firstTwo.add(new State(1, 1.78, -1.51, 0, 0));
        firstTwo.add(new State(3.1, 2.2, -1.11, 3.6, 4));

        //Inorder to shoot, go back in a linear line for 3.13 meter
        //firstTwo.add(new State(0.3, 0.8, -1.11, 3.6, 4));

        //Path for balls 3 and 4
        ArrayList<State> lastTwo = new ArrayList<>();
        lastTwo.add(new State(0.3,0.8,-1.11,0,0));
        lastTwo.add(new State(7.1,3,-1.11,3.6,4));

        //Run last course the other direction

        new Thread(() -> {plotPath(firstTwo, "First Two Balls");}).start();
        new Thread(() -> {plotPath(lastTwo, "Last Two Balls");}).start();
    }
}
