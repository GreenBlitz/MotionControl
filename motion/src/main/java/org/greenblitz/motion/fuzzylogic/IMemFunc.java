package org.greenblitz.motion.fuzzylogic;

public interface IMemFunc {

    /**
     * func output must be between 0 and 1
     * @param normalizedVal double between -1 and 1 for input and between 0 and  1 for output func
     * @return
     */
    public double membershipFunction(double normalizedVal);


}

