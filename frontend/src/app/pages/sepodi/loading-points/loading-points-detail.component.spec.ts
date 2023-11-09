import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LoadingPointsDetailComponent } from './loading-points-detail.component';
import { ActivatedRoute } from '@angular/router';
import { AppTestingModule } from '../../../app.testing.module';
import { DisplayDatePipe } from '../../../core/pipe/display-date.pipe';
import { BehaviorSubject, of, Subject } from 'rxjs';
import { AuthService } from '../../../core/auth/auth.service';
import { MockAtlasButtonComponent } from '../../../app.testing.mocks';
import { DateRangeTextComponent } from '../../../core/versioning/date-range-text/date-range-text.component';
import { SplitServicePointNumberPipe } from '../search-service-point/split-service-point-number.pipe';
import { BERN_WYLEREGG_TRAFFIC_POINTS } from '../traffic-point-element-test-data';
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
import { CoordinatePairWGS84, MapService } from '../map/map.service';
import { CoordinateTransformationService } from '../geography/coordinate-transformation.service';

const authService: Partial<AuthService> = {};

describe('TrafficPointElementsDetailComponent', () => {
  let component: LoadingPointsDetailComponent;
  let fixture: ComponentFixture<LoadingPointsDetailComponent>;

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

  const activatedRouteMock = { data: of({ trafficPoint: [BERN_WYLEREGG_TRAFFIC_POINTS[0]] }) };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [
        LoadingPointsDetailComponent,
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
      ],
      imports: [AppTestingModule],
      providers: [
        { provide: AuthService, useValue: authService },
        { provide: ActivatedRoute, useValue: activatedRouteMock },
        { provide: MapService, useValue: mapService },
        { provide: CoordinateTransformationService, useValue: coordinateTransformationService },
        SplitServicePointNumberPipe,
        TranslatePipe,
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(LoadingPointsDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display current designationOperational and validity', () => {
    expect(component.selectedVersion).toBeTruthy();

    expect(component.selectedVersion.designationOperational).toEqual('1');
    expect(component.maxValidity.validFrom).toEqual(new Date('2019-07-22'));
    expect(component.maxValidity.validTo).toEqual(new Date('2099-12-31'));
  });
});
