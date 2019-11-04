package org.greenblitz.motion.profiling.curve;

import org.greenblitz.motion.base.Point;
import org.greenblitz.motion.base.TwoTuple;

import java.util.ArrayList;

/**
 *
 * @author alexey
 * @deprecated Not tested, most likely bugge
 */
@Deprecated
public class CurveList extends AbstractCurve {

    public ArrayList<ICurve> curves;

    public CurveList(ArrayList<ICurve> curves, double tStart, double tEnd){
        uStart = tStart;
        uEnd = tEnd;
        this.curves = curves;
    }

    public TwoTuple<Integer, Double> getCluster(double u){
        if (u == 1){
            return new TwoTuple<>(curves.size() - 1, 1.0);
        }
        int cluster = (int)(u * curves.size());
        return new TwoTuple<>(cluster, u*curves.size() - cluster);
    }



    @Override
    protected Point getLocationInternal(double u) {
        TwoTuple<Integer, Double> cluster = getCluster(u);
        return curves.get(cluster.getFirst()).getLocation(cluster.getSecond());
    }

    @Override
    protected double getLinearVelocityInternal(double u) {
        TwoTuple<Integer, Double> cluster = getCluster(u);
        return curves.get(cluster.getFirst()).getLinearVelocity(cluster.getSecond());
    }

    @Override
    protected double getAngularVelocityInternal(double u) {
        TwoTuple<Integer, Double> cluster = getCluster(u);
        return curves.get(cluster.getFirst()).getAngularVelocity(cluster.getSecond());
    }

    @Override
    protected double getLengthInternal(double u) {
        TwoTuple<Integer, Double> cluster = getCluster(u);
        TwoTuple<Integer, Double> uStartClust = getCluster(uStart);
        double len =
                curves.get(uStartClust.getFirst()).getLength(1) -
                curves.get(uStartClust.getFirst()).getLength(uStartClust.getSecond());
        for (int i = uStartClust.getFirst() + 1; i < cluster.getFirst(); i++){
            len += curves.get(i).getLength(1.0);
        }
        return len + curves.get(cluster.getFirst()).getLength(cluster.getSecond());
    }

    @Override
    protected double getAngleInternal(double u) {
        TwoTuple<Integer, Double> cluster = getCluster(u);
        return curves.get(cluster.getFirst()).getAngle(cluster.getSecond());
    }

    @Override
    protected double getCurvatureInternal(double u) {
        TwoTuple<Integer, Double> cluster = getCluster(u);
        return curves.get(cluster.getFirst()).getCurvature(cluster.getSecond());
    }

    @Override
    public ICurve getSubCurve(double uStart, double uEnd) {
        return new CurveList(curves, uStart, uEnd);
    }
}
