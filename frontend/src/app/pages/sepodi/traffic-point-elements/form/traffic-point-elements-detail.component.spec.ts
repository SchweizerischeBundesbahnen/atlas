import { ComponentFixture, TestBed } from '@angular/core/testing';
import { beforeEach, describe, expect, it, type Mocked, vi } from 'vitest';
import { TrafficPointElementsDetailComponent } from './traffic-point-elements-detail.component';
import { ActivatedRoute, Router } from '@angular/router';
import { DisplayDatePipe } from '../../../../core/pipe/display-date.pipe';
import { BehaviorSubject, of, Subject } from 'rxjs';
import {
  ActivatedRouteMockType,
  MockAtlasButtonComponent,
  MockNavigationSepodiPrmComponent,
  translateServiceProvider,
} from '../../../../app.testing.mocks';
import { DateRangeTextComponent } from '../../../../core/versioning/date-range-text/date-range-text.component';
import { SplitServicePointNumberPipe } from '../../../../core/search-service-point/split-service-point-number.pipe';
import { TextFieldComponent } from '../../../../core/form-components/text-field/text-field.component';
import { SelectComponent } from '../../../../core/form-components/select/select.component';
import { SwitchVersionComponent } from '../../../../core/components/switch-version/switch-version.component';
import { TranslatePipe } from '@ngx-translate/core';
import { AtlasFieldErrorComponent } from '../../../../core/form-components/atlas-field-error/atlas-field-error.component';
import { AtlasSpacerComponent } from '../../../../core/components/spacer/atlas-spacer.component';
import { GeographyComponent } from '../../geography/geography.component';
import { DecimalNumberPipe } from '../../../../core/pipe/decimal-number.pipe';
import { AtlasSlideToggleComponent } from '../../../../core/form-components/atlas-slide-toggle/atlas-slide-toggle.component';
import { RemoveCharsDirective } from '../../../../core/form-components/text-field/remove-chars.directive';
import { TrafficPointMapService } from '../../map/traffic-point-map.service';
import { CoordinatePairWGS84, MapService } from '../../map/map.service';
import { CoordinateTransformationService } from '../../geography/coordinate-transformation.service';
import { AuthService } from '../../../../core/auth/auth.service';
import { SloidComponent } from '../../../../core/form-components/sloid/sloid.component';
import { DialogService } from '../../../../core/components/dialog/dialog.service';
import moment from 'moment/moment';
import { BERN_WYLEREGG } from '../../../../../test/data/service-point';
import {
  BERN_WYLEREGG_TRAFFIC_POINTS,
  BERN_WYLEREGG_TRAFFIC_POINTS_CONTAINER,
} from '../../../../../test/data/traffic-point-element';
import { UserDetailInfoComponent } from '../../../../core/components/user-edit-info/user-detail-info.component';
import { DetailPageContainerComponent } from '../../../../core/components/detail-page-container/detail-page-container.component';
import { DetailPageContentComponent } from '../../../../core/components/detail-page-content/detail-page-content.component';
import { DetailFooterComponent } from '../../../../core/components/detail-footer/detail-footer.component';
import { ServicePointService } from '../../../../api/service/sepodi/service-point.service';
import { TrafficPointElementInternalService } from '../../../../api/service/sepodi/traffic-point-element-internal.service';
import { TrafficPointElementService } from '../../../../api/service/sepodi/traffic-point-element.service';
import { ReactiveFormsModule } from '@angular/forms';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { DateModule } from '../../../../core/module/date.module';
import { AtlasLabelFieldComponent, InfoIconComponent } from '@atlas/form';

describe('TrafficPointElementsDetailComponent', () => {
  let component: TrafficPointElementsDetailComponent;
  let fixture: ComponentFixture<TrafficPointElementsDetailComponent>;
  let routerSpy: Mocked<Pick<Router, 'navigate'>> & { events: unknown };

  let authService: Partial<AuthService>;
  let trafficPointMapService: Mocked<
    Pick<
      TrafficPointMapService,
      | 'displayTrafficPointsOnMap'
      | 'clearDisplayedTrafficPoints'
      | 'displayCurrentTrafficPoint'
      | 'clearCurrentTrafficPoint'
    >
  >;
  let mapService: Mocked<
    Pick<
      MapService,
      | 'placeMarkerAndFlyTo'
      | 'enterCoordinateSelectionMode'
      | 'exitCoordinateSelectionMode'
      | 'mapInitialized'
      | 'clickedGeographyCoordinates'
    >
  >;
  let coordinateTransformationService: Mocked<Pick<CoordinateTransformationService, 'transform'>>;
  let servicePointService: Mocked<Pick<ServicePointService, 'getServicePointVersions'>>;
  let trafficPointService: Mocked<Pick<TrafficPointElementService, 'updateTrafficPoint' | 'createTrafficPoint'>>;
  let trafficPointInternalService: Mocked<
    Pick<TrafficPointElementInternalService, 'getAreasOfServicePoint' | 'revokeTrafficPoint'>
  >;
  let dialogService: Mocked<Pick<DialogService, 'openDialogDataWithConfirmationResult'>>;

  beforeEach(() => {
    authService = {};
    trafficPointMapService = {
      displayTrafficPointsOnMap: vi.fn(),
      clearDisplayedTrafficPoints: vi.fn(),
      displayCurrentTrafficPoint: vi.fn(),
      clearCurrentTrafficPoint: vi.fn(),
    };
    mapService = {
      placeMarkerAndFlyTo: vi.fn(),
      enterCoordinateSelectionMode: vi.fn(),
      exitCoordinateSelectionMode: vi.fn(),
      mapInitialized: new BehaviorSubject<boolean>(true),
      clickedGeographyCoordinates: new Subject<CoordinatePairWGS84>(),
    };
    coordinateTransformationService = {
      transform: vi.fn(),
    };
    servicePointService = {
      getServicePointVersions: vi.fn().mockReturnValue(of([BERN_WYLEREGG])),
    };
    trafficPointService = {
      updateTrafficPoint: vi.fn().mockReturnValue(of(BERN_WYLEREGG_TRAFFIC_POINTS)),
      createTrafficPoint: vi.fn().mockReturnValue(of(BERN_WYLEREGG_TRAFFIC_POINTS[0])),
    };
    trafficPointInternalService = {
      getAreasOfServicePoint: vi.fn().mockReturnValue(of(BERN_WYLEREGG_TRAFFIC_POINTS_CONTAINER)),
      revokeTrafficPoint: vi.fn().mockReturnValue(of(undefined)),
    };
    dialogService = {
      openDialogDataWithConfirmationResult: vi.fn().mockReturnValue(of(true)),
    };
  });

  describe('for existing Version', () => {
    beforeEach(async () => {
      routerSpy = {
        navigate: vi.fn().mockReturnValue(Promise.resolve(true)),
        events: of(null),
      };

      const activatedRouteMock = {
        parent: {
          snapshot: {
            params: {
              servicePointNumber: 8589008,
            },
          },
          data: of({
            trafficPoint: [BERN_WYLEREGG_TRAFFIC_POINTS[0]],
            isTrafficPointArea: false,
          }),
        },
      };
      await setupTestBed(activatedRouteMock);
      fixture = TestBed.createComponent(TrafficPointElementsDetailComponent);
      component = fixture.componentInstance;
      fixture.detectChanges();
    });

    it('should init selected servicepoint', () => {
      expect(component.servicePoint).toBeTruthy();
      expect(component.servicePointBusinessOrganisations).toBeTruthy();

      expect(component.servicePointNumber).toEqual(8589008);
      expect(component.servicePointSloid).toEqual('ch:1:sloid:89008');

      expect(servicePointService.getServicePointVersions).toHaveBeenCalled();
    });

    it('should init selectable areas', () => {
      expect(component.areaOptions).toBeTruthy();

      expect(trafficPointInternalService.getAreasOfServicePoint).toHaveBeenCalled();
    });

    it('should go back to servicepoint', () => {
      routerSpy.navigate.mockReturnValue(Promise.resolve(true));
      component.backToTrafficPointElements('traffic-point-elements');

      expect(routerSpy.navigate).toHaveBeenCalledWith([
        'service-point-directory',
        'service-points',
        8589008,
        'traffic-point-elements',
      ]);
    });

    it('should toggle form correctly', () => {
      expect(component.form.enabled).toBe(false);

      component.toggleEdit();
      expect(component.form.enabled).toBe(true);

      component.toggleEdit();
      expect(component.form.enabled).toBe(false);
    });

    it('should update via service', () => {
      component.toggleEdit();
      component.save();

      expect(trafficPointService.updateTrafficPoint).toHaveBeenCalled();
    });

    it('should navigate back on confirm cancel', () => {
      component.isTrafficPointArea = true;
      vi.spyOn(component, 'backToTrafficPointElements').mockImplementation(() => {});

      component.confirmCancel();

      expect(component.backToTrafficPointElements).toHaveBeenCalled();
    });
  });

  describe('for new Version', () => {
    beforeEach(async () => {
      routerSpy = {
        navigate: vi.fn().mockReturnValue(Promise.resolve(true)),
        events: of(null),
      };

      const activatedRouteMock = {
        parent: {
          snapshot: {
            params: {
              servicePointNumber: 89008,
            },
          },
          data: of({ trafficPoint: [], isTrafficPointArea: false }),
        },
      };
      await setupTestBed(activatedRouteMock);
      fixture = TestBed.createComponent(TrafficPointElementsDetailComponent);
      component = fixture.componentInstance;
      fixture.detectChanges();
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

    it('should revoke traffic point', () => {
      component.selectedVersion = BERN_WYLEREGG_TRAFFIC_POINTS[0];
      component.revoke();

      expect(trafficPointInternalService.revokeTrafficPoint).toHaveBeenCalled();
    });
  });

  function setupTestBed(activatedRoute: ActivatedRouteMockType) {
    Element.prototype.scrollIntoView = vi.fn();
    return TestBed.configureTestingModule({
      imports: [
        ReactiveFormsModule,
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
        DetailPageContainerComponent,
        DetailPageContentComponent,
        DetailFooterComponent,
        MockNavigationSepodiPrmComponent,
        DateModule.forRoot(),
      ],
      providers: [
        { provide: AuthService, useValue: authService },
        { provide: TrafficPointMapService, useValue: trafficPointMapService },
        { provide: ActivatedRoute, useValue: activatedRoute },
        { provide: MapService, useValue: mapService },
        {
          provide: CoordinateTransformationService,
          useValue: coordinateTransformationService,
        },
        { provide: ServicePointService, useValue: servicePointService },
        { provide: TrafficPointElementService, useValue: trafficPointService },
        {
          provide: TrafficPointElementInternalService,
          useValue: trafficPointInternalService,
        },
        { provide: DialogService, useValue: dialogService },
        { provide: Router, useValue: routerSpy },
        SplitServicePointNumberPipe,
        TranslatePipe,
        provideHttpClient(),
        provideHttpClientTesting(),
        translateServiceProvider,
      ],
    }).compileComponents();
  }
});
