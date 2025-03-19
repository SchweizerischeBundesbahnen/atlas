import { ComponentFixture, TestBed } from '@angular/core/testing';
import { PlatformTableComponent } from './platform-table.component';
import {
  MockAtlasButtonComponent,
  MockNavigationSepodiPrmComponent,
  MockTableComponent,
} from '../../../../app.testing.mocks';
import { ActivatedRoute, Router } from '@angular/router';
import { STOP_POINT } from '../../util/stop-point-test-data.spec';
import { BERN_WYLEREGG } from '../../../../../test/data/service-point';
import {
  PersonWithReducedMobilityService,
  TrafficPointElementsService,
} from '../../../../api';
import { of } from 'rxjs';
import { BERN_WYLEREGG_TRAFFIC_POINTS_CONTAINER } from '../../../../../test/data/traffic-point-element';
import { AtlasButtonComponent } from '../../../../core/components/button/atlas-button.component';
import { TableComponent } from '../../../../core/components/table/table.component';
import { NavigationSepodiPrmComponent } from '../../../../core/navigation-sepodi-prm/navigation-sepodi-prm.component';
import SpyObj = jasmine.SpyObj;

describe('PlatformTableComponent', () => {
  let component: PlatformTableComponent;
  let fixture: ComponentFixture<PlatformTableComponent>;
  let routerSpy: SpyObj<Router>;

  const personWithReducedMobilityService = jasmine.createSpyObj(
    'personWithReducedMobilityService',
    ['getPlatformOverview']
  );
  personWithReducedMobilityService.getPlatformOverview.and.returnValue(of([]));

  const trafficPointElementsService = jasmine.createSpyObj(
    'trafficPointElementsService',
    ['getPlatformsOfServicePoint']
  );
  trafficPointElementsService.getPlatformsOfServicePoint.and.returnValue(
    of(BERN_WYLEREGG_TRAFFIC_POINTS_CONTAINER)
  );

  const activatedRouteMock = {
    parent: {
      snapshot: {
        params: { stopPointSloid: STOP_POINT.sloid },
        data: { stopPoints: [STOP_POINT], servicePoints: [BERN_WYLEREGG] },
      },
    },
  };

  beforeEach(() => {
    routerSpy = jasmine.createSpyObj(['navigate']);

    TestBed.configureTestingModule({
      imports: [PlatformTableComponent],
      providers: [
        { provide: ActivatedRoute, useValue: activatedRouteMock },
        {
          provide: PersonWithReducedMobilityService,
          useValue: personWithReducedMobilityService,
        },
        {
          provide: TrafficPointElementsService,
          useValue: trafficPointElementsService,
        },
        { provide: Router, useValue: routerSpy },
      ],
    }).overrideComponent(PlatformTableComponent, {
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
    routerSpy.navigate.and.returnValue(Promise.resolve(true));

    component.getOverview({ page: 0, size: 10 });

    component.rowClicked(component.platforms[0]);
    expect(routerSpy.navigate).toHaveBeenCalled();
  });
});
