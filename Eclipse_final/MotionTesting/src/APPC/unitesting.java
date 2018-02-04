package APPC;

public class unitesting {
	public static void main(String[] args) throws Throwable {
		ArenaMap map = new ArenaMap(0.1, 10, 10);
		Orientation2D or2d = null;
		Orientation2D origin = new Orientation2D(1, 1, 0);
		new PathFactory().genStraightLine(5, 0, 0.005).construct(map);
		for (;;){
			for (int i = 0; i < 1000; i++)
				or2d = map.pointInRange(origin, 1.1);
			
			Thread.sleep(1);
		}
		
	}
}
