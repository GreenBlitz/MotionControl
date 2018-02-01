package base;

/**
 * Represents a generalized controller
 */

public interface IController {
	/**
	 * Starts the controller.
	 */
    void start();

    /**
     * Temporally stops the controller. The controller should be able to return to action
     * after this was called.
     */
    void stop();
    
    /**
     * Completely stops the controller. Calling {@link IController#start start} after
     * this was called raises an undefined behavior 
     */
    void end();
}
