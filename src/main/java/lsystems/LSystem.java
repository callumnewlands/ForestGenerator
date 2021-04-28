/*
 * Copyright (c) 2021 Callum Newlands
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     The additional term of 7.b applies: Requiring preservation of specified
 *     reasonable legal notices or author attributions in that material or in the
 *     Appropriate Legal Notices displayed by works containing it
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package lsystems;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lsystems.modules.AxiomaticModule;
import lsystems.modules.Module;
import org.apache.commons.math3.distribution.EnumeratedDistribution;
import org.apache.commons.math3.util.Pair;
import params.ParameterLoader;

public class LSystem {

	private List<? extends Module> state;
	private final List<AxiomaticModule> axiom;
	private final List<Module> ignored;
	private final List<Production> productions;
	private final int longestPred;

	public LSystem(List<AxiomaticModule> axiom, List<Module> ignored, List<Production> productions) {
		this.axiom = axiom;
		this.ignored = ignored;
		this.state = this.axiom;
		this.productions = productions;
		this.longestPred = this.productions.stream().mapToInt(Production::getPredLength).max().orElse(1);
	}

	private List<Production> getAllWhichMatch(List<Module> prev, List<Module> pred, List<Module> remaining) {
		return this.productions
				.stream()
				.filter(p -> p.predecessorSatisfied(pred))
				.filter(p -> p.conditionSatisfied(pred))
				.filter(p -> p.contextSatisfied(prev, remaining, ignored))
				.collect(Collectors.toList());
	}

	public String performDerivationStep() {
		int head = 0;
		List<Module> result = new ArrayList<>();
		while (head < this.state.size()) {
			// Maximal length matching
			for (int len = this.longestPred; len > 0; len--) {
				List<Module> state = this.getState();
				List<Module> previous = state.subList(0, head);
				List<Module> current = state.subList(head, head + len);
				List<Module> remaining = state.subList(head + len, state.size());
				List<Production> matches = getAllWhichMatch(previous, current, remaining);
				Production production = null;
				if (matches.size() == 1) {
					production = matches.get(0);
				} else if (matches.size() > 1) {
					production = chooseStochasticProduction(matches, current);
				}
				if (matches.size() > 0) {
					result.addAll(production.apply(current));
					head += len;
					break;
				}
				if (len == 1) {
					result.add(this.state.get(head));
					head++;
				}
			}
		}
		this.state = result;
		return this.getStateSting();
	}

	public List<Module> performDerivations(int n) {
		for (int i = 0; i < n; i++) {
			performDerivationStep();
		}
		return getState();
	}

	private Production chooseStochasticProduction(List<Production> matches, List<Module> pred) {
		if (matches.stream().anyMatch(p -> p.getProbability() == null)) {
			throw new RuntimeException(String.format(
					"Ambiguous productions (without probabilities) for predecessor %s in string %s",
					pred.toString(), getStateSting()));
		}
		Float probSum = matches.stream().map(Production::getProbability).reduce(0f, Float::sum);
		if (probSum - 1 > 0.000001) {
			throw new RuntimeException(String.format(
					"Probabilities for productions on %s in string %s do not sum to 1. Actual sum: %f",
					pred.toString(), getStateSting(), probSum));
		}
		EnumeratedDistribution<Production> dist = new EnumeratedDistribution<>(
				matches.stream().map(p -> Pair.create(p, (double) p.getProbability())).collect(Collectors.toList()));

		dist.reseedRandomGenerator(ParameterLoader.getParameters().random.generator.nextLong());
		return dist.sample();
	}

	public List<Module> getState() {
		return (List<Module>) this.state;
	}

	public String getStateSting() {
		return this.state.stream().map(Object::toString).collect(Collectors.joining());
	}

}
