package base;

import edu.wpi.first.wpilibj.DigitalSource;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.PIDSourceType;
import edu.wpi.first.wpilibj.tables.ITable;
import wrapper.PIDSourceWrapper;

/**
 *
 * Wraps the methods of Encoder
 *
 * Does everything the same as the regular encoder class except the constructor.
1 */
public class WrappedEncoder extends PIDSourceWrapper {
    private Encoder m_encoder;

    public WrappedEncoder(Encoder enc, double distancePerTick) {
        super(enc);
        m_encoder = enc;
        m_encoder.setDistancePerPulse(distancePerTick);
    }

    /**
     * @return The Encoder's FPGA index.
     */
    public int getFPGAIndex() {
        return m_encoder.getFPGAIndex();
    }

    /**
     * Used to divide raw edge counts down to spec'd counts.
     *
     * @return The encoding scale factor 1x, 2x, or 4x, per the requested encoding type.
     */
    public int getEncodingScale() {
        return m_encoder.getEncodingScale();
    }

    /**
     * Free the resources used by this object.
     */
    public void free() {
        m_encoder.free();
    }

    /**
     * Gets the raw value from the encoder. The raw value is the actual count unscaled by the 1x, 2x,
     * or 4x scale factor.
     *
     * @return Current raw count from the encoder
     */
    public int getRaw() {
        return m_encoder.getRaw();
    }

    /**
     * Gets the current count. Returns the current count on the Encoder. This method compensates for
     * the decoding type.
     *
     * @return Current count from the Encoder adjusted for the 1x, 2x, or 4x scale factor.
     */
    public int get() {
        return m_encoder.get();
    }

    /**
     * Reset the Encoder distance to zero. Resets the current count to zero on the encoder.
     */
    public void reset() {
        m_encoder.reset();
    }

    /**
     * Sets the maximum period for stopped detection. Sets the value that represents the maximum
     * period of the Encoder before it will assume that the attached device is stopped. This timeout
     * allows users to determine if the wheels or other shaft has stopped rotating. This method
     * compensates for the decoding type.
     *
     * @param maxPeriod The maximum time between rising and falling edges before the FPGA will report
     *                  the device stopped. This is expressed in seconds.
     */
    public void setMaxPeriod(double maxPeriod) {
        m_encoder.setMaxPeriod(maxPeriod);
    }

    /**
     * Determine if the encoder is stopped. Using the MaxPeriod value, a boolean is returned that is
     * true if the encoder is considered stopped and false if it is still moving. A stopped encoder is
     * one where the most recent pulse width exceeds the MaxPeriod.
     *
     * @return True if the encoder is considered stopped.
     */
    public boolean getStopped() {
        return m_encoder.getStopped();
    }

    /**
     * The last direction the encoder value changed.
     *
     * @return The last direction the encoder value changed.
     */
    public boolean getDirection() {
        return m_encoder.getDirection();
    }

    /**
     * Get the distance the robot has driven since the last reset as scaled by the value from {@link
     * #setDistancePerPulse(double)}.
     *
     * @return The distance driven since the last reset
     */
    public double getDistance() {
        return m_encoder.getDistance();
    }

    /**
     * Get the current rate of the encoder. Units are distance per second as scaled by the value from
     * setDistancePerPulse().
     *
     * @return The current rate of the encoder.
     */
    public double getRate() {
        return m_encoder.getRate();
    }

    /**
     * Set the minimum rate of the device before the hardware reports it stopped.
     *
     * @param minRate The minimum rate. The units are in distance per second as scaled by the value
     *                from setDistancePerPulse().
     */
    public void setMinRate(double minRate) {
        m_encoder.setMinRate(minRate);
    }

    /**
     * Set the distance per pulse for this encoder. This sets the multiplier used to determine the
     * distance driven based on the count value from the encoder. Do not include the decoding type in
     * this scale. The library already compensates for the decoding type. Set this value based on the
     * encoder's rated Pulses per Revolution and factor in gearing reductions following the encoder
     * shaft. This distance can be in any units you like, linear or angular.
     *
     * @param distancePerPulse The scale factor that will be used to convert pulses to useful units.
     */
    public void setDistancePerPulse(double distancePerPulse) {
        m_encoder.setDistancePerPulse(distancePerPulse);
    }

    /**
     * Set the direction sensing for this encoder. This sets the direction sensing on the encoder so
     * that it could count in the correct software direction regardless of the mounting.
     *
     * @param reverseDirection true if the encoder direction should be reversed
     */
    public void setReverseDirection(boolean reverseDirection) {
        m_encoder.setReverseDirection(reverseDirection);
    }

    /**
     * Set the Samples to Average which specifies the number of samples of the timer to average when
     * calculating the period. Perform averaging to account for mechanical imperfections or as
     * oversampling to increase resolution.
     *
     * @param samplesToAverage The number of samples to average from 1 to 127.
     */
    public void setSamplesToAverage(int samplesToAverage) {
        m_encoder.setSamplesToAverage(samplesToAverage);
    }

    /**
     * Get the Samples to Average which specifies the number of samples of the timer to average when
     * calculating the period. Perform averaging to account for mechanical imperfections or as
     * oversampling to increase resolution.
     *
     * @return SamplesToAverage The number of samples being averaged (from 1 to 127)
     */
    public int getSamplesToAverage() {
        return m_encoder.getSamplesToAverage();
    }

    /**
     * Set which parameter of the encoder you are using as a process control variable. The encoder
     * class supports the rate and distance parameters.
     *
     * @param pidSource An enum to select the parameter.
     */
    public void setPIDSourceType(PIDSourceType pidSource) {
        m_encoder.setPIDSourceType(pidSource);
    }

    public PIDSourceType getPIDSourceType() {
        return m_encoder.getPIDSourceType();
    }

    /**
     * Implement the PIDSource interface.
     *
     * @return The current value of the selected source parameter.
     */
    public double pidGet() {
        return m_encoder.pidGet();
    }

    /**
     * Set the index source for the encoder. When this source is activated, the encoder count
     * automatically resets.
     *
     * @param channel A DIO channel to set as the encoder index
     */
    public void setIndexSource(int channel) {
        m_encoder.setIndexSource(channel);
    }

    /**
     * Set the index source for the encoder. When this source is activated, the encoder count
     * automatically resets.
     *
     * @param source A digital source to set as the encoder index
     */
    public void setIndexSource(DigitalSource source) {
        m_encoder.setIndexSource(source);
    }

    /**
     * Set the index source for the encoder. When this source rises, the encoder count automatically
     * resets.
     *  @param channel A DIO channel to set as the encoder index
     * @param type    The state that will cause the encoder to reset
     */
    public void setIndexSource(int channel, Encoder.IndexingType type) {
        m_encoder.setIndexSource(channel, type);
    }

    /**
     * Set the index source for the encoder. When this source rises, the encoder count automatically
     * resets.
     *  @param source A digital source to set as the encoder index
     * @param type   The state that will cause the encoder to reset
     */
    public void setIndexSource(DigitalSource source, Encoder.IndexingType type) {
        m_encoder.setIndexSource(source, type);
    }

    /**
     * Live Window code, only does anything if live window is activated.
     */
    public String getSmartDashboardType() {
        return m_encoder.getSmartDashboardType();
    }

    public void initTable(ITable subtable) {
        m_encoder.initTable(subtable);
    }

    public ITable getTable() {
        return m_encoder.getTable();
    }

    public void updateTable() {
        m_encoder.updateTable();
    }

    public void startLiveWindowMode() {
        m_encoder.startLiveWindowMode();
    }

    public void stopLiveWindowMode() {
        m_encoder.stopLiveWindowMode();
    }

    @Override
    public String toString() {
        return "WrappedEncoder{" +
                "m_encoder=" + m_encoder +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof WrappedEncoder)) return false;
        if (!super.equals(o)) return false;

        WrappedEncoder that = (WrappedEncoder) o;

        return m_encoder.equals(that.m_encoder);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + m_encoder.hashCode();
        return result;
    }
}
