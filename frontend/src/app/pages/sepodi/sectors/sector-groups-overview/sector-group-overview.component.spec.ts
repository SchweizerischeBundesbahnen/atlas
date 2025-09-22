import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SectorGroupOverviewComponent } from './sector-group-overview.component';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { SectorGroupInternalService } from '../../../../api/service/sepodi/sector-group-internal.service';
import { TranslateModule } from '@ngx-translate/core';
import { of } from 'rxjs';
import { ContainerSectorGroupVersion } from '../../../../api/model/containerSectorGroupVersion';
import { BERN } from '../../../../../test/data/service-point';
import { PermissionService } from '../../../../core/auth/permission/permission.service';
import { adminPermissionServiceMock } from '../../../../app.testing.mocks';

describe('SectorGroupOverviewComponent', () => {
  let component: SectorGroupOverviewComponent;
  let fixture: ComponentFixture<SectorGroupOverviewComponent>;

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
      imports: [SectorGroupOverviewComponent, TranslateModule.forRoot()],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        {
          provide: SectorGroupInternalService,
          useValue: sectorGroupInternalService,
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
    expect(component.showCreateButtons).toBeTrue();
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
      ['ch:1:sloid:7000:1:1'],
      jasmine.any(Object)
    );
  });

  it('should navigate back to service point', () => {
    component.backToServicePoint();

    expect(routerSpy.navigate).toHaveBeenCalledWith(
      ['..', '..'],
      jasmine.any(Object)
    );
  });
});
