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
		parameters = mapper.readValue(new File(filePath), Parameters.class);
		setRandomGenerator();
		return parameters;
	}

	public static Parameters getParameters() {
		if (parameters == null) {
			parameters = new Parameters();
			setRandomGenerator();
		}
		return parameters;
	}

	private static void setRandomGenerator() {
		if (parameters.random.seed == -1) {
			parameters.random.setGenerator(new Random());
		} else {
			parameters.random.setGenerator(new Random(parameters.random.seed));
		}
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
