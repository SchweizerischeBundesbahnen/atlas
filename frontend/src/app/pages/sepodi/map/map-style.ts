import { StyleSpecification } from 'maplibre-gl';
import { environment } from '../../../../environments/environment';

export const MAP_ZOOM_DETAILS = 10.5;
export const MAP_SOURCE_NAME = 'geodata';
export const MAP_LAYER_NAME = 'service-points';
export const MAP_STYLE_SPEC: StyleSpecification = {
  version: 8,
  sources: {
    swisstopofarbe: {
      type: 'raster',
      tiles: [
        'https://wmts.geo.admin.ch/1.0.0/ch.swisstopo.pixelkarte-farbe/default/current/3857/{z}/{x}/{y}.jpeg',
      ],
      tileSize: 256,
      attribution: '&copy; OpenStreetMap Contributors',
      maxzoom: 19,
      bounds: [5.140242, 45.3981812, 11.47757, 48.230651],
    },
    osm: {
      type: 'raster',
      tiles: [
        'https://journey-maps-tiles.geocdn.sbb.ch/styles/osm_streets_v2/{z}/{x}/{y}.webp?api_key=74c0170da613da0d825339a7f0dd0546',
      ],
      tileSize: 256,
      attribution:
        '&copy; SBB/CFF/FFS &copy; geOps Tiles &copy; imagico &copy; OpenMapTiles &copy; OpenStreetMap Contributors',
      maxzoom: 22,
    },
    satelite: {
      type: 'raster',
      tiles: [
        'https://journey-maps-tiles.geocdn.sbb.ch/styles/aerial/{z}/{x}/{y}.webp?api_key=74c0170da613da0d825339a7f0dd0546',
      ],
      tileSize: 256,
      attribution:
        '&copy; SBB/CFF/FFS &copy; geOps Tiles &copy; imagico &copy; OpenMapTiles &copy; OpenStreetMap Contributors',
      maxzoom: 22,
    },
    satelite_swiss: {
      type: 'raster',
      tiles: [
        'https://wmts.geo.admin.ch/1.0.0/ch.swisstopo.swissimage-product/default/current/3857/{z}/{x}/{y}.jpeg',
      ],
      tileSize: 256,
      attribution: '&copy; OpenStreetMap Contributors',
      maxzoom: 19,
      bounds: [5.140242, 45.3981812, 11.47757, 48.230651],
    },
    geodata: {
      type: 'vector',
      minzoom: 5,
      maxzoom: 20,
      tiles: [
        `${environment.atlasApiUrl}/service-point-directory/v1/service-points/geodata/{z}/{x}/{y}.pbf`,
      ],
      promoteId: 'number',
    },
  },
  layers: [
    {
      id: 'swisstopofarbe',
      type: 'raster',
      source: 'swisstopofarbe',
      paint: {
        'raster-opacity': 0.5,
      },
      layout: {
        visibility: 'none',
      },
    },
    {
      id: 'osm',
      type: 'raster',
      source: 'osm',
      paint: {
        'raster-opacity': 0.8,
      },
    },
    {
      id: 'satelite',
      type: 'raster',
      source: 'satelite',
    },
    {
      id: 'satelite_swiss',
      type: 'raster',
      source: 'satelite_swiss',
      layout: {
        visibility: 'none',
      },
    },
    {
      id: 'selected-sepo',
      'source-layer': MAP_LAYER_NAME,
      source: MAP_SOURCE_NAME,
      type: 'circle',
      paint: {
        'circle-radius': ['interpolate', ['linear'], ['zoom'], 5, 3, 20, 18],
        'circle-color': [
          'case',
          ['boolean', ['feature-state', 'selected'], false],
          '#FFFF00',
          'transparent',
        ],
        'circle-opacity': 0.9,
      },
    },
    {
      id: MAP_SOURCE_NAME,
      'source-layer': MAP_LAYER_NAME,
      source: MAP_SOURCE_NAME,
      type: 'circle',
      paint: {
        'circle-radius': ['interpolate', ['linear'], ['zoom'], 5, 1.5, 20, 9],
        'circle-color': [
          'step',
          ['zoom'],
          'darkblue',
          MAP_ZOOM_DETAILS,
          'darkblue' /* => STAM: write the expression, to color the service-point type-specific */,
        ],
        'circle-opacity': 0.9,
        'circle-stroke-width': ['interpolate', ['linear'], ['zoom'], 12, 1, 20, 2],
      },
    },
  ],
};
