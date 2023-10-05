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
import { BehaviorSubject } from 'rxjs';

const mapService = jasmine.createSpyObj<MapService>(['placeMarkerAndFlyTo']);
const coordinateTransformationServiceSpy = jasmine.createSpyObj<CoordinateTransformationService>([
  'transform',
]);
const clickedGeographyCoordinatesSubject = new BehaviorSubject<CoordinatePairWGS84>({
  lat: 0,
  lng: 0,
});
mapService.clickedGeographyCoordinates = clickedGeographyCoordinatesSubject;
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
    component.formGroup = new FormGroup<GeographyFormGroup>({
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
    const coordinates: CoordinatePair = { north: 20000, east: 10000 };
    component.spatialReference = SpatialReference.Lv95;
    spyOn(component, 'isCoordinatePairNotZero').and.returnValue(true);
    coordinateTransformationServiceSpy.transform.and.returnValue({ north: 12, east: 12 });

    component.onChangeCoordinatesManually(coordinates);

    expect(coordinateTransformationServiceSpy.transform).toHaveBeenCalledWith(
      { north: 20000, east: 10000 },
      SpatialReference.Lv95,
      SpatialReference.Wgs84
    );
    expect(mapService.placeMarkerAndFlyTo).toHaveBeenCalledWith({ lng: 12, lat: 12 });
  });

  it('should not call placeMarkerAndFlyTo if lat/lng not valid', () => {
    spyOn(component, 'isValidCoordinatePair').and.returnValue(false);

    component.onChangeCoordinatesManually({ north: 0, east: 0 });

    expect(mapService.placeMarkerAndFlyTo).not.toHaveBeenCalled();
  });

  it('should transform coordinates & place marker and fly to on click map', () => {
    const coordinates: CoordinatePairWGS84 = { lat: 10, lng: 10 };
    component.spatialReference = SpatialReference.Lv95;
    spyOn(component, 'isCoordinatePairNotZero').and.returnValue(true);
    spyOn(component, 'setFormGroupValue');
    spyOn(component, 'initTransformedCoordinatePair');
    coordinateTransformationServiceSpy.transform.and.returnValue({ north: 12, east: 12 });

    component.onMapClick({ north: coordinates.lat, east: coordinates.lng });

    expect(coordinateTransformationServiceSpy.transform).toHaveBeenCalledWith(
      { north: 10, east: 10 },
      SpatialReference.Wgs84,
      SpatialReference.Lv95
    );
    expect(component.setFormGroupValue).toHaveBeenCalledWith({ north: 12, east: 12 });
    expect(component.initTransformedCoordinatePair).toHaveBeenCalled();
  });
});
