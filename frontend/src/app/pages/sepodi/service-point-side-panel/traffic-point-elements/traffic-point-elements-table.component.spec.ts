import { ComponentFixture, TestBed } from '@angular/core/testing';
import { beforeEach, describe, expect, it, type Mocked, vi } from 'vitest';
import { TrafficPointElementsTableComponent } from './traffic-point-elements-table.component';
import { AuthService } from '../../../../core/auth/auth.service';
import {
  MockAtlasButtonComponent,
  MockNavigationSepodiPrmComponent,
  MockTableComponent,
  translateServiceProvider,
} from '../../../../app.testing.mocks';
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
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { TrafficPointElementInternalService } from '../../../../api/service/sepodi/traffic-point-element-internal.service';
import { TrafficPointMapService } from '../../map/traffic-point-map.service';

describe('TrafficPointElementsTableComponent', () => {
  let component: TrafficPointElementsTableComponent;
  let fixture: ComponentFixture<TrafficPointElementsTableComponent>;
  let routerSpy: Mocked<Pick<Router, 'navigate'>>;

  const authService: Partial<AuthService> = {};

  let trafficPointElementInternalServiceSpy: Mocked<
    Pick<TrafficPointElementInternalService, 'getPlatformsOfServicePoint'>
  >;
  let trafficPointMapServiceSpy: Mocked<
    Pick<TrafficPointMapService, 'highlightTrafficPointBySloid' | 'clearHighlightedTrafficPoint'>
  >;

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

  beforeEach(() => {
    trafficPointElementInternalServiceSpy = {
      getPlatformsOfServicePoint: vi.fn(),
    };
    trafficPointElementInternalServiceSpy.getPlatformsOfServicePoint.mockReturnValue(
      of(BERN_WYLEREGG_TRAFFIC_POINTS_CONTAINER)
    );

    routerSpy = {
      navigate: vi.fn(),
    };
    routerSpy.navigate.mockReturnValue(Promise.resolve(true));

    trafficPointMapServiceSpy = {
      highlightTrafficPointBySloid: vi.fn(),
      clearHighlightedTrafficPoint: vi.fn(),
    };

    TestBed.configureTestingModule({
      imports: [TrafficPointElementsTableComponent],
      providers: [
        translateServiceProvider,
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: AuthService, useValue: authService },
        {
          provide: TrafficPointElementInternalService,
          useValue: trafficPointElementInternalServiceSpy,
        },
        {
          provide: TrafficPointMapService,
          useValue: trafficPointMapServiceSpy,
        },
        { provide: ActivatedRoute, useValue: activatedRouteMock },
        { provide: Router, useValue: routerSpy },
      ],
    }).overrideComponent(TrafficPointElementsTableComponent, {
      remove: {
        imports: [AtlasButtonComponent, TableComponent, NavigationSepodiPrmComponent],
      },
      add: {
        imports: [MockAtlasButtonComponent, MockTableComponent, MockNavigationSepodiPrmComponent],
      },
    });

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

    expect(trafficPointElementInternalServiceSpy.getPlatformsOfServicePoint).toHaveBeenCalledWith(8507000, 0, 10, [
      'designation,asc',
    ]);
  });

  it('should navigate to add trafficpoint', () => {
    component.addNewTrafficPointElement();
    expect(routerSpy.navigate).toHaveBeenCalledWith(['add'], expect.any(Object));
  });

  it('should navigate to edit trafficpoint', () => {
    component.editVersion(BERN_WYLEREGG_TRAFFIC_POINTS[0]);
    expect(routerSpy.navigate).toHaveBeenCalledWith(['ch:1:sloid:89008:0:1'], expect.any(Object));
  });

  it('should highlight the traffic point on the map when a row is hovered', () => {
    component.onRowHovered(BERN_WYLEREGG_TRAFFIC_POINTS[0]);
    expect(trafficPointMapServiceSpy.highlightTrafficPointBySloid).toHaveBeenCalledWith('ch:1:sloid:89008:0:1');
  });

  it('should clear the highlighted traffic point on the map when a row is no longer hovered', () => {
    component.onRowHoverEnded();
    expect(trafficPointMapServiceSpy.clearHighlightedTrafficPoint).toHaveBeenCalled();
  });

  it('should clear the highlighted traffic point on destroy', () => {
    component.ngOnDestroy();
    expect(trafficPointMapServiceSpy.clearHighlightedTrafficPoint).toHaveBeenCalled();
  });
});
