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

package modeldata.meshdata;

import java.util.List;
import lombok.Getter;

public class VertexAttribute {

	@Getter
	private final String name;
	@Getter
	private final int numberOfFloatComponents;
	@Getter
	private final int location;
	@Getter
	private final int divisor;

	public static final VertexAttribute POSITION = new VertexAttribute(0, "position", 3);
	public static final VertexAttribute NORMAL = new VertexAttribute(1, "normal", 3);
	public static final VertexAttribute TEXTURE = new VertexAttribute(2, "texCoord", 2);
	public static final List<VertexAttribute> INSTANCE_MODEL = List.of(
			new VertexAttribute(3, "instanceModel", 4, 1),
			new VertexAttribute(4, "instanceModel", 4, 1),
			new VertexAttribute(5, "instanceModel", 4, 1),
			new VertexAttribute(6, "instanceModel", 4, 1)
	);
	public static final VertexAttribute TANGENT = new VertexAttribute(7, "tangent", 3);

	public VertexAttribute(final int location, final String name, final int numberOfFloatComponents) {
		this(location, name, numberOfFloatComponents, 0);
	}

	public VertexAttribute(final int location, final String name, final int numberOfFloatComponents, final int divisor) {
		this.location = location;
		this.name = name;
		this.numberOfFloatComponents = numberOfFloatComponents;
		this.divisor = divisor;
	}
}