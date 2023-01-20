import { LayerSpecification, LngLatBoundsLike, StyleSpecification } from 'maplibre-gl';

export const SWISS_BOUNDING_BOX: LngLatBoundsLike = [
  // CH bounds;
  [5.7349, 45.6755],
  [10.6677, 47.9163],
];

export const MAP_SOURCE_NAME = 'geodata';
export const MAP_LAYER_NAME = 'service-points';

/* when source "id" not specified, layer and source share the same id */
export const TILES_SOURCE_SPEC: any = {
  type: 'vector',
  minzoom: 7,
  maxzoom: 20,
  tiles: [
    `http://localhost:8888/service-point-directory/v1/geodata/${MAP_LAYER_NAME}/{z}/{x}/{y}.pbf`,
  ],
};

const DEFAULT_OPACITY = 0.9;

export const SERVICE_POINTS_LAYER_SPEC: LayerSpecification = {
  id: MAP_SOURCE_NAME,
  'source-layer': MAP_LAYER_NAME,
  source: TILES_SOURCE_SPEC,
  type: 'circle',
  paint: {
    'circle-radius': ['interpolate', ['linear'], ['zoom'], 5, 1.5, 20, 9],
    'circle-color': [
      'step',
      ['zoom'],
      'darkblue',
      10.5,
      'darkblue' /* => stam: here comes the logic to paint service point different by type */,
    ],
    'circle-opacity': DEFAULT_OPACITY,
    'circle-stroke-color': 'rgb(255,255,255)',
    'circle-stroke-opacity': DEFAULT_OPACITY,
    'circle-stroke-width': ['interpolate', ['linear'], ['zoom'], 12, 0, 20, 1],
  },
};

export const MAP_STYLE_SPEC: StyleSpecification = {
  version: 8,
  sources: {
    osm: {
      type: 'raster',
      tiles: [
        'https://wmts.geo.admin.ch/1.0.0/ch.swisstopo.pixelkarte-farbe/default/current/3857/{z}/{x}/{y}.jpeg',
      ],
      tileSize: 256,
      attribution: '&copy; OpenStreetMap Contributors',
      maxzoom: 19,
      bounds: [5.140242, 45.3981812, 11.47757, 48.230651],
    },
  },
  layers: [
    {
      id: 'osm',
      type: 'raster',
      source: 'osm',
      paint: {
        'raster-opacity': 0.5,
      },
    },
  ],
};
