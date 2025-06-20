import {
  CreateLoadingPointVersion,
  ReadLoadingPointVersion,
} from '../../app/api';

export const LOADING_POINT: ReadLoadingPointVersion[] = [
  {
    creationDate: '2023-11-10T10:38:22.727356',
    creator: 'e524381',
    editionDate: '2023-11-10T10:38:22.727356',
    editor: 'e524381',
    id: 1255,
    number: 1231,
    designation: '12342',
    designationLong: undefined,
    connectionPoint: false,
    validFrom: new Date('2023-11-01'),
    validTo: new Date('2023-11-01'),
    etagVersion: 6,
    servicePointNumber: {
      number: 8504414,
      numberShort: 4414,
      checkDigit: 9,
      uicCountryCode: 85,
    },
    servicePointSloid: 'ch:1:sloid:4414',
  },
  {
    creationDate: '2023-11-10T10:38:38.715492',
    creator: 'e524381',
    editionDate: '2023-11-10T10:38:22.727356',
    editor: 'e524381',
    id: 1256,
    number: 1231,
    designation: '1234',
    designationLong: undefined,
    connectionPoint: false,
    validFrom: new Date('2023-11-02'),
    validTo: new Date('2099-11-07'),
    etagVersion: 7,
    servicePointNumber: {
      number: 8504414,
      numberShort: 4414,
      checkDigit: 9,
      uicCountryCode: 85,
    },
    servicePointSloid: 'ch:1:sloid:4414',
  },
];

export const LOADING_POINT_CREATE: CreateLoadingPointVersion = {
  creationDate: '2023-11-10T10:38:38.715492',
  creator: 'e524381',
  editionDate: '2023-11-10T10:38:22.727356',
  editor: 'e524381',
  id: 1256,
  number: 1231,
  designation: '1234',
  designationLong: undefined,
  connectionPoint: false,
  validFrom: new Date('2023-11-02'),
  validTo: new Date('2099-11-07'),
  etagVersion: 7,
  servicePointNumber: 1234567,
};
