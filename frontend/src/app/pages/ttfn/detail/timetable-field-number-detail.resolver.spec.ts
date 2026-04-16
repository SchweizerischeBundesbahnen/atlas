import { TestBed } from '@angular/core/testing';
import { beforeEach, describe, expect, it, vi, type Mocked } from 'vitest';
import { ActivatedRouteSnapshot, convertToParamMap, RouterStateSnapshot } from '@angular/router';
import { Status, TimetableFieldNumberVersion } from '../../../api';
import {
  TimetableFieldNumberDetailResolver,
  timetableFieldNumberResolver,
} from './timetable-field-number-detail.resolver';
import { firstValueFrom, Observable, of } from 'rxjs';
import { AppTestingModule } from '../../../app.testing.module';
import { TimetableFieldNumberService } from '../../../api/service/lidi/timetable-field-number.service';

describe('TimetableFieldNumberDetailResolver', () => {
  const version: TimetableFieldNumberVersion = {
    id: 1234,
    ttfnid: 'ttfnid',
    status: 'VALIDATED',
    validFrom: new Date('2021-06-01'),
    validTo: new Date('2029-06-01'),
    number: '1.1',
    businessOrganisation: 'sbb',
    descriptionOutwardLine1: 'desc outward 1',
    meanOfTransport: 'TRAIN',
  };

  let timetableFieldNumberServiceSpy: Mocked<Pick<TimetableFieldNumberService, 'getAllVersionsVersioned'>>;
  let resolver: TimetableFieldNumberDetailResolver;

  beforeEach(() => {
    timetableFieldNumberServiceSpy = {
      getAllVersionsVersioned: vi.fn(),
    };
    timetableFieldNumberServiceSpy.getAllVersionsVersioned.mockReturnValue(of([version]));

    TestBed.configureTestingModule({
      imports: [AppTestingModule],
      providers: [
        TimetableFieldNumberDetailResolver,
        {
          provide: TimetableFieldNumberService,
          useValue: timetableFieldNumberServiceSpy,
        },
      ],
    });
    resolver = TestBed.inject(TimetableFieldNumberDetailResolver);
  });

  it('should create', () => {
    expect(resolver).toBeTruthy();
  });

  it('should get version from service to display', async () => {
    const mockRoute = {
      paramMap: convertToParamMap({ id: '1234' }),
    } as ActivatedRouteSnapshot;

    const result = TestBed.runInInjectionContext(() =>
      timetableFieldNumberResolver(mockRoute, {} as RouterStateSnapshot)
    ) as Observable<TimetableFieldNumberVersion[]>;

    const versions = await firstValueFrom(result);
    expect(versions.length).toBe(1);
    expect(versions[0].id).toBe(1234);
    expect(versions[0].status).toBe(Status.Validated);
    expect(versions[0].ttfnid).toBe('ttfnid');
  });
});
