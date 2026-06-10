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
import { adminPermissionServiceMock } from '../../../../app.testing.mocks';

describe('SectorOverviewComponent', () => {
  let component: SectorOverviewComponent;
  let fixture: ComponentFixture<SectorOverviewComponent>;

  let sectorInternalServiceSpy: Mocked<Pick<SectorInternalService, 'getSectors'>>;
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

  beforeEach(async () => {
    sectorInternalServiceSpy = {
      getSectors: vi.fn(),
    };
    sectorInternalServiceSpy.getSectors.mockReturnValue(of(sectorOverview));

    routerSpy = {
      navigate: vi.fn(),
    };
    routerSpy.navigate.mockReturnValue(Promise.resolve(true));

    await TestBed.configureTestingModule({
      imports: [SectorOverviewComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        {
          provide: SectorInternalService,
          useValue: sectorInternalServiceSpy,
        },
        { provide: ActivatedRoute, useValue: activatedRouteMock },
        { provide: PermissionService, useValue: adminPermissionServiceMock },
        { provide: Router, useValue: routerSpy },
      ],
    }).compileComponents();

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
    component.editSector({
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
    });

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
});
