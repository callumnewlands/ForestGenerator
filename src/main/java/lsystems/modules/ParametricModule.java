package lsystems.modules;

import java.util.Objects;

public abstract class ParametricModule implements Module {
	protected char name;

	public ParametricModule(char name) {
		this.name = name;
	}

	abstract int getNumberOfParameters();

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof ParametricModule)) {
			return false;
		}
		ParametricModule that = (ParametricModule) o;
		return Objects.equals(name, that.name) && getNumberOfParameters() == that.getNumberOfParameters();
	}

	@Override
	public int hashCode() {
		return Objects.hash(name);
	}
}
