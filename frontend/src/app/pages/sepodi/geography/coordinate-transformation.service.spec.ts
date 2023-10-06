import { TestBed } from '@angular/core/testing';
import { CoordinateTransformationService } from './coordinate-transformation.service';
import { SpatialReference } from '../../../api';

describe('CoordinateTransformationService', () => {
  let service: CoordinateTransformationService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(CoordinateTransformationService);
  });

  it('should transform from lv95 to wgs84', () => {
    //when
    const transformedToWgs84 = service.transform(
      {
        north: 1201099.0,
        east: 2600783.0,
        spatialReference: SpatialReference.Lv95,
      },
      SpatialReference.Wgs84
    );
    //then
    expect(transformedToWgs84).toEqual({
      north: 46.96096807883433,
      east: 7.448919722210154,
      spatialReference: SpatialReference.Wgs84,
    });
  });

  it('should transform from wgs84 to lv95', () => {
    //when
    const transformedToLv95 = service.transform(
      {
        north: 46.96096807883433,
        east: 7.448919722210154,
        spatialReference: SpatialReference.Wgs84,
      },
      SpatialReference.Lv95
    );
    //then
    expect(transformedToLv95).toEqual({
      north: 1201099.0009949398,
      east: 2600783.0005625985,
      spatialReference: SpatialReference.Lv95,
    });
  });
});
