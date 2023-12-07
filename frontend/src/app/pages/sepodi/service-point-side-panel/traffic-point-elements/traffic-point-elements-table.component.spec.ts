import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TrafficPointElementsTableComponent } from './traffic-point-elements-table.component';
import { AuthService } from '../../../../core/auth/auth.service';
import { AppTestingModule } from '../../../../app.testing.module';
import { MockAtlasButtonComponent, MockTableComponent } from '../../../../app.testing.mocks';
import { TrafficPointElementsService } from '../../../../api';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';
import { BERN_WYLEREGG_TRAFFIC_POINTS_CONTAINER } from '../../../../../test/data/traffic-point-element';

describe('TrafficPointElementsTableComponent', () => {
  let component: TrafficPointElementsTableComponent;
  let fixture: ComponentFixture<TrafficPointElementsTableComponent>;

  const authService: Partial<AuthService> = {};
  const trafficPointElementsService = jasmine.createSpyObj('TrafficPointElementsService', [
    'getPlatformsOfServicePoint',
  ]);
  trafficPointElementsService.getPlatformsOfServicePoint.and.returnValue(
    of(BERN_WYLEREGG_TRAFFIC_POINTS_CONTAINER),
  );
  const activatedRouteMock = {
    parent: { snapshot: { params: { id: 8507000 } } },
    data: of({ isTrafficPointArea: false }),
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [
        TrafficPointElementsTableComponent,
        MockAtlasButtonComponent,
        MockTableComponent,
      ],
      imports: [AppTestingModule],
      providers: [
        { provide: AuthService, useValue: authService },
        { provide: TrafficPointElementsService, useValue: trafficPointElementsService },
        { provide: ActivatedRoute, useValue: activatedRouteMock },
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
});
