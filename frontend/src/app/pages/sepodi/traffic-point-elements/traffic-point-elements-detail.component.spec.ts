import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TrafficPointElementsDetailComponent } from './traffic-point-elements-detail.component';
import { ActivatedRoute } from '@angular/router';
import { AppTestingModule } from '../../../app.testing.module';
import { DisplayDatePipe } from '../../../core/pipe/display-date.pipe';
import { of } from 'rxjs';
import { BERN_WYLEREGG } from '../service-point-test-data';
import { AuthService } from '../../../core/auth/auth.service';
import { MockAtlasButtonComponent } from '../../../app.testing.mocks';
import { DateRangeTextComponent } from '../../../core/versioning/date-range-text/date-range-text.component';
import { SplitServicePointNumberPipe } from '../search-service-point/split-service-point-number.pipe';
import { BERN_WYLEREGG_TRAFFIC_POINTS } from '../traffic-point-element-test-data';

const authService: Partial<AuthService> = {};

describe('TrafficPointElementsDetailComponent', () => {
  let component: TrafficPointElementsDetailComponent;
  let fixture: ComponentFixture<TrafficPointElementsDetailComponent>;

  const activatedRouteMock = { data: of({ trafficPoint: [BERN_WYLEREGG_TRAFFIC_POINTS[0]] }) };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [
        TrafficPointElementsDetailComponent,
        DisplayDatePipe,
        SplitServicePointNumberPipe,
        MockAtlasButtonComponent,
        DateRangeTextComponent,
      ],
      imports: [AppTestingModule],
      providers: [
        { provide: AuthService, useValue: authService },
        { provide: ActivatedRoute, useValue: activatedRouteMock },
        SplitServicePointNumberPipe,
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(TrafficPointElementsDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display current designationOfficial and validity', () => {
    expect(component.selectedVersion).toBeTruthy();

    expect(component.selectedVersion.designationOperational).toEqual('1');
    expect(component.maxValidity.validFrom).toEqual(new Date('2019-07-22'));
    expect(component.maxValidity.validTo).toEqual(new Date('2099-12-31'));
  });
});
