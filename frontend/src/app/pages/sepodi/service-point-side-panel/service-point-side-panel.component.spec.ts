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
import { ReadServicePointVersion } from '../../../api';

const authService: Partial<AuthService> = {};
const trafficPointMapService = jasmine.createSpyObj<TrafficPointMapService>([
  'displayTrafficPointsOnMap',
  'clearDisplayedTrafficPoints',
]);

const servicePointInGermany: ReadServicePointVersion[] = [
  {
    creationDate: '2024-01-19T09:05:52.782453',
    creator: 'e524381',
    editionDate: '2024-01-19T09:06:44.773714',
    editor: 'e524381',
    id: 16730,
    designationOfficial: 'Mauchen',
    freightServicePoint: false,
    businessOrganisation: 'ch:1:sboid:1100004',
    categories: [],
    operatingPointRouteNetwork: false,
    meansOfTransport: [],
    validFrom: new Date('2024-01-01'),
    validTo: new Date('2099-01-01'),
    etagVersion: 2,
    number: {
      number: 8001653,
      checkDigit: 5,
      uicCountryCode: 80,
      numberShort: 1653,
    },
    status: 'VALIDATED',
    operatingPoint: false,
    operatingPointWithTimetable: false,
    servicePointGeolocation: {
      spatialReference: 'LV95',
      lv95: {
        north: 1290229.39332,
        east: 2611448.61144,
        spatialReference: 'LV95',
      },
      wgs84: {
        north: 47.7625279833,
        east: 7.59131639363,
        spatialReference: 'WGS84',
      },
      lv03: {
        north: 290229.39332,
        east: 611448.61144,
        spatialReference: 'LV03',
      },
      isoCountryCode: 'DE',
    },
    country: 'GERMANY',
    borderPoint: false,
    trafficPoint: false,
    operatingPointKilometer: false,
    stopPoint: false,
    fareStop: false,
    hasGeolocation: true,
  },
];

describe('ServicePointSidePanelComponent', () => {
  let component: ServicePointSidePanelComponent;
  let fixture: ComponentFixture<ServicePointSidePanelComponent>;

  const activatedRouteMock = { data: of({ servicePoint: [BERN_WYLEREGG] }) };

  beforeEach(() => {
    TestBed.configureTestingModule({
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
    });
  });

  describe('panel for servicePoint in Switzerland', () => {
    beforeEach(() => {
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

      expect(component.tabs).toHaveSize(5);

      expect(trafficPointMapService.displayTrafficPointsOnMap).toHaveBeenCalled();
    });
  });

  describe('panel for servicePoint in Germany', () => {
    beforeEach(() => {
      TestBed.overrideProvider(ActivatedRoute, {
        useValue: {
          data: of({
            servicePoint: servicePointInGermany,
          }),
        },
      });
      fixture = TestBed.createComponent(ServicePointSidePanelComponent);
      component = fixture.componentInstance;
      fixture.detectChanges();
    });

    it('should display only 3 tabs', () => {
      expect(component.tabs).toHaveSize(3);
    });
  });
});
