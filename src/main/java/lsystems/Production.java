package lsystems;

import java.util.List;
import java.util.function.Predicate;

public class Production {

	private List<Module> leftContext;
	private List<Module> predecessor;
	private List<Module> rightContext;
	private Predicate<Float> condition;
	private List<Module> successor;
	private Float probability;

	public Production(List<Module> predecessor, List<Module> successor) {
		this.predecessor = predecessor;
		this.successor = successor;
	}

	int getPredLength() {
		return predecessor.size();
	}

	// TODO consider context (and condition)
	public boolean matchesPred(List<Module> pred) {
		return predecessor.equals(pred);
	}

	// TODO consider variables and things?
	public List<Module> apply(List<Module> pred) {
		return successor;
	}

	public Float getProbability() {
		return probability;
	}
}
