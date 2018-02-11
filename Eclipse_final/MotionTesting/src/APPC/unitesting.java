package APPC;

import base.point.orientation.IOrientation2D;
import base.point.orientation.Orientation2D;

public class unitesting {
	public static void main(String[] args) throws Throwable {
		ArenaMap map = new ArenaMap(0.1, 10, 10);
		IOrientation2D or = null;
		IOrientation2D origin = Orientation2D.immutable(1, 1, 0);
		new PathFactory().genStraightLine(5, 0, 0.005).construct(map);
		for (;;) {
			or = map.lastPointInRange(origin, 1.1);
			Thread.sleep(20);
		}

	}
}
