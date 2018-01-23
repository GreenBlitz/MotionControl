package APPC;

public class APPDriveData {
	public double power;
	public double curve;
	
	public APPDriveData(double power, double curve) {
		this.power = power;
		this.curve = curve;
	}

	@Override
	public String toString() {
		return "[power=" + power + ", curve=" + curve + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(curve);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(power);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		APPDriveData other = (APPDriveData) obj;
		if (Double.doubleToLongBits(curve) != Double.doubleToLongBits(other.curve))
			return false;
		if (Double.doubleToLongBits(power) != Double.doubleToLongBits(other.power))
			return false;
		return true;
	}
	
	
}
