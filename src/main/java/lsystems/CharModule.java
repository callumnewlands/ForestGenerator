package lsystems;

import java.util.Objects;

public class CharModule implements Module {
	private char value;

	public CharModule(char value) {
		this.value = value;
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
