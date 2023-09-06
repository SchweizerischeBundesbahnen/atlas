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
import { SpatialReference } from '../../../api';
import { MaterialModule } from '../../../core/module/material.module';
import { TextFieldComponent } from '../../../core/form-components/text-field/text-field.component';
import { RemoveCharsDirective } from '../../../core/form-components/text-field/remove-chars.directive';
import { DecimalNumberPipe } from '../../../core/pipe/decimal-number.pipe';

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
        FormModule,
        MaterialModule,
        TranslateModule.forRoot({
          loader: { provide: TranslateLoader, useClass: TranslateFakeLoader },
        }),
      ],
      providers: [{ provide: TranslatePipe }],
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
});
