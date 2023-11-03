import { CoordinatePair, SpatialReference } from '../../../api';
import proj4 from 'proj4';
import { Injectable } from '@angular/core';

/**
 * EPSG String from https://epsg.io/2056
 */
@Injectable({ providedIn: 'root' })
export class CoordinateTransformationService {
  constructor() {
    proj4.defs(
      SpatialReference.Lv95,
      '+proj=somerc +lat_0=46.9524055555556 +lon_0=7.43958333333333 +k_0=1 +x_0=2600000 +y_0=1200000 +ellps=bessel +towgs84=674.374,15.056,405.346,0,0,0,0 +units=m +no_defs +type=crs',
    );
  }

  transform(coordinatePair: CoordinatePair, to: SpatialReference): CoordinatePair | undefined {
    if (
      !!coordinatePair.north &&
      !!coordinatePair.east &&
      coordinatePair.north !== 0 &&
      coordinatePair.east !== 0
    ) {
      const transformationResult = proj4(coordinatePair.spatialReference, to, [
        coordinatePair.east,
        coordinatePair.north,
      ]);
      return {
        east: transformationResult[0],
        north: transformationResult[1],
        spatialReference: to,
      };
    }
    return undefined;
  }
}
