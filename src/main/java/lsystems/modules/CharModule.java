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

import java.util.Objects;

public class CharModule implements PredecessorModule, AxiomaticModule {

	private final char value;

	public CharModule(char value) {
		this.value = value;
	}

	@Override
	public int getNumberOfParameters() {
		return 0;
	}

	@Override
	public char getName() {
		return value;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		CharModule that = (CharModule) o;
		return Objects.equals(value, that.value);
	}

	@Override
	public String toString() {
		return Character.toString(value);
	}
}
