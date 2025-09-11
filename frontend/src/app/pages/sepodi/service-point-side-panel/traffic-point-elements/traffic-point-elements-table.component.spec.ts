import { ComponentFixture, TestBed } from '@angular/core/testing';
import { TrafficPointElementsTableComponent } from './traffic-point-elements-table.component';
import { AuthService } from '../../../../core/auth/auth.service';
import { MockAtlasButtonComponent, MockNavigationSepodiPrmComponent, MockTableComponent, } from '../../../../app.testing.mocks';
import { ActivatedRoute, Router } from '@angular/router';
import { of } from 'rxjs';
import {
  BERN_WYLEREGG_TRAFFIC_POINTS,
  BERN_WYLEREGG_TRAFFIC_POINTS_CONTAINER,
} from '../../../../../test/data/traffic-point-element';
import { BERN_WYLEREGG } from '../../../../../test/data/service-point';
import { AtlasButtonComponent } from '../../../../core/components/button/atlas-button.component';
import { TableComponent } from '../../../../core/components/table/table.component';
import { NavigationSepodiPrmComponent } from '../../../../core/navigation-sepodi-prm/navigation-sepodi-prm.component';
import { TranslateModule } from '@ngx-translate/core';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { TrafficPointElementInternalService } from '../../../../api/service/sepodi/traffic-point-element-internal.service';
import SpyObj = jasmine.SpyObj;

describe('TrafficPointElementsTableComponent', () => {
  let component: TrafficPointElementsTableComponent;
  let fixture: ComponentFixture<TrafficPointElementsTableComponent>;
  let routerSpy: SpyObj<Router>;

  const authService: Partial<AuthService> = {};
  const trafficPointElementInternalService = jasmine.createSpyObj(
    'TrafficPointElementsService',
    ['getPlatformsOfServicePoint']
  );
  trafficPointElementInternalService.getPlatformsOfServicePoint.and.returnValue(
    of(BERN_WYLEREGG_TRAFFIC_POINTS_CONTAINER)
  );
  const activatedRouteMock = {
    parent: {
      snapshot: {
        params: {
          servicePointNumber: 8507000,
        },
        data: {
          servicePoint: [BERN_WYLEREGG],
        },
      },
    },
    data: of({
      isTrafficPointArea: false,
    }),
  };

  beforeEach(async () => {
    routerSpy = jasmine.createSpyObj(['navigate']);
    routerSpy.navigate.and.returnValue(Promise.resolve(true));

    await TestBed.configureTestingModule({
      imports: [TrafficPointElementsTableComponent, TranslateModule.forRoot()],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: AuthService, useValue: authService },
        {
          provide: TrafficPointElementInternalService,
          useValue: trafficPointElementInternalService,
        },
        { provide: ActivatedRoute, useValue: activatedRouteMock },
        { provide: Router, useValue: routerSpy },
      ],
    })
      .overrideComponent(TrafficPointElementsTableComponent, {
        remove: {
          imports: [
            AtlasButtonComponent,
            TableComponent,
            NavigationSepodiPrmComponent,
          ],
        },
        add: {
          imports: [
            MockAtlasButtonComponent,
            MockTableComponent,
            MockNavigationSepodiPrmComponent,
          ],
        },
      })
      .compileComponents();

    fixture = TestBed.createComponent(TrafficPointElementsTableComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display platform data', () => {
    component.getOverview({
      page: 0,
      size: 10,
    });

    expect(
      trafficPointElementInternalService.getPlatformsOfServicePoint
    ).toHaveBeenCalledOnceWith(8507000, 0, 10, ['designation,asc']);
  });

  it('should navigate to add trafficpoint', () => {
    component.addNewTrafficPointElement();
    expect(routerSpy.navigate).toHaveBeenCalledOnceWith(
      ['add'],
      jasmine.any(Object)
    );
  });

  it('should navigate to edit trafficpoint', () => {
    component.editVersion(BERN_WYLEREGG_TRAFFIC_POINTS[0]);
    expect(routerSpy.navigate).toHaveBeenCalledOnceWith(
      ['ch:1:sloid:89008:0:1'],
      jasmine.any(Object)
    );
  });
});
