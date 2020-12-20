package lsystems.modules;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ParametricExpressionModule extends ParametricParameterModule {

	private Function<Map<String, Float>, List<Float>> expression;

	public ParametricExpressionModule(char name, List<String> params, Function<Map<String, Float>, List<Float>> expression) {
		super(name, params);
		this.expression = expression;
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
		return String.format("%s(Function<%s>)",
				name,
				params.stream().map(Objects::toString).collect(Collectors.joining(",")));
	}

	public ParametricValueModule evaluate(Map<String, Float> params) {
		if (!this.params.stream().allMatch(params::containsKey)) {
			throw new RuntimeException("Parameter value not provided for (at least) one of: " + this.params.toString());
		}
		return new ParametricValueModule(name, expression.apply(params));
	}
}
