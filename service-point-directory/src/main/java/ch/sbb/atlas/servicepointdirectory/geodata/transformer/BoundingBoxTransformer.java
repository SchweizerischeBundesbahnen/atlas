package ch.sbb.atlas.servicepointdirectory.geodata.transformer;

import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Envelope;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@CacheConfig(cacheNames = {"BoundingBoxTransformer"})
/**
 * Convert tile xyz value to bbox of the form `[w, s, e, n]`
 * https://wiki.openstreetmap.org/wiki/Slippy_map_tilenames
 * - `x` {Number} x (longitude) number.
 * - `y` {Number} y (latitude) number.
 * - `zoom` {Number} zoom.
 * - `return` {Array} bbox array of values in form `[w, s, e, n]`.
 */
public class BoundingBoxTransformer {

  public static final double CIRCLE_360_DEGREES = 360.0;
  public static final int CIRCLE_180_DEGREES = 180;

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
    return x / Math.pow(2.0, z) * CIRCLE_360_DEGREES - CIRCLE_180_DEGREES;
  }

  static double tile2lat(int y, double z) {
    double n = Math.PI - (2.0 * Math.PI * y) / Math.pow(2.0, z);
    return Math.toDegrees(Math.atan(Math.sinh(n)));
  }
}
