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

import lombok.Getter;
import lombok.Setter;
import org.joml.Vector2f;
import org.joml.Vector3f;

@Getter
@Setter
public class Vertex {
	private Vector3f position;
	private Vector3f normal;
	private Vector3f tangent;
	private Vector2f texCoord;

	public Vertex(Vector3f position) {
		this.position = position;
	}

	public Vertex(Vector3f position, Vector3f normal) {
		this.position = position;
		this.normal = normal;
	}

	public Vertex(Vector3f position, Vector2f texCoord) {
		this.position = position;
		this.texCoord = texCoord;
	}

	public Vertex(Vector3f position, Vector3f normal, Vector2f texCoord) {
		this.position = position;
		this.normal = normal;
		this.texCoord = texCoord;
	}

	public Vertex(Vector3f position, Vector3f normal, Vector3f tangent, Vector2f texCoord) {
		this.position = position;
		this.normal = normal;
		this.tangent = tangent;
		this.texCoord = texCoord;
	}
}
