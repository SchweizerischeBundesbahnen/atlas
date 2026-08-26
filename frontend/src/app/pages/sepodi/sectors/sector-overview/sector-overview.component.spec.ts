import { ComponentFixture, TestBed } from '@angular/core/testing';
import { beforeEach, describe, expect, it, type Mocked, vi } from 'vitest';

import { SectorOverviewComponent } from './sector-overview.component';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { SectorInternalService } from '../../../../api/service/sepodi/sector-internal.service';
import { of } from 'rxjs';
import { ContainerReadSectorVersion } from '../../../../api/model/containerReadSectorVersion';
import { SpatialReference } from '../../../../api';
import { BERN } from '../../../../../test/data/service-point';
import { PermissionService } from '../../../../core/auth/permission/permission.service';
import { adminPermissionServiceMock, translateServiceProvider } from '../../../../app.testing.mocks';
import { SectorMapService } from '../../map/sector-map.service';
import { ReadSectorVersion } from '../../../../api/model/readSectorVersion';

describe('SectorOverviewComponent', () => {
  let component: SectorOverviewComponent;
  let fixture: ComponentFixture<SectorOverviewComponent>;

  let sectorInternalServiceSpy: Mocked<Pick<SectorInternalService, 'getSectors'>>;
  let sectorMapServiceSpy: Mocked<Pick<SectorMapService, 'highlightSector' | 'clearHighlightedSector'>>;
  let routerSpy: Mocked<Pick<Router, 'navigate'>>;

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

  const sectorOverview: ContainerReadSectorVersion = {
    objects: [],
    totalCount: 0,
  };

  const SECTOR_A: ReadSectorVersion = {
    trafficPointSloid: 'ch:1:sloid:7000:1',
    validFrom: new Date('2014-12-14'),
    validTo: new Date('2014-12-14'),
    designation: 'A',
    sectorGeolocation: {
      lv95: {
        north: 0,
        east: 0,
        spatialReference: SpatialReference.Lv95,
      },
      spatialReference: 'WGS84WEB',
      wgs84: {
        north: 0,
        east: 0,
        spatialReference: SpatialReference.Wgs84,
      },
      lv03: {
        north: 0,
        east: 0,
        spatialReference: SpatialReference.Lv03,
      },
    },
    sloid: 'ch:1:sloid:7000:1:1',
  };

  const SECTOR_A_PAST_VERSION: ReadSectorVersion = {
    ...SECTOR_A,
    validFrom: new Date('2014-12-14'),
    validTo: new Date('2019-12-31'),
    sectorGeolocation: {
      ...SECTOR_A.sectorGeolocation!,
      wgs84: {
        north: 46.947853,
        east: 7.435045,
        spatialReference: SpatialReference.Wgs84,
      },
    },
  };

  const SECTOR_A_FUTURE_VERSION: ReadSectorVersion = {
    ...SECTOR_A,
    validFrom: new Date('2099-01-01'),
    validTo: new Date('2099-12-31'),
    sectorGeolocation: {
      ...SECTOR_A.sectorGeolocation!,
      wgs84: {
        north: 46.94841167916,
        east: 7.43798616183,
        spatialReference: SpatialReference.Wgs84,
      },
    },
  };

  beforeEach(() => {
    sectorInternalServiceSpy = {
      getSectors: vi.fn(),
    };
    sectorInternalServiceSpy.getSectors.mockReturnValue(of(sectorOverview));

    sectorMapServiceSpy = {
      highlightSector: vi.fn(),
      clearHighlightedSector: vi.fn(),
    };

    routerSpy = {
      navigate: vi.fn(),
    };
    routerSpy.navigate.mockReturnValue(Promise.resolve(true));

    TestBed.configureTestingModule({
      imports: [SectorOverviewComponent],
      providers: [
        translateServiceProvider,
        provideHttpClient(),
        provideHttpClientTesting(),
        {
          provide: SectorInternalService,
          useValue: sectorInternalServiceSpy,
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

    fixture = TestBed.createComponent(SectorOverviewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
    expect(component.showCreateButtons).toBe(true);
  });

  it('should display sector data', () => {
    component.getSectorOverview({
      page: 0,
      size: 10,
    });

    expect(sectorInternalServiceSpy.getSectors).toHaveBeenCalledWith('ch:1:sloid:7000:1', 0, 10, ['designation,asc']);
  });

  it('should navigate to sector detail', () => {
    component.editSector(SECTOR_A);

    expect(routerSpy.navigate).toHaveBeenCalledWith(['ch:1:sloid:7000:1:1'], expect.any(Object));
  });

  it('should navigate back to service point', () => {
    component.backToServicePoint();

    expect(routerSpy.navigate).toHaveBeenCalledWith(['../..'], expect.any(Object));
  });

  it('should navigate to add sector', () => {
    component.addSector();

    expect(routerSpy.navigate).toHaveBeenCalledWith(['add'], expect.any(Object));
  });

  it('should highlight the sector on the map when a row is hovered', () => {
    component.onRowHovered({ ...SECTOR_A });

    expect(sectorMapServiceSpy.highlightSector).toHaveBeenCalledWith(SECTOR_A.sectorGeolocation!.wgs84);
  });

  it('should highlight the sector of a version valid in the past when its row is hovered', () => {
    // When
    component.onRowHovered({ ...SECTOR_A_PAST_VERSION });

    // Then
    expect(sectorMapServiceSpy.highlightSector).toHaveBeenCalledWith({
      north: 46.947853,
      east: 7.435045,
      spatialReference: SpatialReference.Wgs84,
    });
  });

  it('should highlight the sector of a version valid in the future when its row is hovered', () => {
    // When
    component.onRowHovered({ ...SECTOR_A_FUTURE_VERSION });

    // Then
    expect(sectorMapServiceSpy.highlightSector).toHaveBeenCalledWith({
      north: 46.94841167916,
      east: 7.43798616183,
      spatialReference: SpatialReference.Wgs84,
    });
  });

  it('should clear the highlight when a hovered sector version has no geolocation', () => {
    // Given
    const sectorWithoutGeolocation: ReadSectorVersion = { ...SECTOR_A, sectorGeolocation: undefined };

    // When
    component.onRowHovered(sectorWithoutGeolocation);

    // Then
    expect(sectorMapServiceSpy.clearHighlightedSector).toHaveBeenCalled();
    expect(sectorMapServiceSpy.highlightSector).not.toHaveBeenCalled();
  });

  it('should clear the highlighted sector on the map when a row is no longer hovered', () => {
    component.onRowHoverEnded();

    expect(sectorMapServiceSpy.clearHighlightedSector).toHaveBeenCalled();
  });

  it('should clear the highlighted sector on destroy', () => {
    component.ngOnDestroy();

    expect(sectorMapServiceSpy.clearHighlightedSector).toHaveBeenCalled();
  });
});
