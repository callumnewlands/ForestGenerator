package lsystems;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.math3.distribution.EnumeratedDistribution;
import org.apache.commons.math3.util.Pair;

public class LSystem {

	private List<Module> state;
	private final List<Module> axiom;
	private final List<Module> ignored;
	private final List<Production> productions;
	private final int longestPred;

	public LSystem(List<Module> axiom, List<Module> ignored, List<Production> productions) {
		this.axiom = axiom;
		this.ignored = ignored;
		this.state = this.axiom;
		this.productions = productions;
		this.longestPred = this.productions.stream().mapToInt(Production::getPredLength).max().orElse(1);
	}

	private List<Production> getAllWhichMatch(List<Module> pred) {
		return this.productions.stream().filter(p -> p.matchesPred(pred)).collect(Collectors.toList());
	}

	private boolean isIgnored(Module module) {
		return this.ignored.stream().anyMatch(i -> i.equals(module));
	}

	public String performDerivationStep() {
		int head = 0;
		List<Module> result = new ArrayList<>();
		while (head < this.state.size()) {
			Module current = this.state.get(head);
			if (isIgnored(current)) {
				result.add(current);
				head++;
				continue;
			}
			// Maximal length matching
			for (int len = this.longestPred; len > 0; len--) {
				List<Module> pred = this.state.subList(head, head + len);
				List<Production> matches = getAllWhichMatch(pred);
				Production production = null;
				if (matches.size() == 1) {
					production = matches.get(0);
				} else if (matches.size() > 1) {
					production = chooseStochasticProduction(matches, pred);
				}
				if (matches.size() > 0) {
					result.addAll(production.apply(pred));
					head += len;
					break;
				}
				if (len == 1) {
					throw new RuntimeException(
							String.format("Cannot derive from string: %s position: %d(%s)",
									this.getStateSting(), head, pred.toString()));
				}
			}
		}
		this.state = result;
		return this.getStateSting();
	}

	private Production chooseStochasticProduction(List<Production> matches, List<Module> pred) {
		if (matches.stream().anyMatch(p -> p.getProbability() == null)) {
			throw new RuntimeException(String.format(
					"Ambiguous productions (without probabilities) for predecessor %s in string %s",
					pred.toString(), getStateSting()));
		}
		Float probSum = matches.stream().map(Production::getProbability).reduce(0f, Float::sum);
		if (probSum != 1) {
			throw new RuntimeException(String.format(
					"Probabilities for productions on %s in string %s do not sum to 1. Actual sum: %f",
					pred.toString(), getStateSting(), probSum));
		}
		EnumeratedDistribution<Production> dist = new EnumeratedDistribution<>(
				matches.stream().map(p -> Pair.create(p, (double) p.getProbability())).collect(Collectors.toList()));
		return dist.sample();
	}

	public String getStateSting() {
		return this.state.stream().map(Object::toString).collect(Collectors.joining());
	}
}
