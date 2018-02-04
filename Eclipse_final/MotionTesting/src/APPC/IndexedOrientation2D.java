package APPC;

// Orientation2D with an index
public class IndexedOrientation2D extends Orientation2D{
	final int index;
	
	public IndexedOrientation2D(Orientation2D point, int ind){
		super(point);
		index = ind;
	}
	
	public int getIndex(){return index;}
}
