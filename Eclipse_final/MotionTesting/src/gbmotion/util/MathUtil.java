package gbmotion.util;

import gbmotion.base.point.IPoint2D;
import gbmotion.base.point.ImPoint2D;
import gbmotion.base.point.Point2D;

public class MathUtil {
	public static double normalizeAngle(double angle) {
		while (angle > 2 * Math.PI) {
			angle -= 2 * Math.PI;
		}
		while (angle < 0) {
			angle += 2 * Math.PI;
		}
		return angle;
	}
	
	/**
	 * only to be used with immutables
	 * @param start
	 * @param end
	 * @param part
	 * @return
	 */
	private static IPoint2D between(IPoint2D start, IPoint2D end, double part){
		return start.scale(1-part).moveBy(end.scale(part));
	}
	
	/**
	 * only to be used with immutables
	 * @param close
	 * @param far
	 * @param dist
	 * @return
	 */
	public static ImPoint2D nextTo(IPoint2D close, IPoint2D far, double dist){
		return Point2D.immutable(between(close, far, dist/close.distance(far)));
	}

	public static IPoint2D bazierPhase(double phase, ImPoint2D... poles) {
		return bazierPhase(phase, poles.length, poles);
	}

	private static IPoint2D bazierPhase(double phase, int relevant, IPoint2D... poles){
		if(relevant<=1) return poles[0];
		for(int i=0; i<relevant-1;){
			poles[i] = between(poles[i], poles[i+1], phase);
		}
		return bazierPhase(phase, --relevant, poles);
	}
}
