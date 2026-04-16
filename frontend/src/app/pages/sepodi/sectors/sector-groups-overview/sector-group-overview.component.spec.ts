import { ComponentFixture, TestBed } from '@angular/core/testing';
import { beforeEach, describe, expect, it, vi, type Mocked } from 'vitest';

import { SectorGroupOverviewComponent } from './sector-group-overview.component';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { SectorGroupInternalService } from '../../../../api/service/sepodi/sector-group-internal.service';
import { TranslateModule } from '@ngx-translate/core';
import { BehaviorSubject, of } from 'rxjs';
import { BERN } from '../../../../../test/data/service-point';
import { PermissionService } from '../../../../core/auth/permission/permission.service';
import { adminPermissionServiceMock } from '../../../../app.testing.mocks';
import { ContainerReadSectorVersion } from '../../../../api/model/containerReadSectorVersion';
import { SectorInternalService } from '../../../../api/service/sepodi/sector-internal.service';

describe('SectorGroupOverviewComponent', () => {
  let component: SectorGroupOverviewComponent;
  let fixture: ComponentFixture<SectorGroupOverviewComponent>;

  let sectorGroupInternalServiceSpy: Mocked<Pick<SectorGroupInternalService, 'getSectorGroups'>>;
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

  const sectorGroupOverview: ContainerReadSectorVersion = {
    objects: [],
    totalCount: 0,
  };

  const subject = new BehaviorSubject<ContainerReadSectorVersion>({
    totalCount: 1,
    objects: [],
  });

  beforeEach(async () => {
    sectorGroupInternalServiceSpy = {
      getSectorGroups: vi.fn(),
    };
    sectorGroupInternalServiceSpy.getSectorGroups.mockReturnValue(of(sectorGroupOverview));

    sectorInternalServiceSpy = {
      getSectors: vi.fn(),
    };
    sectorInternalServiceSpy.getSectors.mockReturnValue(subject.asObservable());

    routerSpy = {
      navigate: vi.fn(),
    };
    routerSpy.navigate.mockReturnValue(Promise.resolve(true));

    await TestBed.configureTestingModule({
      imports: [SectorGroupOverviewComponent, TranslateModule.forRoot()],
      providers: [
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
        { provide: ActivatedRoute, useValue: activatedRouteMock },
        { provide: PermissionService, useValue: adminPermissionServiceMock },
        { provide: Router, useValue: routerSpy },
      ],
    }).compileComponents();

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
});
