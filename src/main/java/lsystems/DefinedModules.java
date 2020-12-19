package lsystems;

public final class DefinedModules {
	private DefinedModules() {
	}

	public static final Module LB = new CharModule('[');
	public static final Module F = new CharModule('F');
	public static final Module X = new CharModule('X');
	public static final Module PL = new CharModule('+');
	public static final Module MI = new CharModule('-');
	public static final Module RB = new CharModule(']');
}
