import { TestBed } from '@angular/core/testing';
import { SectorPermissionService } from './sector-permission.service';
import { PermissionService } from '../../../core/auth/permission/permission.service';
import { adminPermissionServiceMock } from '../../../app.testing.mocks';
import { MeanOfTransport } from '../../../api';

describe('SectorPermissionService', () => {
  let sectorPermissionService: SectorPermissionService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        SectorPermissionService,
        { provide: PermissionService, useValue: adminPermissionServiceMock },
      ],
    });
    sectorPermissionService = TestBed.inject(SectorPermissionService);
  });

  it('should create', () => {
    expect(sectorPermissionService).toBeTruthy();
  });

  it('should show create buttons if service point has mot train', () => {
    const showCreateButton = sectorPermissionService.showCreateButton([
      {
        status: 'VALIDATED',
        designationOfficial: '',
        businessOrganisation: '',
        validFrom: new Date('2020-01-01'),
        validTo: new Date('2020-01-01'),
        number: {
          number: 857000,
          numberShort: 7000,
          uicCountryCode: 85,
          checkDigit: 0,
        },
        country: 'SWITZERLAND',
        meansOfTransport: [MeanOfTransport.Train],
      },
    ]);
    expect(showCreateButton).toBeTrue();
  });

  it('should not show create buttons if service point has no version with mot train', () => {
    const showCreateButton = sectorPermissionService.showCreateButton([
      {
        status: 'VALIDATED',
        designationOfficial: '',
        businessOrganisation: '',
        validFrom: new Date('2020-01-01'),
        validTo: new Date('2020-01-01'),
        number: {
          number: 857000,
          numberShort: 7000,
          uicCountryCode: 85,
          checkDigit: 0,
        },
        country: 'SWITZERLAND',
        meansOfTransport: [MeanOfTransport.Bus],
      },
    ]);
    expect(showCreateButton).toBeFalse();
  });
});
