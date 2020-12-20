package lsystems;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class ProductionBuilder {

	private List<PredecessorModule> leftContext;
	private final List<PredecessorModule> predecessor;
	private List<PredecessorModule> rightContext;
	private final List<Module> successor;
	private Predicate<Map<String, Float>> condition;
	private Float probability;

	public ProductionBuilder(List<PredecessorModule> predecessor, List<Module> successor) {
		this.successor = successor;
		this.predecessor = predecessor;
	}

	public ProductionBuilder withLeftContext(List<PredecessorModule> leftContext) {
		this.leftContext = leftContext;
		return this;
	}

	public ProductionBuilder withRightContext(List<PredecessorModule> rightContext) {
		this.rightContext = rightContext;
		return this;
	}

	public ProductionBuilder withCondition(Predicate<Map<String, Float>> condition) {
		this.condition = condition;
		return this;
	}

	public ProductionBuilder withProbability(Float probability) {
		this.probability = probability;
		return this;
	}

	public Production build() {
		return new Production(leftContext, predecessor, rightContext, condition, successor, probability);
	}
}
