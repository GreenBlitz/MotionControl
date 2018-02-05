package APPC;

public class unitesting {
	public static void main(String[] args) throws Throwable {
		ArenaMap map = new ArenaMap(0.1, 10, 10);
		Orientation2D or = null;
		Orientation2D origin = new Orientation2D(1, 1, 0);
		new PathFactory().genStraightLine(5, 0, 0.005).construct(map);
		for (;;) {
			or = map.lastPointInRange(origin, 1.1);
			Thread.sleep(1);
		}

	}
}
