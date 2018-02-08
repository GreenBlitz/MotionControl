package base.point;

public interface ILocation {
	double getX();
	
	double getY();
	
	/**
	 * 
	 * @param other
	 * @return distance between this and other
	 */
	default double distance(ILocation other) {
		return Math.hypot(getX() - other.getX(), getY() - other.getY());
	}
	
	/**
	 * 
	 * @param other
	 * @return distance <i>squared</i> between this to other
	 */
	default double distanceSquared(ILocation other) {
		return (getX() - other.getX()) * (getX() - other.getX()) + (getY() - other.getY()) * (getY() - other.getY()); 
	}
}
