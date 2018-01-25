package base;

import edu.wpi.first.wpilibj.DigitalSource;
import edu.wpi.first.wpilibj.Encoder;

/**
 * Represents a scaled encoder- it will return a scaled value from construction
 * 1
 */
public class ScaledEncoder extends Encoder implements Input<Double> {
	double scale;

	public ScaledEncoder(final int channelA, final int channelB, boolean reverseDirection, double scale) {
		super(channelA, channelB, reverseDirection);
		setDistancePerPulse(scale);
	}

	public ScaledEncoder(final int channelA, final int channelB, double scale) {
		super(channelA, channelB);
		setDistancePerPulse(scale);
	}

	public ScaledEncoder(final int channelA, final int channelB, boolean reverseDirection,
			final EncodingType encodingType, double scale) {
		super(channelA, channelB, reverseDirection, encodingType);
		setDistancePerPulse(scale);
	}

	public ScaledEncoder(final int channelA, final int channelB, final int indexChannel, boolean reverseDirection,
			double scale) {
		super(channelA, channelB, indexChannel, reverseDirection);
		setDistancePerPulse(scale);
	}

	public ScaledEncoder(final int channelA, final int channelB, final int indexChannel, double scale) {
		super(channelA, channelB, indexChannel);
		setDistancePerPulse(scale);
	}

	public ScaledEncoder(DigitalSource sourceA, DigitalSource sourceB, boolean reverseDirection, double scale) {
		super(sourceA, sourceB, reverseDirection);
		setDistancePerPulse(scale);
	}

	public ScaledEncoder(DigitalSource sourceA, DigitalSource sourceB, double scale) {
		super(sourceA, sourceB);
		setDistancePerPulse(scale);
	}

	public ScaledEncoder(DigitalSource sourceA, DigitalSource sourceB, boolean reverseDirection,
			final EncodingType encodingType, double scale) {
		super(sourceA, sourceB, reverseDirection, encodingType);
		setDistancePerPulse(scale);
	}

	public ScaledEncoder(DigitalSource sourceA, DigitalSource sourceB, DigitalSource indexSource,
			boolean reverseDirection, double scale) {
		super(sourceA, sourceB, indexSource, reverseDirection);
		setDistancePerPulse(scale);
	}

	public ScaledEncoder(DigitalSource sourceA, DigitalSource sourceB, DigitalSource indexSource, double scale) {
		super(sourceA, sourceB, indexSource);
		setDistancePerPulse(scale);
	}

	/**
	 * @see Encoder#pidGet()
	 * @see base.Input#recieve()
	 * @return
	 */
	@Override
	public Double recieve() {
		return pidGet();
	}

	@Override
	/**
	 * @see edu.wpi.first.wpilibj.Encoder#setDistancePerPulse(double)
	 * @param distancePerPulse
	 */
	public void setDistancePerPulse(double distancePerPulse) {
		super.setDistancePerPulse(distancePerPulse);
		scale = distancePerPulse;
	}
}
