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

package params;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Random;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.joml.Vector3f;

public final class ParameterLoader {

	private static Parameters parameters;

	public static Parameters loadParameters(String filePath) throws IOException {
		ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
		mapper.registerSubtypes(
				TreeTypes.BranchingTree.class,
				TreeTypes.OakTree.class,
				TreeTypes.MonopodialTree.class,
				TreeTypes.AspenTree.class,
				TreeTypes.PoplarTree.class,
				TreeTypes.PineTree.class
		);
		parameters = mapper.readValue(new File(filePath), Parameters.class);
		setRandomGenerator();
		System.out.println("Parameters loaded from file: " + filePath);
		return parameters;
	}

	public static Parameters getParameters() {
		if (parameters == null) {
			parameters = new Parameters();
			System.out.println("Default parameters used");
			setRandomGenerator();
		}
		return parameters;
	}

	private static void setRandomGenerator() {
		long seed;
		if (parameters.random.seed == -1) {
			Random r = new Random();
			seed = r.nextLong();
		} else {
			seed = parameters.random.seed;
		}
		parameters.random.setGenerator(new Random(seed));
		System.out.println("Random seed: " + seed);
	}

	static class Vec3Deserializer extends JsonDeserializer<Vector3f> {

		@Override
		public Vector3f deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
			List<Number> values = jsonParser.readValueAs((Class<List<Number>>) (Object) List.class);
			return new Vector3f(
					values.get(0).floatValue(),
					values.get(1).floatValue(),
					values.get(2).floatValue());
		}
	}

}
