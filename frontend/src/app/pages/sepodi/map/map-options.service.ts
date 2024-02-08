import {Injectable} from '@angular/core';
import {LngLatBoundsLike} from 'maplibre-gl';

const SWISS_BOUNDING_BOX: LngLatBoundsLike = [
  [5.7349, 45.6755],
  [10.6677, 47.9163],
];

export const MAP_STYLES: MapStyle[] = [
  { id: 'swisstopofarbe', label: 'SEPODI.MAP_STYLES.SWISSTOPO' },
  { id: 'swisstopograu', label: 'SEPODI.MAP_STYLES.SWISSTOPO_GRAY' },
  { id: 'satellite_swiss', label: 'SEPODI.MAP_STYLES.SATELLITE_SWISS' },
  { id: 'osm', label: 'SEPODI.MAP_STYLES.OPEN_STREET_MAP' },
  { id: 'satellite', label: 'SEPODI.MAP_STYLES.SATELLITE' },
];

export interface MapStyle {
  id: string;
  label: string;
}

@Injectable({
  providedIn: 'root',
})
export class MapOptionsService {

  getInitialBoundingBox(): LngLatBoundsLike {
    return SWISS_BOUNDING_BOX;
  }
}
