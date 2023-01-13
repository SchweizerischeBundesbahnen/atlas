/*****************************************************************
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 ****************************************************************/

package ch.sbb.atlas.servicepointdirectory.geodata.transformer;

import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Envelope;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@CacheConfig(cacheNames = {"BoundingBoxTransformer"})
/***
 * Convert tile xyz value to bbox of the form `[w, s, e, n]`
 * https://wiki.openstreetmap.org/wiki/Slippy_map_tilenames
 * - `x` {Number} x (longitude) number.
 * - `y` {Number} y (latitude) number.
 * - `zoom` {Number} zoom.
 * - `return` {Array} bbox array of values in form `[w, s, e, n]`.
 */
public class BoundingBoxTransformer {

  /***
   * Convert tile xyz value to bbox of the form [w, s, e, n]
   *
   * @param x (longitude) number.
   * @param y (latitude) number.
   * @param zoom zoom.
   * @return Array bbox array of values in form [w, s, e, n].
   */
  @Cacheable(value = "BoundingBoxTransformer")
  public Envelope calculateBoundingBox(
      final double zoom,
      final int x,
      final int y) {
    return new Envelope(
        tile2lon(x, zoom),
        tile2lon(x + 1, zoom),
        tile2lat(y + 1, zoom),
        tile2lat(y, zoom)
    );
  }

  static double tile2lon(int x, double z) {
    return x / Math.pow(2.0, z) * 360.0 - 180;
  }

  static double tile2lat(int y, double z) {
    double n = Math.PI - (2.0 * Math.PI * y) / Math.pow(2.0, z);
    return Math.toDegrees(Math.atan(Math.sinh(n)));
  }
}
