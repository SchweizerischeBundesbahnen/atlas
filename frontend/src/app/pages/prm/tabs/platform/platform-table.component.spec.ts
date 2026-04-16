import { ComponentFixture, TestBed } from '@angular/core/testing';
import { beforeEach, describe, expect, it, type Mocked, vi } from 'vitest';

import { PlatformTableComponent } from './platform-table.component';
import {
  MockAtlasButtonComponent,
  MockNavigationSepodiPrmComponent,
  MockTableComponent,
} from '../../../../app.testing.mocks';
import { ActivatedRoute, Router } from '@angular/router';
import { STOP_POINT } from '../../util/stop-point-test-data';
import { BERN_WYLEREGG } from '../../../../../test/data/service-point';
import { of } from 'rxjs';
import { BERN_WYLEREGG_TRAFFIC_POINTS_CONTAINER } from '../../../../../test/data/traffic-point-element';
import { AtlasButtonComponent } from '../../../../core/components/button/atlas-button.component';
import { TableComponent } from '../../../../core/components/table/table.component';
import { NavigationSepodiPrmComponent } from '../../../../core/navigation-sepodi-prm/navigation-sepodi-prm.component';
import { TrafficPointElementInternalService } from '../../../../api/service/sepodi/traffic-point-element-internal.service';
import { PlatformInternalService } from '../../../../api/service/prm/platform/platform-internal.service';

describe('PlatformTableComponent', () => {
  let component: PlatformTableComponent;
  let fixture: ComponentFixture<PlatformTableComponent>;
  let routerSpy: Mocked<Pick<Router, 'navigate'>>;
  let platformInternalService: Mocked<Pick<PlatformInternalService, 'getPlatformOverview'>>;
  let trafficPointElementInternalService: Mocked<
    Pick<TrafficPointElementInternalService, 'getPlatformsOfServicePoint'>
  >;

  const activatedRouteMock = {
    parent: {
      snapshot: {
        params: { stopPointSloid: STOP_POINT.sloid },
        data: { stopPoints: [STOP_POINT], servicePoints: [BERN_WYLEREGG] },
      },
    },
  };

  beforeEach(() => {
    routerSpy = {
      navigate: vi.fn(),
    };
    routerSpy.navigate.mockResolvedValue(true);

    platformInternalService = {
      getPlatformOverview: vi.fn(),
    };
    platformInternalService.getPlatformOverview.mockReturnValue(of([]));

    trafficPointElementInternalService = {
      getPlatformsOfServicePoint: vi.fn(),
    };
    trafficPointElementInternalService.getPlatformsOfServicePoint.mockReturnValue(
      of(BERN_WYLEREGG_TRAFFIC_POINTS_CONTAINER)
    );

    TestBed.configureTestingModule({
      imports: [PlatformTableComponent],
      providers: [
        { provide: ActivatedRoute, useValue: activatedRouteMock },
        {
          provide: PlatformInternalService,
          useValue: platformInternalService,
        },
        {
          provide: TrafficPointElementInternalService,
          useValue: trafficPointElementInternalService,
        },
        { provide: Router, useValue: routerSpy },
      ],
    }).overrideComponent(PlatformTableComponent, {
      remove: {
        imports: [AtlasButtonComponent, TableComponent, NavigationSepodiPrmComponent],
      },
      add: {
        imports: [MockAtlasButtonComponent, MockTableComponent, MockNavigationSepodiPrmComponent],
      },
    });

    fixture = TestBed.createComponent(PlatformTableComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load table', () => {
    component.getOverview({ page: 0, size: 10 });

    expect(component.platforms.length).toBe(2);
    expect(component.platforms[0].completion).toBe('NOT_STARTED');
  });

  it('should navigate to platform on table click', () => {
    component.getOverview({ page: 0, size: 10 });

    component.rowClicked(component.platforms[0]);
    expect(routerSpy.navigate).toHaveBeenCalled();
  });
});
