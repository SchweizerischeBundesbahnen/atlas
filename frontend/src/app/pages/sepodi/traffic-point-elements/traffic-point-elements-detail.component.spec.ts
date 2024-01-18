import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TrafficPointElementsDetailComponent } from './traffic-point-elements-detail.component';
import { ActivatedRoute, Router } from '@angular/router';
import { AppTestingModule } from '../../../app.testing.module';
import { DisplayDatePipe } from '../../../core/pipe/display-date.pipe';
import { BehaviorSubject, of, Subject } from 'rxjs';
import { ActivatedRouteMockType, MockAtlasButtonComponent } from '../../../app.testing.mocks';
import { DateRangeTextComponent } from '../../../core/versioning/date-range-text/date-range-text.component';
import { SplitServicePointNumberPipe } from '../../../core/search-service-point/split-service-point-number.pipe';
import { TextFieldComponent } from '../../../core/form-components/text-field/text-field.component';
import { SelectComponent } from '../../../core/form-components/select/select.component';
import { AtlasLabelFieldComponent } from '../../../core/form-components/atlas-label-field/atlas-label-field.component';
import { SwitchVersionComponent } from '../../../core/components/switch-version/switch-version.component';
import { TranslatePipe } from '@ngx-translate/core';
import { AtlasFieldErrorComponent } from '../../../core/form-components/atlas-field-error/atlas-field-error.component';
import { AtlasSpacerComponent } from '../../../core/components/spacer/atlas-spacer.component';
import { GeographyComponent } from '../geography/geography.component';
import { DecimalNumberPipe } from '../../../core/pipe/decimal-number.pipe';
import { AtlasSlideToggleComponent } from '../../../core/form-components/atlas-slide-toggle/atlas-slide-toggle.component';
import { InfoIconComponent } from '../../../core/form-components/info-icon/info-icon.component';
import { RemoveCharsDirective } from '../../../core/form-components/text-field/remove-chars.directive';
import { TrafficPointMapService } from '../map/traffic-point-map.service';
import { CoordinatePairWGS84, MapService } from '../map/map.service';
import { CoordinateTransformationService } from '../geography/coordinate-transformation.service';
import { AuthService } from '../../../core/auth/auth.service';
import { SloidComponent } from '../../../core/form-components/sloid/sloid.component';
import { ServicePointsService, TrafficPointElementsService } from '../../../api';
import { DialogService } from '../../../core/components/dialog/dialog.service';
import moment from 'moment/moment';
import { BERN_WYLEREGG } from '../../../../test/data/service-point';
import { BERN_WYLEREGG_TRAFFIC_POINTS } from '../../../../test/data/traffic-point-element';
import { UserDetailInfoComponent } from '../../../core/components/base-detail/user-edit-info/user-detail-info.component';

const authService: Partial<AuthService> = {};
const trafficPointMapService = jasmine.createSpyObj<TrafficPointMapService>([
  'displayTrafficPointsOnMap',
  'clearDisplayedTrafficPoints',
  'displayCurrentTrafficPoint',
  'clearCurrentTrafficPoint',
]);

describe('TrafficPointElementsDetailComponent', () => {
  let component: TrafficPointElementsDetailComponent;
  let fixture: ComponentFixture<TrafficPointElementsDetailComponent>;
  let router: Router;

  const mapService = jasmine.createSpyObj<MapService>([
    'placeMarkerAndFlyTo',
    'enterCoordinateSelectionMode',
    'exitCoordinateSelectionMode',
  ]);
  mapService.mapInitialized = new BehaviorSubject<boolean>(true);
  mapService.clickedGeographyCoordinates = new Subject<CoordinatePairWGS84>();

  const coordinateTransformationService = jasmine.createSpyObj<CoordinateTransformationService>([
    'transform',
  ]);

  const servicePointService = jasmine.createSpyObj(['getServicePointVersions']);
  servicePointService.getServicePointVersions.and.returnValue(of([BERN_WYLEREGG]));
  const trafficPointService = jasmine.createSpyObj('trafficPointElementsService', [
    'getAreasOfServicePoint',
    'updateTrafficPoint',
    'createTrafficPoint',
  ]);
  trafficPointService.getAreasOfServicePoint.and.returnValue(
    of({ objects: BERN_WYLEREGG_TRAFFIC_POINTS }),
  );
  trafficPointService.updateTrafficPoint.and.returnValue(of(BERN_WYLEREGG_TRAFFIC_POINTS));
  trafficPointService.createTrafficPoint.and.returnValue(of(BERN_WYLEREGG_TRAFFIC_POINTS[0]));

  const dialogService = jasmine.createSpyObj('dialogService', ['confirm']);
  dialogService.confirm.and.returnValue(of(true));

  describe('for existing Version', () => {
    beforeEach(() => {
      window.history.pushState({ isTrafficPointArea: false }, '', '');
      const activatedRouteMock = { data: of({ trafficPoint: [BERN_WYLEREGG_TRAFFIC_POINTS[0]] }) };
      setupTestBed(activatedRouteMock);
      fixture = TestBed.createComponent(TrafficPointElementsDetailComponent);
      component = fixture.componentInstance;
      fixture.detectChanges();
      router = TestBed.inject(Router);
    });

    it('should display current designationOperational and validity', () => {
      expect(component.selectedVersion).toBeTruthy();

      expect(component.selectedVersion.designationOperational).toEqual('1');
      expect(component.maxValidity.validFrom).toEqual(new Date('2019-07-22'));
      expect(component.maxValidity.validTo).toEqual(new Date('2099-12-31'));
    });

    it('should init selected servicepoint', () => {
      expect(component.servicePointName).toBeTruthy();
      expect(component.servicePoint).toBeTruthy();
      expect(component.servicePointBusinessOrganisations).toBeTruthy();

      expect(component.servicePointNumber).toEqual(8589008);
      expect(component.servicePointNumberPartForSloid).toEqual('89008');

      expect(servicePointService.getServicePointVersions).toHaveBeenCalled();
    });

    it('should init selectable areas', () => {
      expect(component.areaOptions).toBeTruthy();

      expect(trafficPointService.getAreasOfServicePoint).toHaveBeenCalled();
    });

    it('should go back to servicepoint', () => {
      spyOn(router, 'navigate').and.returnValue(Promise.resolve(true));
      component.backToTrafficPointElements('traffic-point-elements');

      expect(router.navigate).toHaveBeenCalledWith([
        'service-point-directory',
        'service-points',
        8589008,
        'traffic-point-elements',
      ]);
    });

    it('should toggle form correctly', () => {
      expect(component.form.enabled).toBeFalse();

      component.toggleEdit();
      expect(component.form.enabled).toBeTrue();

      component.toggleEdit();
      expect(component.form.enabled).toBeFalse();
    });

    it('should update via service', () => {
      component.toggleEdit();
      component.save();

      expect(trafficPointService.updateTrafficPoint).toHaveBeenCalled();
    });
  });

  describe('for new Version', () => {
    beforeEach(() => {
      window.history.pushState({ isTrafficPointArea: false }, '', '');
      const activatedRouteMock = { data: of({ trafficPoint: [] }) };
      setupTestBed(activatedRouteMock);
      fixture = TestBed.createComponent(TrafficPointElementsDetailComponent);
      component = fixture.componentInstance;
      fixture.detectChanges();
      router = TestBed.inject(Router);
    });

    it('should not display current version', () => {
      expect(component.selectedVersion).toBeFalsy();
    });

    it('should save version', () => {
      component.form.controls.designation.setValue('Designation');
      component.form.controls.validFrom.setValue(moment(new Date(2000 - 10 - 1)));
      component.form.controls.validTo.setValue(moment(new Date(2099 - 10 - 1)));

      component.form.controls.trafficPointElementGeolocation?.disable();
      component.save();

      expect(trafficPointService.createTrafficPoint).toHaveBeenCalled();
    });
  });

  function setupTestBed(activatedRoute: ActivatedRouteMockType) {
    TestBed.configureTestingModule({
      declarations: [
        TrafficPointElementsDetailComponent,
        DisplayDatePipe,
        SplitServicePointNumberPipe,
        MockAtlasButtonComponent,
        DateRangeTextComponent,
        TextFieldComponent,
        SelectComponent,
        AtlasLabelFieldComponent,
        SwitchVersionComponent,
        AtlasFieldErrorComponent,
        AtlasSpacerComponent,
        AtlasSlideToggleComponent,
        GeographyComponent,
        DecimalNumberPipe,
        InfoIconComponent,
        RemoveCharsDirective,
        SloidComponent,
        UserDetailInfoComponent,
      ],
      imports: [AppTestingModule],
      providers: [
        { provide: AuthService, useValue: authService },
        { provide: TrafficPointMapService, useValue: trafficPointMapService },
        { provide: ActivatedRoute, useValue: activatedRoute },
        { provide: MapService, useValue: mapService },
        { provide: CoordinateTransformationService, useValue: coordinateTransformationService },
        { provide: ServicePointsService, useValue: servicePointService },
        { provide: TrafficPointElementsService, useValue: trafficPointService },
        { provide: DialogService, useValue: dialogService },
        SplitServicePointNumberPipe,
        TranslatePipe,
      ],
    }).compileComponents();
  }
});
