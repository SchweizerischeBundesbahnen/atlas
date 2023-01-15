/*****************************************************************
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 * <p>
 *    http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 ****************************************************************/
package ch.sbb.atlas.servicepointdirectory.geodata.transformer;

import ch.sbb.atlas.servicepointdirectory.geodata.protobuf.VectorTile;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;

public class VectorTileEncoder {

  private final Map<String, Layer> layers = new LinkedHashMap<>();

  private final int extent;

  protected final Envelope clipArea;

  private Envelope tileEnvelope;

  /**
   * Create a {@link VectorTileEncoder} with the default extent of 4096 and clip buffer 0.
   */
  public VectorTileEncoder() {
    this(4096, 0);
  }

  /**
   * Create a {@link VectorTileEncoder} with the given extent value.
   * <p>
   * The extent value control how detailed the coordinates are encoded in the
   * vector tile. 4096 is a good default, 256 can be used to reduce density.
   * <p>
   * The clip buffer value control how large the clipping area is outside the
   * tile for geometries. 0 means that the clipping is done at the tile border. 8
   * is a good default.
   *
   * @param extent     an int with extent value. 4096 is a good value.
   * @param clipBuffer an int with clip buffer size for geometries. 8 is a
   *                   good value.
   */
  public VectorTileEncoder(int extent, int clipBuffer) {
    this.extent = extent;
    clipArea = createTileEnvelope(clipBuffer, extent);
  }

  private static Envelope createTileEnvelope(int buffer, int size) {
    Coordinate[] coords = new Coordinate[5];
    coords[0] = new Coordinate(-buffer, size + buffer);
    coords[1] = new Coordinate(size + buffer, size + buffer);
    coords[2] = new Coordinate(size + buffer, -buffer);
    coords[3] = new Coordinate(-buffer, -buffer);
    coords[4] = coords[0];
    return new GeometryFactory().createPolygon(coords).getEnvelopeInternal();
  }

  private double scaleValue(double unscaledNum, double maxAllowed, double min, double max) {
    return (maxAllowed) * (unscaledNum - min) / (max - min);
  }

  private void scaleGeometryToTileEnvelope(Point geometry) {
    if (geometry.isEmpty() || geometry.getSRID() == 0) {
      return;
    } else if (geometry.getSRID() != 3857) {
      throw new IllegalArgumentException("Geometry SRID must be WGS84WebMercator");
    }

    Arrays.stream(geometry.getCoordinates()).forEach(coord -> {
      final int scaledX = (int) Math.round(
          scaleValue(coord.getX(), extent, tileEnvelope.getMinX(), tileEnvelope.getMaxX()));
      // In the tile coordinate system, Y axis is positive down.
      // However, in geographic coordinate system, Y axis is positive up.
      final int scaledY = extent - (int) Math.round(
          scaleValue(coord.getY(), extent, tileEnvelope.getMinY(), tileEnvelope.getMaxY()));
      coord.setX(scaledX);
      coord.setY(scaledY);
    });
  }

  /**
   * Add a feature with layer name (typically feature type name), some attributes
   * and a Geometry. The Geometry must be in "pixel" space 0,0 upper left and
   * 256,256 lower right.
   * <p>
   * For optimization, geometries will be clipped and simplified. Features with
   * geometries outside the tile will be skipped.
   *
   * @param layerName  a {@link String} with the vector tile layer name.
   * @param attributes a {@link Map} with the vector tile feature attributes.
   * @param geometry   a {@link Point} for the vector tile feature.
   */
  public void addFeature(String layerName, Map<String, ?> attributes, Point geometry) {
    scaleGeometryToTileEnvelope(geometry);

    // no need to add empty geometry
    if (geometry.isEmpty()) {
      return;
    }

    Layer layer = layers.get(layerName);
    if (layer == null) {
      layer = new Layer();
      layers.put(layerName, layer);
    }

    Feature feature = new Feature();
    feature.geometry = geometry;

    for (Map.Entry<String, ?> e : attributes.entrySet()) {
      // skip attribute without value
      if (e.getValue() == null) {
        continue;
      }
      feature.tags.add(layer.key(e.getKey()));
      feature.tags.add(layer.value(e.getValue()));
    }

    layer.features.add(feature);
  }

  /**
   * @return a byte array with the vector tile
   */
  public VectorTile.Tile encode() {

    VectorTile.Tile.Builder tile = VectorTile.Tile.newBuilder();

    for (Map.Entry<String, Layer> e : layers.entrySet()) {
      String layerName = e.getKey();
      Layer layer = e.getValue();

      VectorTile.Tile.Layer.Builder tileLayer = VectorTile.Tile.Layer.newBuilder();

      tileLayer.setVersion(2);
      tileLayer.setName(layerName);

      tileLayer.addAllKeys(layer.keys());

      for (Object value : layer.values()) {
        VectorTile.Tile.Value.Builder tileValue = VectorTile.Tile.Value.newBuilder();
        if (value instanceof String) {
          tileValue.setStringValue((String) value);
        } else if (value instanceof Integer) {
          tileValue.setSintValue((Integer) value);
        } else if (value instanceof Long) {
          tileValue.setSintValue((Long) value);
        } else if (value instanceof Float) {
          tileValue.setFloatValue((Float) value);
        } else if (value instanceof Double) {
          tileValue.setDoubleValue((Double) value);
        } else if (value instanceof BigDecimal) {
          tileValue.setStringValue(value.toString());
        } else if (value instanceof Number) {
          tileValue.setDoubleValue(((Number) value).doubleValue());
        } else if (value instanceof Boolean) {
          tileValue.setBoolValue((Boolean) value);
        } else {
          tileValue.setStringValue(value.toString());
        }
        tileLayer.addValues(tileValue.build());
      }

      tileLayer.setExtent(extent);

      for (Feature feature : layer.features) {

        Point geometry = feature.geometry;

        VectorTile.Tile.Feature.Builder featureBuilder = VectorTile.Tile.Feature.newBuilder();

        featureBuilder.addAllTags(feature.tags);

        x = 0;
        y = 0;
        List<Integer> commands = commands(geometry.getCoordinates());

        // skip features with no geometry commands
        if (commands.isEmpty()) {
          continue;
        }

        featureBuilder.setType(VectorTile.Tile.GeomType.POINT);
        featureBuilder.addAllGeometry(commands);

        tileLayer.addFeatures(featureBuilder.build());
      }

      tile.addLayers(tileLayer.build());
    }

    return tile.build();
  }

  private int x = 0;
  private int y = 0;

  /**
   * // // // Ex.: MoveTo(3, 6), LineTo(8, 12), LineTo(20, 34), ClosePath //
   * Encoded as: [ 9 3 6 18 5 6 12 22 15 ] // == command type 7 (ClosePath),
   * length 1 // ===== relative LineTo(+12, +22) == LineTo(20, 34) // ===
   * relative LineTo(+5, +6) == LineTo(8, 12) // == [00010 010] = command type
   * 2 (LineTo), length 2 // === relative MoveTo(+3, +6) // == [00001 001] =
   * command type 1 (MoveTo), length 1 // Commands are encoded as uint32
   * varints, vertex parameters are // encoded as sint32 varints (zigzag).
   * Vertex parameters are // also encoded as deltas to the previous position.
   * The original // position is (0,0)
   */

  List<Integer> commands(Coordinate[] cs) {

    if (cs.length == 0) {
      return Collections.emptyList();
    }

    List<Integer> r = new ArrayList<>();

    int lineToIndex = 0;
    int lineToLength = 0;

    for (int i = 0; i < cs.length; i++) {
      Coordinate c = cs[i];

      if (i == 0) {
        r.add(commandAndLength(Command.MoveTo, 1));
      }

      int _x = (int) Math.round(c.x);
      int _y = (int) Math.round(c.y);

      // prevent point equal to the previous
      if (i > 0 && _x == x && _y == y) {
        lineToLength--;
        continue;
      }

      // delta, then zigzag
      r.add(zigZagEncode(_x - x));
      r.add(zigZagEncode(_y - y));

      x = _x;
      y = _y;

      if (i == 0 && cs.length > 1) {
        // can length be too long?
        lineToIndex = r.size();
        lineToLength = cs.length - 1;
        r.add(commandAndLength(Command.LineTo, lineToLength));
      }

    }

    // update LineTo length
    if (lineToIndex > 0) {
      if (lineToLength == 0) {
        // remove empty LineTo
        r.remove(lineToIndex);
      } else {
        // update LineTo with new length
        r.set(lineToIndex, commandAndLength(Command.LineTo, lineToLength));
      }
    }

    return r;
  }

  static int commandAndLength(int command, int repeat) {
    return repeat << 3 | command;
  }

  static int zigZagEncode(int n) {
    // https://developers.google.com/protocol-buffers/docs/encoding#types
    return (n << 1) ^ (n >> 31);
  }

  /**
   * Set tile envelop. Expecting WGS84 WebMercator.
   *
   * @param tileEnvelope in WGS84WebMercator
   */
  public void setTileEnvelope(Envelope tileEnvelope) {
    this.tileEnvelope = tileEnvelope;
  }

  public static final class Layer {

    final List<Feature> features = new ArrayList<>();

    private final Map<String, Integer> keys = new LinkedHashMap<>();
    private final Map<Object, Integer> values = new LinkedHashMap<>();

    public Integer key(String key) {
      return keys.computeIfAbsent(key, k -> keys.size());
    }

    public List<String> keys() {
      return new ArrayList<>(keys.keySet());
    }

    public Integer value(Object value) {
      return values.computeIfAbsent(value, k -> values.size());
    }

    public List<Object> values() {
      return List.copyOf(values.keySet());
    }
  }

  private static final class Feature {

    Point geometry;
    final List<Integer> tags = new ArrayList<>();

  }
}
