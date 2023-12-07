import {
  ContainerReadTrafficPointElementVersion,
  ReadTrafficPointElementVersion,
} from '../../app/api';

export const BERN_WYLEREGG_TRAFFIC_POINTS: ReadTrafficPointElementVersion[] = [
  {
    creationDate: '2019-07-22T14:30:55',
    creator: 'fs45117',
    editionDate: '2020-04-12T23:57:10',
    editor: 'GSU_DIDOK',
    id: 9298,
    designation: undefined,
    designationOperational: '1',
    length: undefined,
    boardingAreaHeight: undefined,
    compassDirection: 53.0,
    trafficPointElementType: 'BOARDING_PLATFORM',
    sloid: 'ch:1:sloid:89008:0:1',
    parentSloid: undefined,
    validFrom: new Date('2019-07-22'),
    validTo: new Date('2099-12-31'),
    etagVersion: 0,
    servicePointNumber: {
      number: 8589008,
      uicCountryCode: 85,
      numberShort: 89008,
      checkDigit: 7,
    },
    servicePointSloid: 'ch:1:sloid:89008',
    trafficPointElementGeolocation: {
      spatialReference: 'LV95',
      lv95: {
        north: 1201104.862,
        east: 2600795.343,
        spatialReference: 'LV95',
      },
      wgs84: {
        north: 46.96102079646,
        east: 7.44908190053,
        spatialReference: 'WGS84',
      },
      lv03: {
        north: 201104.862,
        east: 600795.343,
        spatialReference: 'LV03',
      },
      height: 553.9,
    },
    hasGeolocation: true,
  },
  {
    creationDate: '2019-07-22T14:30:56',
    creator: 'fs45117',
    editionDate: '2020-04-12T23:57:10',
    editor: 'GSU_DIDOK',
    id: 9299,
    designation: undefined,
    designationOperational: '2',
    length: undefined,
    boardingAreaHeight: undefined,
    compassDirection: 231.0,
    trafficPointElementType: 'BOARDING_PLATFORM',
    sloid: 'ch:1:sloid:89008:0:2',
    parentSloid: undefined,
    validFrom: new Date('2019-07-22'),
    validTo: new Date('2099-12-31'),
    etagVersion: 0,
    servicePointNumber: {
      number: 8589008,
      uicCountryCode: 85,
      numberShort: 89008,
      checkDigit: 7,
    },
    servicePointSloid: 'ch:1:sloid:89008',
    trafficPointElementGeolocation: {
      spatialReference: 'LV95',
      lv95: {
        north: 1201055.104,
        east: 2600711.93,
        spatialReference: 'LV95',
      },
      wgs84: {
        north: 46.96057330114,
        east: 7.4479859044,
        spatialReference: 'WGS84',
      },
      lv03: {
        north: 201055.104,
        east: 600711.93,
        spatialReference: 'LV03',
      },
      height: 551.5,
    },
    hasGeolocation: true,
  },
];

export const BERN_WYLEREGG_TRAFFIC_POINTS_CONTAINER: ContainerReadTrafficPointElementVersion = {
  objects: BERN_WYLEREGG_TRAFFIC_POINTS,
  totalCount: 2,
};
