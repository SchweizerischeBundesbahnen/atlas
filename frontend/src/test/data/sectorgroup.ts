import { CreateSectorGroupVersion } from '../../app/api/model/createSectorGroupVersion';
import { SectorGroupVersion } from '../../app/api/model/sectorGroupVersion';

export const CREATE_BERN_PLATFORM_1_SECTORGROUP_A: CreateSectorGroupVersion[] =
  [
    {
      trafficPointSloid: 'ch:1:sloid:7000:1:1',
      validFrom: new Date('2014-12-14'),
      validTo: new Date('2099-12-31'),
      designation: 'A',
      sloid: 'ch:1:sloid:7000:1:1:5',
      length: 1200,
      sectorSloids: new Set(['ch:1:sloid:7000:1:1:1', 'ch:1:sloid:7000:1:1:2']),
    },
  ];

export const BERN_PLATFORM_1_SECTORGROUP_A: SectorGroupVersion[] = [
  {
    id: 12345,
    trafficPointSloid: 'ch:1:sloid:7000:1:1',
    validFrom: new Date('2014-12-14'),
    validTo: new Date('2099-12-31'),
    designation: 'A',
    sloid: 'ch:1:sloid:7000:1:1:5',
    length: 1200,
  },
];
