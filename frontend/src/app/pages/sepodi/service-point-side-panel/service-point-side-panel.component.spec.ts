import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ServicePointSidePanelComponent } from './service-point-side-panel.component';
import { ActivatedRoute } from '@angular/router';
import { AppTestingModule } from '../../../app.testing.module';
import { DisplayDatePipe } from '../../../core/pipe/display-date.pipe';
import { of } from 'rxjs';
import { AuthService } from '../../../core/auth/auth.service';
import { MockAtlasButtonComponent } from '../../../app.testing.mocks';
import { DateRangeTextComponent } from '../../../core/versioning/date-range-text/date-range-text.component';
import { SplitServicePointNumberPipe } from '../../../core/search-service-point/split-service-point-number.pipe';
import { TrafficPointMapService } from '../map/traffic-point-map.service';
import { BERN_WYLEREGG } from '../../../../test/data/service-point';

const authService: Partial<AuthService> = {};
const trafficPointMapService = jasmine.createSpyObj<TrafficPointMapService>([
  'displayTrafficPointsOnMap',
  'clearDisplayedTrafficPoints',
]);

describe('ServicePointSidePanelComponent', () => {
  let component: ServicePointSidePanelComponent;
  let fixture: ComponentFixture<ServicePointSidePanelComponent>;

  const activatedRouteMock = { data: of({ servicePoint: [BERN_WYLEREGG] }) };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [
        ServicePointSidePanelComponent,
        DisplayDatePipe,
        SplitServicePointNumberPipe,
        MockAtlasButtonComponent,
        DateRangeTextComponent,
      ],
      imports: [AppTestingModule],
      providers: [
        { provide: AuthService, useValue: authService },
        { provide: ActivatedRoute, useValue: activatedRouteMock },
        { provide: TrafficPointMapService, useValue: trafficPointMapService },
        SplitServicePointNumberPipe,
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(ServicePointSidePanelComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display current designationOfficial and validity', () => {
    expect(component.selectedVersion).toBeTruthy();

    expect(component.selectedVersion.designationOfficial).toEqual('Bern, Wyleregg');
    expect(component.maxValidity.validFrom).toEqual(new Date('2014-12-14'));
    expect(component.maxValidity.validTo).toEqual(new Date('2021-03-31'));

    expect(trafficPointMapService.displayTrafficPointsOnMap).toHaveBeenCalled();
  });
});
