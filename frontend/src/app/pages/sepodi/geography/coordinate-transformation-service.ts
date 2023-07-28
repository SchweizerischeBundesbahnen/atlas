import { CoordinatePair, SpatialReference } from '../../../api';
import proj4 from 'proj4';
import { Injectable } from '@angular/core';

export const keyLv95 = 'EPSG:2056';

@Injectable({ providedIn: 'root' })
export class CoordinateTransformationService {
  constructor() {
    proj4.defs(
      SpatialReference.Lv95,
      '+proj=somerc +lat_0=46.95240555555556 +lon_0=7.439583333333333 +k_0=1 +x_0=2600000 +y_0=1200000 +ellps=bessel +towgs84=674.374,15.056,405.346,0,0,0,0 +units=m +no_defs '
    );
  }

  transform(
    coordinatePair: CoordinatePair,
    from: SpatialReference,
    to: SpatialReference
  ): CoordinatePair {
    const transformationResult = proj4(from, to, [coordinatePair.east, coordinatePair.north]);
    return {
      east: transformationResult[1],
      north: transformationResult[0],
    };
  }
}
