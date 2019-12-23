package org.greenblitz.motion.fuzzylogic;

import java.util.ArrayList;
import java.util.List;

public class FuzzyRuleSet {

    private int inputNum;
    private List<FuzzyValue>[] input;
    private List<FuzzyValue> output;
    private List<Rule> rules;

    public FuzzyRuleSet(List<FuzzyValue>[] input, List<FuzzyValue> output, List<Rule> rules) {
        inputNum = input.length;
        this.input = input;
        this.output = output;
        this.rules = rules;
    }

    public FuzzyRuleSet(int inputNum, List<FuzzyValue> input, List<FuzzyValue> output, List<Rule> rules) {
        this.inputNum = inputNum;
        List<FuzzyValue>[] inputVal = new List[inputNum];
        for (int i = 0; i < inputNum; i++) {
            inputVal[i] = input;
        }
        this.input = inputVal;
        this.output = output;
        this.rules = rules;
    }

    public FuzzyRuleSet(int inputNum, List<FuzzyValue> types, List<Rule> rules) {
        this(inputNum, types, types, rules);
    }



    public double  calculate (double[] normalizedValues) throws Exception {
        if (normalizedValues.length != inputNum)
            throw new Exception("you are dumb as hell, you inserted the wrong amount of values to the function") {
            };
        List<Double>[] allMemFuncOut = new ArrayList[inputNum];
        for (int i = 0; i < normalizedValues.length; i++) {
            List<Double> memFuncOut = new ArrayList<>();
            for (FuzzyValue fuzzyValue : input[i]) {
                memFuncOut.add(fuzzyValue.getInMemFunc().membershipFunction(normalizedValues[i]));
            }
            allMemFuncOut[i] = memFuncOut;
        }

        double[] outVals  = new double[output.size()];
        for(Rule rule: rules){
            double value = 1;
            FuzzyValue[] fVals = rule.getConditions();
            for(int i = 0; i < inputNum; i ++){
                value = AND(value,allMemFuncOut[i].get(input[i].lastIndexOf(fVals[i])));
            }
            outVals[output.lastIndexOf(rule.getResult())] = value;
        }


        return defuzz(outVals);
                
        //addAllCombinations(0,1, new ArrayList<>(),null,allMemFuncOut);
    }

    private double defuzz(double[] outVals){
        double eplsilon = 0.01;
        double[] areas = new double[outVals.length];
        for(int i = 0; i < areas.length; i ++){
            double sum = 0;
            if(outVals[i] != 0){
                for(double x = 0; x < 1; x += eplsilon ){
                    sum += eplsilon * Math.min(outVals[i],output.get(i).getOutMemFunc().membershipFunction(x));
                }
            }
            areas[i] =  sum;
        }

        double upperSum = 0;
        for(int i = 0; i < areas.length; i ++){
            if(outVals[i] != 0){
                double sum = 0,x = 0;
                for(; x < 1 && sum < 0.5*areas[i]; x += eplsilon ){
                    sum += eplsilon * Math.min(outVals[i],output.get(i).getOutMemFunc().membershipFunction(x));
                }
                upperSum += x*areas[i];
            }
        }

        double lowerSum = 0;
        for(double area: areas) lowerSum += area;

        return upperSum/lowerSum;
    }



/*
    private void addAllCombinations(int index, double value, List<FuzzyValue> fVals, FuzzyValue fVal, List<Double>[] allMemFuncOut) {
        if (fVal != null) fVals.add(fVal);
        if (index == inputNum) outVals[getOutIndex(fVals)] = value;
        for (int i = 0; i < allMemFuncOut[index].size(); i++) {
            addAllCombinations(index + 1, AND(value, allMemFuncOut[index].get(i)), fVals, fVal, allMemFuncOut);
        }
    }
    */

    public double AND(double a, double b) {
        return a * b;
    }
}