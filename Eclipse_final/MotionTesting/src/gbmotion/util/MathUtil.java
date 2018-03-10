package gbmotion.util;

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
}
