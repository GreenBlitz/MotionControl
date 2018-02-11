package APPC;

import base.point.IPoint2D;
import base.point.ImPoint2D;
import base.point.orientation.IOrientation2D;
import base.point.orientation.ImOrientation2D;

/**
 * Point2D with an index
 *
 */
public class IndexedOrientation2D extends ImOrientation2D {
	final int index;

	public IndexedOrientation2D(IOrientation2D point, int ind) {
		super(point);
		index = ind;
	}

	public int getIndex() {
		return index;
	}

	public String toString() {
		return "(" + super.toString() + ", " + index + ")";
	}
}
