import {
  ExpressionSpecification,
  GeoJSONSourceSpecification,
  RasterSourceSpecification,
  StyleSpecification,
  SymbolLayerSpecification,
} from 'maplibre-gl';
import { environment } from '../../../../environments/environment';

export const SERVICE_POINT_MIN_ZOOM = 12;
export const MAP_SOURCE_NAME = 'geodata';
export const MAP_LAYER_NAME = 'service-points';
export const MAP_TRAFFIC_POINT_LAYER_NAME = 'traffic_points';
export const MAP_SECTOR_LAYER_NAME = 'sectors';

const geoAdminRasterSource = (wmtsLayer: string): RasterSourceSpecification => ({
  type: 'raster',
  tiles: [`https://wmts.geo.admin.ch/1.0.0/${wmtsLayer}/default/current/3857/{z}/{x}/{y}.jpeg`],
  tileSize: 256,
  attribution: '&copy; OpenStreetMap Contributors',
  bounds: [5.140242, 45.3981812, 11.47757, 48.230651],
});

const ICON_SIZE: ExpressionSpecification = [
  'interpolate',
  ['linear'],
  ['zoom'],
  9,
  0.2,
  10,
  0.4,
  12,
  0.6,
  14,
  0.8,
  16,
  1,
];
const INDICATOR_ICON_SIZE: ExpressionSpecification = [
  'interpolate',
  ['linear'],
  ['zoom'],
  9,
  0.4,
  10,
  0.6,
  12,
  0.8,
  14,
  1,
  16,
  1.4,
];

const symbolLayer = (
  id: string,
  iconImage: string | ExpressionSpecification,
  size: ExpressionSpecification
): SymbolLayerSpecification => ({
  id,
  source: id,
  type: 'symbol',
  layout: {
    'icon-allow-overlap': true,
    'icon-image': iconImage,
    'icon-size': size,
  },
});

const EMPTY_GEO_JSON_FEATURE_SOURCE: GeoJSONSourceSpecification = {
  type: 'geojson',
  data: {
    type: 'Feature',
    geometry: {
      type: 'Point',
      coordinates: [0, 0],
    },
    properties: null,
  },
};

const EMPTY_GEO_JSON_FEATURECOLLECTION_SOURCE: GeoJSONSourceSpecification = {
  type: 'geojson',
  data: {
    type: 'FeatureCollection',
    features: [],
  },
};

export const MAP_STYLE_SPEC: StyleSpecification = {
  version: 8,
  sources: {
    swisstopofarbe: geoAdminRasterSource('ch.swisstopo.pixelkarte-farbe'),
    swisstopograu: geoAdminRasterSource('ch.swisstopo.pixelkarte-grau'),
    satellite_swiss: geoAdminRasterSource('ch.swisstopo.swissimage-product'),
    osm: {
      type: 'raster',
      tiles: [
        `https://journey-maps-tiles.geocdn.sbb.ch/styles/journey_maps_bright_v1/{z}/{x}/{y}.webp?api_key=${environment.journeyMapsApiKey}`,
      ],
      tileSize: 256,
      attribution:
        '<a href="https://www.sbb.ch/" target="_blank">&copy; SBB/CFF/FFS</a> <a href="https://www.geops.com/" target="_blank">&copy; geOps Tiles</a> <a href="https://www.openstreetmap.org/about/" target="_blank">&copy; OpenStreetMap</a>',
    },
    satellite: {
      type: 'raster',
      tiles: [
        `https://ibasemaps-api.arcgis.com/arcgis/rest/services/World_Imagery/MapServer/tile/{z}/{y}/{x}?token=${environment.arcgisMapToken}`,
      ],
      tileSize: 256,
      attribution: 'Esri, Maxar, Earthstar Geographics, USDA FSA, USGS, Aerogrid, IGN, IGP, and the GIS User Community',
    },
    geodata: {
      type: 'vector',
      minzoom: SERVICE_POINT_MIN_ZOOM,
      tiles: [
        `${environment.atlasUnauthApiUrl}/service-point-directory/internal/service-points/geodata/{z}/{x}/{y}.pbf`,
      ],
      promoteId: 'number',
    },
    current_coordinates: EMPTY_GEO_JSON_FEATURE_SOURCE,
    traffic_points: EMPTY_GEO_JSON_FEATURE_SOURCE,
    current_traffic_point: EMPTY_GEO_JSON_FEATURE_SOURCE,
    hovered_traffic_point: EMPTY_GEO_JSON_FEATURE_SOURCE,
    sectors: EMPTY_GEO_JSON_FEATURE_SOURCE,
    current_sector: EMPTY_GEO_JSON_FEATURE_SOURCE,
    hovered_sector: EMPTY_GEO_JSON_FEATURECOLLECTION_SOURCE,
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
      id: 'swisstopograu',
      type: 'raster',
      source: 'swisstopograu',
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
      type: 'circle',
      paint: {
        'circle-radius': [
          'interpolate',
          ['linear'],
          ['zoom'],
          9,
          0.2 * 10,
          10,
          0.4 * 10,
          12,
          0.6 * 10,
          14,
          0.8 * 10,
          16,
          10,
        ],
        'circle-color': [
          'match',
          ['get', 'type'],
          'STOP_POINT',
          '#1c429c',
          'FREIGHT_SERVICE_POINT',
          '#1c429c',
          'STOP_POINT_AND_FREIGHT_SERVICE_POINT',
          '#1c429c',
          'SERVICE_POINT',
          '#e3bd63',
          'OPERATING_POINT_TECHNICAL',
          '#008000',
          'ON_DEMAND',
          '#02f5f5',
          'rgba(0, 0, 0, 0)', // Default 100% transparentes Schwarz
        ],
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
    symbolLayer(MAP_TRAFFIC_POINT_LAYER_NAME, ['get', 'type'], ICON_SIZE),
    symbolLayer('current_traffic_point', 'SELECTED_TP_INDICATOR', INDICATOR_ICON_SIZE),
    symbolLayer('hovered_traffic_point', 'SELECTED_TP_INDICATOR', INDICATOR_ICON_SIZE),
    symbolLayer(MAP_SECTOR_LAYER_NAME, 'SECTOR', ICON_SIZE),
    symbolLayer('current_sector', 'SELECTED_SECTOR_INDICATOR', INDICATOR_ICON_SIZE),
    symbolLayer('hovered_sector', 'SELECTED_SECTOR_INDICATOR', INDICATOR_ICON_SIZE),
  ],
};
