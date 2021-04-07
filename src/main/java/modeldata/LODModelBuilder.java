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