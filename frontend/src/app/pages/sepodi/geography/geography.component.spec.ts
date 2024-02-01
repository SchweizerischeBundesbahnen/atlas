import { ComponentFixture, TestBed } from '@angular/core/testing';
import {
  TranslateFakeLoader,
  TranslateLoader,
  TranslateModule,
  TranslatePipe,
} from '@ngx-translate/core';
import { FormModule } from '../../../core/module/form.module';
import { GeographyComponent } from './geography.component';
import { FormControl, FormGroup } from '@angular/forms';
import { GeographyFormGroup } from './geography-form-group';
import { CoordinatePair, SpatialReference } from '../../../api';
import { MaterialModule } from '../../../core/module/material.module';
import { TextFieldComponent } from '../../../core/form-components/text-field/text-field.component';
import { RemoveCharsDirective } from '../../../core/form-components/text-field/remove-chars.directive';
import { DecimalNumberPipe } from '../../../core/pipe/decimal-number.pipe';
import { CoordinatePairWGS84, MapService } from '../map/map.service';
import { CoordinateTransformationService } from './coordinate-transformation.service';
import { AppTestingModule } from '../../../app.testing.module';
import { BehaviorSubject, Subject } from 'rxjs';

const mapService = jasmine.createSpyObj<MapService>(
  ['placeMarkerAndFlyTo', 'enterCoordinateSelectionMode', 'exitCoordinateSelectionMode'],
  {
    mapInitialized: new BehaviorSubject(false),
  },
);
mapService.clickedGeographyCoordinates = new Subject<CoordinatePairWGS84>();

const coordinateTransformationServiceSpy = jasmine.createSpyObj<CoordinateTransformationService>([
  'transform',
]);
describe('GeographyComponent', () => {
  let component: GeographyComponent;
  let fixture: ComponentFixture<GeographyComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [
        GeographyComponent,
        TextFieldComponent,
        RemoveCharsDirective,
        DecimalNumberPipe,
      ],
      imports: [
        AppTestingModule,
        FormModule,
        MaterialModule,
        TranslateModule.forRoot({
          loader: { provide: TranslateLoader, useClass: TranslateFakeLoader },
        }),
      ],
      providers: [
        { provide: TranslatePipe },
        { provide: MapService, useValue: mapService },
        { provide: CoordinateTransformationService, useValue: coordinateTransformationServiceSpy },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(GeographyComponent);
    component = fixture.componentInstance;
    component.form = new FormGroup<GeographyFormGroup>({
      east: new FormControl(45),
      north: new FormControl(7),
      height: new FormControl(5),
      spatialReference: new FormControl(SpatialReference.Lv95),
    });
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should transform coordinates & place marker and fly to on change manually coordinates', () => {
    const coordinates: CoordinatePair = {
      north: 20000,
      east: 10000,
      spatialReference: SpatialReference.Lv95,
    };
    coordinateTransformationServiceSpy.transform.and.returnValue({
      north: 12,
      east: 12,
      spatialReference: SpatialReference.Wgs84,
    });

    component.onChangeCoordinatesManually(coordinates);

    expect(coordinateTransformationServiceSpy.transform).toHaveBeenCalledWith(
      { north: 20000, east: 10000, spatialReference: SpatialReference.Lv95 },
      SpatialReference.Wgs84,
    );
    expect(mapService.placeMarkerAndFlyTo).toHaveBeenCalledWith({ lng: 12, lat: 12 });
  });

  it('should set height to form', () => {
    const coordinates: CoordinatePair = {
      north: 1207935,
      east: 2600464,
      spatialReference: SpatialReference.Lv95,
    };
    spyOn(component, 'setHeightFromGeoData');

    component.setHeightFromGeoData(coordinates)

    expect(component.setHeightFromGeoData).toHaveBeenCalledWith(coordinates)
  });

  it('should call setHeightFromGeoData if initTransformedCoordinatePair is called', () => {
    spyOn(component, 'setHeightFromGeoData');

    component.initTransformedCoordinatePair()

    expect(component.setHeightFromGeoData).toHaveBeenCalled()
  });
});
