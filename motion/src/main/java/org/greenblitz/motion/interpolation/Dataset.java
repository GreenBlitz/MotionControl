package org.greenblitz.motion.interpolation;

import org.greenblitz.motion.base.TwoTuple;

import java.util.ArrayList;
import java.util.List;

public class Dataset {

    private int dimension;
    private List<TwoTuple<Double, double[]>> data;

    public Dataset(int dim){
        dimension = dim;
        data = new ArrayList<>();
    }

    public void addDatapoints(Iterable<TwoTuple<Double, double[]>> points) {
        for (TwoTuple<Double, double[]> p : points) {
            addDatapoint(p.getFirst(), p.getSecond());
        }
    }

    public void addDatapoint(double x, double[] y) {
        if (y.length != dimension - 1){
            throw new RuntimeException("The dimension of this dataset is " + dimension + " but " + (y.length + 1)
                    + " was given.");
        }
        if (data.size() == 0) {
            data.add(new TwoTuple<>(x, y));
            return;
        }
        int index = 0;
        for (TwoTuple<Double, double[]> point : data) {
            if (point.getFirst() >= x) {
                if (point.getFirst() == x) {
                    point.setSecond(y);
                    return;
                }
                break;
            }
            index += 1;
        }

        addAt(index, new TwoTuple<>(x, y));
    }

    private void addAt(int index, TwoTuple<Double, double[]> newData){
        if (index == data.size()){
            data.add(newData);
            return;
        }
        TwoTuple<Double, double[]> wasAtIndex = data.get(index);
        data.set(index, newData);
        addAt(index + 1, wasAtIndex);
    }

    public TwoTuple<TwoTuple<Double, double[]>, TwoTuple<Double, double[]>> getAdjesent(double x) {
        if (data.size() < 2 || x < data.get(0).getFirst() || x > data.get(data.size() - 1).getFirst()) {
            throw new RuntimeException("point given now within range of dataset, or data set is smaller than 2 points");
        }
        int index = 0;
        for (TwoTuple<Double, double[]> point : data) {
            if (point.getFirst() >= x) {
                if (point.getFirst() == x) {
                    return new TwoTuple<>(point, point);
                }
                break;
            }
            index += 1;
        }
        return new TwoTuple<>(data.get(index - 1), data.get(index));
    }

    public double[] linearlyInterpolate(double x){
        TwoTuple<TwoTuple<Double, double[]>, TwoTuple<Double, double[]>> data = getAdjesent(x);
        double weight = (x - data.getFirst().getFirst()) / (data.getSecond().getFirst() - data.getFirst().getFirst());
        double[] ret = new double[dimension];
        for (int i = 0; i < dimension - 1; i++){
            ret[i] = data.getFirst().getSecond()[i] +
                    (data.getSecond().getSecond()[i] - data.getFirst().getSecond()[i]) * weight;
        }
        return ret;
    }

    @Override
    public String toString() {
        StringBuilder ret = new StringBuilder("Dateset = \n");
        for (TwoTuple<Double, double[]> val : data){
            ret.append("(").append(val.getFirst()).append(", ");
            for (double d : val.getSecond()){
                ret.append(d).append(", ");
            }
            ret.append(")\n");
        }
        return ret.toString();
    }
}
