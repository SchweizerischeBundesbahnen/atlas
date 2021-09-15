import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, convertToParamMap, RouterModule } from '@angular/router';
import { TimetableFieldNumbersService, Version } from '../../api';
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
};

describe('TimetableFieldNumberDetailResolver', () => {
  const timetableFieldNumberServiceSpy = jasmine.createSpyObj('timetableFieldNumbersService', [
    'getVersion',
  ]);
  timetableFieldNumberServiceSpy.getVersion.and.returnValue(of(version));

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

    resolvedVersion.subscribe((version) => {
      expect(version.id).toBe(1234);
      expect(version.status).toBe(StatusEnum.Active);
      expect(version.ttfnid).toBe('ttfnid');
    });
  });
});
