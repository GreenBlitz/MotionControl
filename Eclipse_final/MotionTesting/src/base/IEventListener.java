package base;

@FunctionalInterface
public interface IEventListener<T extends Event> {
	void onEvent(T event);
}
