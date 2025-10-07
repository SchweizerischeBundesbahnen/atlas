import { SpatialReference } from '../../app/api';
import { ReadSectorVersion } from '../../app/api/model/readSectorVersion';

export const BERN_PLATFORM_1_SECTOR_A: ReadSectorVersion[] = [
  {
    trafficPointSloid: 'ch:1:sloid:7000:1:1',
    validFrom: new Date('2014-12-14'),
    validTo: new Date('2099-12-31'),
    designation: 'A',
    sectorGeolocation: {
      lv95: {
        north: 0,
        east: 0,
        spatialReference: SpatialReference.Lv95,
      },
      spatialReference: 'WGS84WEB',
      wgs84: {
        north: 0,
        east: 0,
        spatialReference: SpatialReference.Wgs84,
      },
      lv03: {
        north: 0,
        east: 0,
        spatialReference: SpatialReference.Lv03,
      },
    },
    sloid: 'ch:1:sloid:7000:1:1:1',
  },
];

export const BERN_PLATFORM_1_SECTOR_MULTIPLE: ReadSectorVersion[] = [
  {
    trafficPointSloid: 'ch:1:sloid:7000:1:1',
    validFrom: new Date('2014-12-14'),
    validTo: new Date('2099-12-31'),
    designation: 'A',
    sectorGeolocation: {
      lv95: {
        north: 0,
        east: 0,
        spatialReference: SpatialReference.Lv95,
      },
      spatialReference: 'WGS84WEB',
      wgs84: {
        north: 0,
        east: 0,
        spatialReference: SpatialReference.Wgs84,
      },
      lv03: {
        north: 0,
        east: 0,
        spatialReference: SpatialReference.Lv03,
      },
    },
    sloid: 'ch:1:sloid:7000:1:1:1',
  },
  {
    trafficPointSloid: 'ch:1:sloid:7000:1:1',
    validFrom: new Date('2014-12-14'),
    validTo: new Date('2099-12-31'),
    designation: 'B',
    sectorGeolocation: {
      lv95: {
        north: 0,
        east: 0,
        spatialReference: SpatialReference.Lv95,
      },
      spatialReference: 'WGS84WEB',
      wgs84: {
        north: 0,
        east: 0,
        spatialReference: SpatialReference.Wgs84,
      },
      lv03: {
        north: 0,
        east: 0,
        spatialReference: SpatialReference.Lv03,
      },
    },
    sloid: 'ch:1:sloid:7000:1:1:2',
  },
];
