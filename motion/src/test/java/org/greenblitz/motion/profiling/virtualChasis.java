package org.greenblitz.motion.profiling;

import org.greenblitz.motion.base.State;

public class virtualChasis {

    State right, left;
    long time = Long.MIN_VALUE;

    public virtualChasis(State right, State left){
        this.left = left;
        this.right = right;
    }

    public void initialize(){time = System.currentTimeMillis();}

    public void update(double rightPower, double leftPower){
        long dTime = System.currentTimeMillis() - time;
    }
}
