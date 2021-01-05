package lsystems.modules;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ParametricParameterModule extends ParametricModule implements PredecessorModule {

	protected final List<String> params;

	public ParametricParameterModule(char name, List<String> params) {
		super(name);
		this.params = params;
	}

	public List<String> getParams() {
		return params;
	}

	@Override
	public int getNumberOfParameters() {
		return params.size();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		return super.equals(o);
	}

	@Override
	public String toString() {
		return String.format("%s(%s)",
				name,
				params.stream().map(Objects::toString).collect(Collectors.joining(",")));
	}
}
