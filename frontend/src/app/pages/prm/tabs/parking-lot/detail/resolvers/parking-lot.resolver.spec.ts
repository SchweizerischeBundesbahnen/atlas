import { TestBed } from '@angular/core/testing';
import { beforeEach, describe, expect, it, type Mocked, vi } from 'vitest';
import { firstValueFrom, Observable, of } from 'rxjs';
import { ActivatedRouteSnapshot, convertToParamMap, RouterStateSnapshot } from '@angular/router';
import { parkingLotResolver } from './parking-lot.resolver';
import { BooleanOptionalAttributeType, ReadParkingLotVersion } from '../../../../../../api';
import { AppTestingModule } from '../../../../../../app.testing.module';
import { ParkingLotService } from '../../../../../../api/service/prm/parking-lot/parking-lot.service';

const parkingLot: ReadParkingLotVersion[] = [
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
    placesAvailable: BooleanOptionalAttributeType.ToBeCompleted,
    prmPlacesAvailable: BooleanOptionalAttributeType.ToBeCompleted,
    number: {
      number: 8507000,
      numberShort: 7000,
      uicCountryCode: 85,
      checkDigit: 3,
    },
  },
];

describe('PrmParkingLotResolver', () => {
  let parkingLotServiceSpy: Mocked<Pick<ParkingLotService, 'getParkingLotVersions'>>;

  beforeEach(() => {
    parkingLotServiceSpy = {
      getParkingLotVersions: vi.fn(),
    };
    parkingLotServiceSpy.getParkingLotVersions.mockReturnValue(of(parkingLot));

    TestBed.configureTestingModule({
      imports: [AppTestingModule],
      providers: [
        {
          provide: ParkingLotService,
          useValue: parkingLotServiceSpy,
        },
      ],
    });
  });

  it('should get parkingLot from prm-directory', async () => {
    const mockRoute = {
      paramMap: convertToParamMap({ sloid: 'ch:1:sloid:12345:1' }),
    } as ActivatedRouteSnapshot;

    const result = TestBed.runInInjectionContext(() =>
      parkingLotResolver(mockRoute, {} as RouterStateSnapshot)
    ) as Observable<ReadParkingLotVersion[]>;

    const versions = await firstValueFrom(result);
    expect(versions.length).toBe(1);
    expect(versions[0].sloid).toBe('ch:1:sloid:12345:1');
  });
});
