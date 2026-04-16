import { TestBed } from '@angular/core/testing';
import { beforeEach, describe, expect, it, type Mocked, vi } from 'vitest';

import { firstValueFrom, Observable, of } from 'rxjs';
import { ActivatedRouteSnapshot, convertToParamMap, RouterStateSnapshot } from '@angular/router';
import { referencePointResolver } from './reference-point.resolver';
import { ReadReferencePointVersion } from '../../../../../../api';
import { AppTestingModule } from '../../../../../../app.testing.module';
import { ReferencePointService } from '../../../../../../api/service/prm/reference-point/reference-point.service';

const referencePoint: ReadReferencePointVersion[] = [
  {
    creationDate: '2024-01-22T13:52:30.598026',
    creator: 'u123456',
    editionDate: '2024-01-22T13:52:30.598026',
    editor: 'u123456',
    id: 1000,
    sloid: 'ch:1:sloid:12345:1',
    validFrom: new Date('2000-01-01'),
    validTo: new Date('2000-12-31'),
    etagVersion: 0,
    parentServicePointSloid: 'ch:1:sloid:7000',
    designation: 'designation',
    additionalInformation: 'additional',
    mainReferencePoint: true,
    referencePointType: 'PLATFORM',
    number: {
      number: 8507000,
      numberShort: 7000,
      uicCountryCode: 85,
      checkDigit: 3,
    },
  },
];

describe('PrmReferencePointResolver', () => {
  let referencePointServiceSpy: Mocked<Pick<ReferencePointService, 'getReferencePointVersions'>>;

  beforeEach(() => {
    referencePointServiceSpy = {
      getReferencePointVersions: vi.fn(),
    };
    referencePointServiceSpy.getReferencePointVersions.mockReturnValue(of(referencePoint));

    TestBed.configureTestingModule({
      imports: [AppTestingModule],
      providers: [
        {
          provide: ReferencePointService,
          useValue: referencePointServiceSpy,
        },
      ],
    });
  });

  it('should get reference-point from prm-directory', async () => {
    const mockRoute = {
      paramMap: convertToParamMap({ sloid: 'ch:1:sloid:12345:1' }),
    } as ActivatedRouteSnapshot;

    const result = TestBed.runInInjectionContext(() =>
      referencePointResolver(mockRoute, {} as RouterStateSnapshot)
    ) as Observable<ReadReferencePointVersion[]>;

    const versions = await firstValueFrom(result);
    expect(versions.length).toBe(1);
    expect(versions[0].sloid).toBe('ch:1:sloid:12345:1');
  });
});
