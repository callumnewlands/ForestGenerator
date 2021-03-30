package lsystems;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import lsystems.modules.Module;
import lsystems.modules.ParametricExpressionModule;
import lsystems.modules.ParametricParameterModule;
import lsystems.modules.ParametricValueModule;
import lsystems.modules.PredecessorModule;

public class Production {

	private final List<PredecessorModule> leftContext;
	private final List<PredecessorModule> predecessor;
	private final List<PredecessorModule> rightContext;
	private final Predicate<Map<String, Float>> condition;
	private final List<Module> successor;
	private final float probability;

	public Production(List<PredecessorModule> leftContext,
					  List<PredecessorModule> predecessor,
					  List<PredecessorModule> rightContext,
					  Predicate<Map<String, Float>> condition,
					  List<Module> successor,
					  Float probability) {
		this.leftContext = leftContext;
		this.predecessor = predecessor;
		this.rightContext = rightContext;
		this.condition = condition;
		this.successor = successor;
		this.probability = probability;
	}

	int getPredLength() {
		return predecessor.size();
	}

	public boolean predecessorSatisfied(List<Module> pred) {
		return predecessor.equals(pred);
	}

	public boolean conditionSatisfied(List<Module> pred) {
		if (this.condition == null) {
			return true;
		}
		return condition.test(getParamsFromModules(pred));
	}

	public boolean contextSatisfied(List<Module> prev, List<Module> remaining, List<Module> ignored) {
		return leftContextSatisfied(prev, ignored) && rightContextSatisfied(remaining, ignored);
	}

	private boolean sideContextSatisfied(List<Module> actual, List<PredecessorModule> expected, List<Module> ignored) {
		int contextLength = expected.size();
		List<Module> actualContext = actual
				.stream()
				.filter(m -> !ignored.contains(m))
				.collect(Collectors.toList());
		actualContext = actualContext.subList(actualContext.size() - contextLength, actualContext.size());
		return expected.equals(actualContext);
	}

	private boolean leftContextSatisfied(List<Module> prev, List<Module> ignored) {
		if (leftContext == null) {
			return true;
		}
		return sideContextSatisfied(prev, leftContext, ignored);
	}

	private boolean rightContextSatisfied(List<Module> remaining, List<Module> ignored) {
		if (rightContext == null) {
			return true;
		}
		return sideContextSatisfied(remaining, rightContext, ignored);
	}

	// Given the modules in "currentPred" match this production, what are the values of the params?
	private Map<String, Float> getParamsFromModules(List<Module> currentPred) {
		Map<String, Float> params = new HashMap<>();
		for (int i = 0; i < this.predecessor.size(); i++) {
			if (!(this.predecessor.get(i) instanceof ParametricParameterModule)) {
				continue;
			}
			if (!(currentPred.get(i) instanceof ParametricValueModule)) {
				throw new RuntimeException(String.format("Modules: %s and %s are not the same shape.",
						this.predecessor.get(i).toString(), currentPred.get(i).toString()));
			}
			List<String> vars = ((ParametricParameterModule) this.predecessor.get(i)).getParams();
			List<Float> values = ((ParametricValueModule) currentPred.get(i)).getValues();
			if (vars.size() != values.size()) {
				throw new RuntimeException(String.format("Modules: %s and %s are not the same shape.",
						this.predecessor.get(i).toString(), currentPred.get(i).toString()));
			}
			for (int j = 0; j < vars.size(); j++) {
				String var = vars.get(j);
				Float val = values.get(j);
				if (params.containsKey(var)) {
					throw new RuntimeException(
							String.format("Conflicting values for param %s in modules: %s with values %s",
									var, this.predecessor, currentPred));
				}
				params.put(var, val);
			}
		}
		return params;
	}

	public List<Module> apply(List<Module> currentPred) {
		Map<String, Float> params = getParamsFromModules(currentPred);
		if (params == null || params.size() == 0) {
			return successor; // Non-parametric
		}
		return successor.stream().map(m -> {
			if (m instanceof ParametricExpressionModule) {
				return ((ParametricExpressionModule) m).evaluate(params);
			}
			return m;
		}).collect(Collectors.toList());

	}

	public Float getProbability() {
		return probability;
	}
}
