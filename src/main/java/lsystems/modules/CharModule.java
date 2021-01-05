package lsystems.modules;

import java.util.Objects;

public class CharModule implements PredecessorModule, AxiomaticModule {

	private final char value;

	public CharModule(char value) {
		this.value = value;
	}

	@Override
	public int getNumberOfParameters() {
		return 0;
	}

	@Override
	public char getName() {
		return value;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		CharModule that = (CharModule) o;
		return Objects.equals(value, that.value);
	}

	@Override
	public String toString() {
		return Character.toString(value);
	}
}
