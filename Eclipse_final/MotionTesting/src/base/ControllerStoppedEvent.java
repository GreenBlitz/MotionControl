package base;

public final class ControllerStoppedEvent<I,O> extends Event {
	
	private final Controller<I,O> m_controller;
	
	public ControllerStoppedEvent(Controller<I,O> controller){
		super();
		m_controller = controller;
		setCancelable(false);
	}
	
	public static final <I,O> ControllerStoppedEvent<I, O> of(Controller<I, O> controller){
		return new ControllerStoppedEvent<>(controller);
	}
	
	public Controller<I, O> getController(){
		return m_controller;
	}
	
	
}
