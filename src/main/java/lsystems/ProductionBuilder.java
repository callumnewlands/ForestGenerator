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

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import lsystems.modules.Module;
import lsystems.modules.PredecessorModule;

public class ProductionBuilder {

	private List<PredecessorModule> leftContext;
	private final List<PredecessorModule> predecessor;
	private List<PredecessorModule> rightContext;
	private final List<Module> successor;
	private Predicate<Map<String, Float>> condition;
	private float probability = 1.0f;

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
