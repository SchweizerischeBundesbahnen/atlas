import { TestBed } from '@angular/core/testing';

import { Observable, of } from 'rxjs';
import { ActivatedRouteSnapshot, convertToParamMap, RouterStateSnapshot } from '@angular/router';
import { contactPointResolver } from './contact-point.resolver';
import {
  ContactPointType,
  PersonWithReducedMobilityService,
  ReadContactPointVersion,
  StandardAttributeType,
} from '../../../../../../api';
import { AppTestingModule } from '../../../../../../app.testing.module';

const contactPoint: ReadContactPointVersion[] = [
  {
    creationDate: '2024-01-22T13:52:30.598026',
    creator: 'e524381',
    editionDate: '2024-01-22T13:52:30.598026',
    editor: 'e524381',
    id: 1000,
    sloid: 'ch:1:sloid:12345:1',
    validFrom: new Date('2000-01-01'),
    validTo: new Date('2000-12-31'),
    etagVersion: 0,
    parentServicePointSloid: 'ch:1:sloid:7000',
    designation: 'designation',
    additionalInformation: 'additional',
    inductionLoop: StandardAttributeType.ToBeCompleted,
    openingHours: 'openingHours',
    wheelchairAccess: StandardAttributeType.ToBeCompleted,
    type: ContactPointType.InformationDesk,
    number: {
      number: 8507000,
      numberShort: 7000,
      uicCountryCode: 85,
      checkDigit: 3,
    },
  },
];

describe('PrmContactPointResolver', () => {
  const personWithReducedMobilityServiceSpy = jasmine.createSpyObj(
    'personWithReducedMobilityService',
    ['getContactPointVersions'],
  );
  personWithReducedMobilityServiceSpy.getContactPointVersions.and.returnValue(of(contactPoint));

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

  it('should get contactPoint from prm-directory', () => {
    const mockRoute = {
      paramMap: convertToParamMap({ sloid: 'ch:1:sloid:12345:1' }),
    } as ActivatedRouteSnapshot;

    const result = TestBed.runInInjectionContext(() =>
      contactPointResolver(mockRoute, {} as RouterStateSnapshot),
    ) as Observable<ReadContactPointVersion[]>;

    result.subscribe((versions) => {
      expect(versions.length).toBe(1);
      expect(versions[0].sloid).toBe('ch:1:sloid:12345:1');
    });
  });
});
