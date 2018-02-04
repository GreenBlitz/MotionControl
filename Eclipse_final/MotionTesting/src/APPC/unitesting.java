package APPC;

public class unitesting {
	public static void main(String[] args) {
		ArenaMap map = new ArenaMap(0.1, 10, 10);
		for (;;){
			new PathFactory().genStraightLine(5, 0, 0.005).construct(map);
			System.out.println(map.pointInRange(new Orientation2D(1, 1, 0), 1.1));
		}
	}
}
