package gbmotion.events;

public class Event {
	
	
	private boolean m_isCanceled = false;
	private boolean m_isCancelable = true;
	private long m_timestamp;
	
	
	public Event(){
		m_timestamp = System.currentTimeMillis();
	}
	
	public boolean isCancelable(){
		return m_isCancelable;
	}
	
	public boolean isCanceled(){
		return m_isCanceled;
	}
	
	public void setCanceled(boolean canceled){
		if (m_isCancelable)
			m_isCanceled = canceled;
	}
	
	protected final void setCancelable(boolean cancelable){
		m_isCancelable = cancelable;
	}
	
	public long getTimestamp(){
		return m_timestamp;
	}
	
	
}
