import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, convertToParamMap, RouterModule } from '@angular/router';
import { TimetableFieldNumbersService, Version } from '../../../api';
import { TimetableFieldNumberDetailResolver } from './timetable-field-number-detail.resolver';
import { of } from 'rxjs';
import StatusEnum = Version.StatusEnum;

const version: Version = {
  id: 1234,
  ttfnid: 'ttfnid',
  name: 'name',
  swissTimetableFieldNumber: 'asdf',
  status: 'ACTIVE',
  validFrom: new Date('2021-06-01'),
  validTo: new Date('2029-06-01'),
  number: '1.1',
  businessOrganisation: 'sbb',
};

describe('TimetableFieldNumberDetailResolver', () => {
  const timetableFieldNumberServiceSpy = jasmine.createSpyObj('timetableFieldNumbersService', [
    'getAllVersionsVersioned',
  ]);
  timetableFieldNumberServiceSpy.getAllVersionsVersioned.and.returnValue(of([version]));

  let resolver: TimetableFieldNumberDetailResolver;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterModule.forRoot([])],
      providers: [
        TimetableFieldNumberDetailResolver,
        { provide: TimetableFieldNumbersService, useValue: timetableFieldNumberServiceSpy },
      ],
    });
    resolver = TestBed.inject(TimetableFieldNumberDetailResolver);
  });

  it('should create', () => {
    expect(resolver).toBeTruthy();
  });

  it('should get version from service to display', () => {
    const mockRoute = { paramMap: convertToParamMap({ id: '1234' }) } as ActivatedRouteSnapshot;

    const resolvedVersion = resolver.resolve(mockRoute);

    resolvedVersion.subscribe((versions) => {
      expect(versions.length).toBe(1);
      expect(versions[0].id).toBe(1234);
      expect(versions[0].status).toBe(StatusEnum.Active);
      expect(versions[0].ttfnid).toBe('ttfnid');
    });
  });
});
