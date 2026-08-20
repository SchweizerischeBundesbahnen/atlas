import { ComponentFixture, TestBed } from '@angular/core/testing';
import { beforeEach, describe, expect, it, type Mocked, vi } from 'vitest';

import { SectorGroupOverviewComponent } from './sector-group-overview.component';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { SectorGroupInternalService } from '../../../../api/service/sepodi/sector-group-internal.service';
import { BehaviorSubject, of, Subject } from 'rxjs';
import { BERN } from '../../../../../test/data/service-point';
import { PermissionService } from '../../../../core/auth/permission/permission.service';
import { adminPermissionServiceMock, translateServiceProvider } from '../../../../app.testing.mocks';
import { ContainerReadSectorVersion } from '../../../../api/model/containerReadSectorVersion';
import { SectorInternalService } from '../../../../api/service/sepodi/sector-internal.service';
import { SectorGroupService } from '../../../../api/service/sepodi/sector-group.service';
import { SectorMapService } from '../../map/sector-map.service';
import { ReadSectorGroupVersion } from '../../../../api/model/readSectorGroupVersion';
import { ReadSectorVersion } from '../../../../api/model/readSectorVersion';
import { BERN_PLATFORM_1_SECTOR_MULTIPLE } from '../../../../../test/data/sector';

describe('SectorGroupOverviewComponent', () => {
  let component: SectorGroupOverviewComponent;
  let fixture: ComponentFixture<SectorGroupOverviewComponent>;

  let sectorGroupInternalServiceSpy: Mocked<Pick<SectorGroupInternalService, 'getSectorGroups'>>;
  let sectorGroupServiceSpy: Mocked<Pick<SectorGroupService, 'getSectorsBySectorGroupSloid'>>;
  let sectorMapServiceSpy: Mocked<Pick<SectorMapService, 'highlightSectorsBySloids' | 'clearHighlightedSector'>>;
  let sectorInternalServiceSpy: Mocked<Pick<SectorInternalService, 'getSectors'>>;
  let routerSpy: Mocked<Pick<Router, 'navigate'>>;

  const SECTOR_GROUP_A: ReadSectorGroupVersion = {
    trafficPointSloid: 'ch:1:sloid:7000:1',
    validFrom: new Date('2014-12-14'),
    validTo: new Date('2014-12-14'),
    designation: 'A',
    sloid: 'ch:1:sloid:7000:1:1',
  };

  const activatedRouteMock = {
    parent: {
      snapshot: {
        data: {
          servicePoint: BERN,
        },
        params: {
          trafficPointSloid: 'ch:1:sloid:7000:1',
        },
      },
    },
  };

  const sectorGroupOverview: ContainerReadSectorVersion = {
    objects: [],
    totalCount: 0,
  };

  const subject = new BehaviorSubject<ContainerReadSectorVersion>({
    totalCount: 1,
    objects: [],
  });

  beforeEach(() => {
    sectorGroupInternalServiceSpy = {
      getSectorGroups: vi.fn(),
    };
    sectorGroupInternalServiceSpy.getSectorGroups.mockReturnValue(of(sectorGroupOverview));

    sectorInternalServiceSpy = {
      getSectors: vi.fn(),
    };
    sectorInternalServiceSpy.getSectors.mockReturnValue(subject.asObservable());

    sectorGroupServiceSpy = {
      getSectorsBySectorGroupSloid: vi.fn(),
    };
    sectorGroupServiceSpy.getSectorsBySectorGroupSloid.mockReturnValue(of([]));

    sectorMapServiceSpy = {
      highlightSectorsBySloids: vi.fn(),
      clearHighlightedSector: vi.fn(),
    };

    routerSpy = {
      navigate: vi.fn(),
    };
    routerSpy.navigate.mockReturnValue(Promise.resolve(true));

    TestBed.configureTestingModule({
      imports: [SectorGroupOverviewComponent],
      providers: [
        translateServiceProvider,
        provideHttpClient(),
        provideHttpClientTesting(),
        {
          provide: SectorGroupInternalService,
          useValue: sectorGroupInternalServiceSpy,
        },
        {
          provide: SectorInternalService,
          useValue: sectorInternalServiceSpy,
        },
        {
          provide: SectorGroupService,
          useValue: sectorGroupServiceSpy,
        },
        {
          provide: SectorMapService,
          useValue: sectorMapServiceSpy,
        },
        { provide: ActivatedRoute, useValue: activatedRouteMock },
        { provide: PermissionService, useValue: adminPermissionServiceMock },
        { provide: Router, useValue: routerSpy },
      ],
    });

    fixture = TestBed.createComponent(SectorGroupOverviewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
    expect(component.showCreateButtons).toBe(true);
  });

  it('should display sector group data', () => {
    component.getSectorGroupOverview({
      page: 0,
      size: 10,
    });

    expect(sectorGroupInternalServiceSpy.getSectorGroups).toHaveBeenCalledWith('ch:1:sloid:7000:1', 0, 10, [
      'designation,asc',
    ]);
  });

  it('should navigate to sector group detail', () => {
    component.editSectorGroup({
      trafficPointSloid: 'ch:1:sloid:7000:1',
      validFrom: new Date('2014-12-14'),
      validTo: new Date('2014-12-14'),
      designation: 'A',
      sloid: 'ch:1:sloid:7000:1:1',
    });

    expect(routerSpy.navigate).toHaveBeenCalledWith(['ch:1:sloid:7000:1:1'], expect.any(Object));
  });

  it('should navigate back to service point', () => {
    component.backToServicePoint();

    expect(routerSpy.navigate).toHaveBeenCalledWith(['../..'], expect.any(Object));
  });

  it('should navigate to add sector group', () => {
    component.addSectorGroup();

    expect(routerSpy.navigate).toHaveBeenCalledWith(['add'], expect.any(Object));
  });

  it('should set hasAtLeastTwoSectors to false ', () => {
    subject.next({ totalCount: 1, objects: [] });
    fixture.detectChanges();

    expect(component.hasAtLeastTwoSectors).toBe(false);
  });

  it('should set hasAtLeastTwoSectors to true ', () => {
    subject.next({ totalCount: 2, objects: [] });
    fixture.detectChanges();

    expect(component.hasAtLeastTwoSectors).toBe(true);
  });

  it('should highlight all sectors of the hovered sector group on the map', () => {
    // Given
    sectorGroupServiceSpy.getSectorsBySectorGroupSloid.mockReturnValue(of(BERN_PLATFORM_1_SECTOR_MULTIPLE));

    // When
    component.onRowHovered(SECTOR_GROUP_A);

    // Then
    expect(sectorGroupServiceSpy.getSectorsBySectorGroupSloid).toHaveBeenCalledWith('ch:1:sloid:7000:1:1');
    expect(sectorMapServiceSpy.highlightSectorsBySloids).toHaveBeenCalledWith([
      'ch:1:sloid:7000:1:1:1',
      'ch:1:sloid:7000:1:1:2',
    ]);
  });

  it('should clear the highlighted sectors when a row is no longer hovered', () => {
    // Given
    sectorGroupServiceSpy.getSectorsBySectorGroupSloid.mockReturnValue(of(BERN_PLATFORM_1_SECTOR_MULTIPLE));
    component.onRowHovered(SECTOR_GROUP_A);
    sectorMapServiceSpy.highlightSectorsBySloids.mockClear();

    // When
    component.onRowHoverEnded();

    // Then
    expect(sectorMapServiceSpy.highlightSectorsBySloids).toHaveBeenCalledWith([]);
  });

  it('should not highlight sectors of a sector group that is no longer hovered', () => {
    // Given
    const pendingSectors = new Subject<ReadSectorVersion[]>();
    sectorGroupServiceSpy.getSectorsBySectorGroupSloid.mockReturnValue(pendingSectors.asObservable());
    component.onRowHovered(SECTOR_GROUP_A);

    // When
    component.onRowHoverEnded();
    pendingSectors.next(BERN_PLATFORM_1_SECTOR_MULTIPLE);

    // Then
    expect(sectorMapServiceSpy.highlightSectorsBySloids).toHaveBeenCalledExactlyOnceWith([]);
  });

  it('should clear the highlighted sectors on destroy', () => {
    component.ngOnDestroy();

    expect(sectorMapServiceSpy.clearHighlightedSector).toHaveBeenCalled();
  });
});
