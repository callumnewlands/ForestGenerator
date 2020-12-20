package lsystems;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ParametricValueModule extends ParametricModule implements AxiomaticModule {

	private final List<Float> values;

	public ParametricValueModule(char name, Float value) {
		this(name, List.of(value));
	}

	public ParametricValueModule(char name, List<Float> values) {
		super(name);
		this.values = values;
	}

	public List<Float> getValues() {
		return values;
	}

	@Override
	int getNumberOfParameters() {
		return values.size();
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
				values.stream().map(Objects::toString).collect(Collectors.joining(",")));
	}
}
