import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SectorOverviewComponent } from './sector-overview.component';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { SectorInternalService } from '../../../../api/service/sepodi/sector-internal.service';
import { SectorGroupInternalService } from '../../../../api/service/sepodi/sector-group-internal.service';
import { TranslateModule } from '@ngx-translate/core';
import { of } from 'rxjs';
import { ContainerSectorVersion } from '../../../../api/model/containerSectorVersion';
import { ContainerSectorGroupVersion } from '../../../../api/model/containerSectorGroupVersion';
import { SpatialReference } from '../../../../api';
import { BERN } from '../../../../../test/data/service-point';
import { PermissionService } from '../../../../core/auth/permission/permission.service';
import { adminPermissionServiceMock } from '../../../../app.testing.mocks';

describe('SectorOverview', () => {
  let component: SectorOverviewComponent;
  let fixture: ComponentFixture<SectorOverviewComponent>;

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

  const sectorInternalService = jasmine.createSpyObj('SectorInternalService', [
    'getSectors',
  ]);
  const sectorOverview: ContainerSectorVersion = {
    objects: [],
    totalCount: 0,
  };
  sectorInternalService.getSectors.and.returnValue(of(sectorOverview));

  const sectorGroupInternalService = jasmine.createSpyObj(
    'SectorGroupInternalService',
    ['getSectorGroups']
  );
  const sectorGroupOverview: ContainerSectorGroupVersion = {
    objects: [],
    totalCount: 0,
  };
  sectorGroupInternalService.getSectorGroups.and.returnValue(
    of(sectorGroupOverview)
  );

  const routerSpy = jasmine.createSpyObj(['navigate']);
  routerSpy.navigate.and.returnValue(Promise.resolve(true));

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SectorOverviewComponent, TranslateModule.forRoot()],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        {
          provide: SectorInternalService,
          useValue: sectorInternalService,
        },
        {
          provide: SectorGroupInternalService,
          useValue: sectorGroupInternalService,
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
    expect(component.showCreateButtons).toBeTrue();
  });

  it('should display sector data', () => {
    component.getSectorOverview({
      page: 0,
      size: 10,
    });

    expect(sectorInternalService.getSectors).toHaveBeenCalledWith(
      'ch:1:sloid:7000:1',
      0,
      10,
      ['designation,asc']
    );
  });

  it('should navigate to sector detail', () => {
    component.editSector({
      trafficPointSloid: 'ch:1:sloid:7000:1',
      validFrom: new Date('2014-12-14'),
      validTo: new Date('2014-12-14'),
      designation: 'A',
      north: 0,
      east: 0,
      spatialReference: SpatialReference.Lv95,
      sloid: 'ch:1:sloid:7000:1:1',
    });

    expect(routerSpy.navigate).toHaveBeenCalledWith(
      ['ch:1:sloid:7000:1:1'],
      jasmine.any(Object)
    );
  });

  it('should display sector group data', () => {
    component.getSectorGroupOverview({
      page: 0,
      size: 10,
    });

    expect(sectorGroupInternalService.getSectorGroups).toHaveBeenCalledWith(
      'ch:1:sloid:7000:1',
      0,
      10,
      ['designation,asc']
    );
  });

  it('should navigate to sector group detail', () => {
    component.editSectorGroup({
      trafficPointSloid: 'ch:1:sloid:7000:1',
      validFrom: new Date('2014-12-14'),
      validTo: new Date('2014-12-14'),
      designation: 'A',
      sloid: 'ch:1:sloid:7000:1:1',
    });

    expect(routerSpy.navigate).toHaveBeenCalledWith(
      ['../sector-groups', 'ch:1:sloid:7000:1:1'],
      jasmine.any(Object)
    );
  });
});
