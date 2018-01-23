package tests;


import APPC.Point2D;

public class BaseTester {

	public static void main(String[] args) {
		curveTester();
	}
	
	public static double[] calculateCurve(Point2D loc, Point2D goal) {
		Point2D goalVector = goal.changePrespectiveTo(loc);
		double angle = Math.atan(goalVector.getX() / goalVector.getY()) / Math.PI * 180;
		return new double[] { (2 * goalVector.getX()) / Math.pow(goalVector.length(), 2), angle };
	}
	
	public static void curveTester(){
		Point2D robot = Point2D.GLOBAL_ORIGIN;
		for (double offst = 0.01; offst < 100; offst += 0.01){
			double[] values = calculateCurve(robot, new Point2D(offst, offst, 0));
			if (values[1] > 45.01 || values[1] < 44.99) {
				System.out.println(String.format("Code failed by angle at offst = %d, the curve was %d and angle was %d",
						offst, values[0], values[1]));
			}
			if (1 / values[0] != offst){
				System.out.println(String.format("Code failed by curve at offst = %d, the curve was %d and angle was %d",
						offst, values[0], values[1]));
			}
		}
	}

}
