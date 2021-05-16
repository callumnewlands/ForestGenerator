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

package utils;

import org.joml.Vector2f;

public final class MathsUtils {

	private MathsUtils() {
	}

	/**
	 * Linear interpolation between x0 and x1 with factor p (in [0, 1])
	 */
	public static float lerp(float x0, float x1, float p) {
		return x0 + p * (x1 - x0);
	}


	public static boolean circlesColliding(Vector2f centre1, float radius1, Vector2f centre2, float radius2) {
		float distanceSquared = centre1.distanceSquared(centre2);
		float radiusSum = radius1 + radius2;
		return (distanceSquared < (radiusSum * radiusSum));
	}

	public static boolean cylindersColliding(Vector2f centre1, float centre1Y, float radius1, float height1,
											 Vector2f centre2, float centre2Y, float radius2, float height2) {
		float distanceSquared = centre1.distanceSquared(centre2);
		float radiusSum = radius1 + radius2;
		if (distanceSquared < (radiusSum * radiusSum)) {
			float heightDist = Math.abs(centre2Y - centre1Y);
			float ySum = height1 + height2;
			return heightDist < ySum;
		}
		return false;
	}
}
