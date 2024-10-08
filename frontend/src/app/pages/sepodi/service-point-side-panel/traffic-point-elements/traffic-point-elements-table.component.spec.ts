import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TrafficPointElementsTableComponent } from './traffic-point-elements-table.component';
import { AuthService } from '../../../../core/auth/auth.service';
import { AppTestingModule } from '../../../../app.testing.module';
import { MockAtlasButtonComponent, MockTableComponent } from '../../../../app.testing.mocks';
import { TrafficPointElementsService } from '../../../../api';
import {ActivatedRoute, Router} from '@angular/router';
import { of } from 'rxjs';
import { BERN_WYLEREGG_TRAFFIC_POINTS_CONTAINER } from '../../../../../test/data/traffic-point-element';
import {DetailPageContainerComponent} from "../../../../core/components/detail-page-container/detail-page-container.component";
import {DetailPageContentComponent} from "../../../../core/components/detail-page-content/detail-page-content.component";
import {DetailFooterComponent} from "../../../../core/components/detail-footer/detail-footer.component";
import SpyObj = jasmine.SpyObj;
import {Pages} from "../../../pages";
import {BERN_WYLEREGG} from "../../../../../test/data/service-point";

describe('TrafficPointElementsTableComponent', () => {
  let component: TrafficPointElementsTableComponent;
  let fixture: ComponentFixture<TrafficPointElementsTableComponent>;
  let routerSpy: SpyObj<Router>;

  const authService: Partial<AuthService> = {};
  const trafficPointElementsService = jasmine.createSpyObj('TrafficPointElementsService', [
    'getPlatformsOfServicePoint',
  ]);
  trafficPointElementsService.getPlatformsOfServicePoint.and.returnValue(
    of(BERN_WYLEREGG_TRAFFIC_POINTS_CONTAINER),
  );
  const activatedRouteMock = {
    parent: {
      snapshot: {
        params: {
          id: 8507000
        },
        data: {
          servicePoint: [BERN_WYLEREGG],
        }
      }

    },
    data: of({
      isTrafficPointArea: false,
    }),
  };

  beforeEach(async () => {
    routerSpy = jasmine.createSpyObj(['navigate']);

    await TestBed.configureTestingModule({
      declarations: [
        TrafficPointElementsTableComponent,
        MockAtlasButtonComponent,
        MockTableComponent,
        DetailPageContainerComponent,
        DetailPageContentComponent,
        DetailFooterComponent,
      ],
      imports: [AppTestingModule],
      providers: [
        { provide: AuthService, useValue: authService },
        { provide: TrafficPointElementsService, useValue: trafficPointElementsService },
        { provide: ActivatedRoute, useValue: activatedRouteMock },
        {provide: Router, useValue: routerSpy},

      ],
    }).compileComponents();

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

    expect(trafficPointElementsService.getPlatformsOfServicePoint).toHaveBeenCalledOnceWith(
      8507000,
      0,
      10,
      ['designation,asc'],
    );
  });

  it('should navigate to the correct platforms url', () => {
    component.navigateToPlatforms();

    expect(routerSpy.navigate).toHaveBeenCalledWith([
      '/',
      Pages.PRM.path,
      Pages.STOP_POINTS.path,
      'ch:1:sloid:89008',
      Pages.PLATFORMS.path
    ]);
  });
});
