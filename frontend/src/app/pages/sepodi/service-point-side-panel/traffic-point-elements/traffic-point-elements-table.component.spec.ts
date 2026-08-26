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
  BERN_TRAFFIC_POINT_PLATFORM_1,
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
import { ReadTrafficPointElementVersion } from '../../../../api';

describe('TrafficPointElementsTableComponent', () => {
  let component: TrafficPointElementsTableComponent;
  let fixture: ComponentFixture<TrafficPointElementsTableComponent>;
  let routerSpy: Mocked<Pick<Router, 'navigate'>>;

  const authService: Partial<AuthService> = {};

  let trafficPointElementInternalServiceSpy: Mocked<
    Pick<TrafficPointElementInternalService, 'getPlatformsOfServicePoint'>
  >;
  let trafficPointMapServiceSpy: Mocked<
    Pick<TrafficPointMapService, 'highlightTrafficPoint' | 'clearHighlightedTrafficPoint'>
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
      highlightTrafficPoint: vi.fn(),
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
    expect(trafficPointMapServiceSpy.highlightTrafficPoint).toHaveBeenCalledWith(
      BERN_WYLEREGG_TRAFFIC_POINTS[0].trafficPointElementGeolocation!.wgs84
    );
  });

  it('should highlight the traffic point of a version valid in the past when its row is hovered', () => {
    // Given
    const pastVersion = BERN_TRAFFIC_POINT_PLATFORM_1[1] as unknown as ReadTrafficPointElementVersion;

    // When
    component.onRowHovered(pastVersion);

    // Then
    expect(trafficPointMapServiceSpy.highlightTrafficPoint).toHaveBeenCalledWith({
      north: 46.947853,
      east: 7.435045,
      spatialReference: 'WGS84',
    });
  });

  it('should highlight the traffic point of a version valid in the future when its row is hovered', () => {
    // Given
    const futureVersion = BERN_TRAFFIC_POINT_PLATFORM_1[2] as unknown as ReadTrafficPointElementVersion;

    // When
    component.onRowHovered(futureVersion);

    // Then
    expect(trafficPointMapServiceSpy.highlightTrafficPoint).toHaveBeenCalledWith({
      north: 46.94841167916,
      east: 7.43798616183,
      spatialReference: 'WGS84',
    });
  });

  it('should clear the highlight when a hovered version has no geolocation', () => {
    // Given
    const versionWithoutGeolocation = BERN_TRAFFIC_POINT_PLATFORM_1[0] as unknown as ReadTrafficPointElementVersion;

    // When
    component.onRowHovered(versionWithoutGeolocation);

    // Then
    expect(trafficPointMapServiceSpy.clearHighlightedTrafficPoint).toHaveBeenCalled();
    expect(trafficPointMapServiceSpy.highlightTrafficPoint).not.toHaveBeenCalled();
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
