package events;

import base.AbstractController;

public final class ControllerStoppedEvent<I,O> extends Event {
	
	private final AbstractController<I,O> m_controller;
	
	public ControllerStoppedEvent(AbstractController<I,O> controller){
		m_controller = controller;
		setCancelable(false);
	}
	
	public static final <I,O> ControllerStoppedEvent<I, O> of(AbstractController<I, O> controller){
		return new ControllerStoppedEvent<>(controller);
	}
	
	public AbstractController<I, O> getController(){
		return m_controller;
	}
	
	
}
