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
    satellite: {
      type: 'raster',
      tiles: [
        'https://journey-maps-tiles.geocdn.sbb.ch/styles/aerial/{z}/{x}/{y}.webp?api_key=74c0170da613da0d825339a7f0dd0546',
      ],
      tileSize: 256,
      attribution:
        '&copy; SBB/CFF/FFS &copy; geOps Tiles &copy; imagico &copy; OpenMapTiles &copy; OpenStreetMap Contributors',
      maxzoom: 22,
    },
    satellite_swiss: {
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
    current_coordinates: {
      type: 'geojson',
      data: {
        type: 'Feature',
        geometry: {
          type: 'Point',
          coordinates: [0, 0],
        },
      },
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
    },
    {
      id: 'osm',
      type: 'raster',
      source: 'osm',
      paint: {
        'raster-opacity': 0.8,
      },
      layout: {
        visibility: 'none',
      },
    },
    {
      id: 'satellite',
      type: 'raster',
      source: 'satellite',
      layout: {
        visibility: 'none',
      },
    },
    {
      id: 'satellite_swiss',
      type: 'raster',
      source: 'satellite_swiss',
      layout: {
        visibility: 'none',
      },
    },
    {
      id: MAP_SOURCE_NAME,
      'source-layer': MAP_LAYER_NAME,
      source: MAP_SOURCE_NAME,
      type: 'symbol',
      layout: {
        'icon-image': ['get', 'type'],
        'icon-size': [
          'interpolate',
          ['linear'],
          ['zoom'],
          // zoom is 9 (or less) -> icon-size will be 0.2
          9,
          0.2,
          10,
          0.4,
          12,
          0.6,
          14,
          0.8,
          // zoom is 16 (or greater) -> icon-size will be 1
          16,
          1,
        ],
        'icon-allow-overlap': true,
        'icon-ignore-placement': true,
      },
    },
    {
      id: 'current_coordinates',
      type: 'circle',
      source: 'current_coordinates',
      paint: {
        'circle-radius': ['interpolate', ['linear'], ['zoom'], 9, 2, 10, 4, 12, 5, 14, 7, 16, 8],
        'circle-color': 'transparent',
        'circle-opacity': 1,
        'circle-stroke-color': 'hotpink',
        'circle-stroke-opacity': 1,
        'circle-stroke-width': 3,
      },
    },
  ],
};
