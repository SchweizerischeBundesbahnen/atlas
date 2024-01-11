import { TestBed } from '@angular/core/testing';

import { Observable, of } from 'rxjs';
import { AppTestingModule } from '../../../../app.testing.module';
import { ActivatedRouteSnapshot, convertToParamMap, RouterStateSnapshot } from '@angular/router';
import { PersonWithReducedMobilityService, ReadPlatformVersion } from '../../../../api';
import { platformResolver } from './platform.resolver';
import { PrmPanelResolver } from '../../prm-panel/resolvers/prm-panel-resolver.service';

const platform: ReadPlatformVersion[] = [
  {
    creationDate: '2024-01-11T10:08:28.446803',
    creator: 'e524381',
    editionDate: '2024-01-11T10:08:28.446803',
    editor: 'e524381',
    id: 1002,
    sloid: 'ch:1:sloid:7000:0:100000',
    validFrom: new Date('2024-01-01'),
    validTo: new Date('2024-01-03'),
    etagVersion: 8,
    parentServicePointSloid: 'ch:1:sloid:7000',
    boardingDevice: 'TO_BE_COMPLETED',
    adviceAccessInfo: undefined,
    additionalInformation: undefined,
    contrastingAreas: 'YES',
    dynamicAudio: 'TO_BE_COMPLETED',
    dynamicVisual: 'TO_BE_COMPLETED',
    height: undefined,
    inclination: undefined,
    inclinationLongitudinal: undefined,
    inclinationWidth: undefined,
    infoOpportunities: [],
    levelAccessWheelchair: 'TO_BE_COMPLETED',
    partialElevation: undefined,
    superelevation: undefined,
    tactileSystem: undefined,
    vehicleAccess: undefined,
    wheelchairAreaLength: undefined,
    wheelchairAreaWidth: undefined,
    number: {
      number: 8507000,
      checkDigit: 3,
      numberShort: 7000,
      uicCountryCode: 85,
    },
  },
];

describe('PrmPlatformResolver', () => {
  const personWithReducedMobilityServiceSpy = jasmine.createSpyObj(
    'personWithReducedMobilityService',
    ['getPlatformVersions'],
  );
  personWithReducedMobilityServiceSpy.getPlatformVersions.and.returnValue(of(platform));

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [AppTestingModule],
      providers: [
        {
          provide: PersonWithReducedMobilityService,
          useValue: personWithReducedMobilityServiceSpy,
        },
      ],
    });
  });

  it('should get platform from prm-directory', () => {
    const mockRoute = {
      paramMap: convertToParamMap({ platformSloid: 'ch:1:sloid:7000:0:100000' }),
    } as ActivatedRouteSnapshot;

    const result = TestBed.runInInjectionContext(() =>
      platformResolver(mockRoute, {} as RouterStateSnapshot),
    ) as Observable<ReadPlatformVersion[]>;

    result.subscribe((versions) => {
      expect(versions.length).toBe(1);
      expect(versions[0].sloid).toBe('ch:1:sloid:7000:0:100000');
    });
  });
});
