package APPC;

import base.point.IPoint2D;
import base.point.ImPoint2D;

/**
 * Orientation2D with an index
 *
 */
public class IndexedPoint2D extends ImPoint2D {
	final int index;

	public IndexedPoint2D(IPoint2D point, int ind) {
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
