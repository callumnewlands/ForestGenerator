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
			throw new RuntimeException("Parameter value not provided for (at least) one of: " + this.params.toString() +
					" in " + this.toString());
		}
		return new ParametricValueModule(name, expression.apply(params));
	}
}
