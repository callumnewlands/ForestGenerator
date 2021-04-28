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

package modeldata;

import java.util.HashMap;
import java.util.Map;
import rendering.LevelOfDetail;

public class LODModelBuilder {

	private final Map<LevelOfDetail, Model> models = new HashMap<>();

	public LODModelBuilder withLowLODModels(Model model) {
		this.models.put(LevelOfDetail.LOW, model);
		return this;
	}

	public LODModelBuilder withHighLODModels(Model model) {
		this.models.put(LevelOfDetail.HIGH, model);
		return this;
	}

	public LODModelBuilder withLODModel(LevelOfDetail levelOfDetail, Model model) {
		this.models.put(levelOfDetail, model);
		return this;
	}

	public LODModel build() {
		return new LODModel(models);
	}

}