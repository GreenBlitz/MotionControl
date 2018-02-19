package gbmotion.events;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
@SuppressWarnings({"unchecked", "cast", "rawtypes"})
public class EventManager {
	private static final Map<Class<? extends Event>, List<IEventListener>> allListeners = new HashMap<>();
	
	public static <T extends Event> void registerListener(IEventListener<T> listener, Class<? extends T> eventClass){
		if (!allListeners.containsKey(eventClass)){
			allListeners.put(eventClass, new LinkedList<IEventListener>());
		}
		
		allListeners.get(eventClass).add(listener);
		
	}
	
	public static void registerMethodAsListener(Method method){
		Class<? extends Event> eventClass = (Class<? extends Event>) method.getParameterTypes()[0];
		IEventListener<Event> listener = (Event e) -> { 
			try {
					method.invoke(null, eventClass.cast(e));
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e_) {
					e_.printStackTrace();
				}
		};
			
		
		
		registerListener(listener, eventClass);
	}
	
	public static void registerClass(Class<?> cls){
		for (Method method : cls.getDeclaredMethods()){
			if (method.getAnnotationsByType(EventListener.class).length != 0){
				registerMethodAsListener(method);
			}
		}
	}
	
	
	public static void fireEvent(Event e){
		List<IEventListener> lst = allListeners.get(e.getClass());
		if (lst != null){
			lst.forEach( ls -> ls.onEvent(e));
		}
	}
	
}
