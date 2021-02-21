package modeldata;

import java.util.HashMap;
import java.util.Map;
import rendering.LevelOfDetail;

public class InstancedLODModelBuilder {

	private int numberOfInstances;
	private final Map<LevelOfDetail, Model> models = new HashMap<>();

	public InstancedLODModelBuilder withLowLODModels(Model model) {
		this.models.put(LevelOfDetail.LOW, model);
		return this;
	}

	public InstancedLODModelBuilder withHighLODModels(Model model) {
		this.models.put(LevelOfDetail.HIGH, model);
		return this;
	}

	public InstancedLODModelBuilder withLODModel(LevelOfDetail levelOfDetail, Model model) {
		this.models.put(levelOfDetail, model);
		return this;
	}

	public InstancedLODModelBuilder withNumberOfInstances(int numberOfInstances) {
		this.numberOfInstances = numberOfInstances;
		return this;
	}

	public InstancedLODModel build() {
		return new InstancedLODModel(models, numberOfInstances);
	}

}