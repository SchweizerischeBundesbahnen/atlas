import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RgbPickerComponent } from './rgb-picker.component';
import { AbstractControl, FormControl, FormGroup } from '@angular/forms';
import { By } from '@angular/platform-browser';
import { AppTestingModule } from '../../../../app.testing.module';
import { ColorPickerModule } from 'ngx-color-picker';
import { AtlasFieldErrorComponent } from '../../../../core/form-components/atlas-field-error/atlas-field-error.component';
import { AtlasLabelFieldComponent } from '../../../../core/form-components/atlas-label-field/atlas-label-field.component';
import { TranslatePipe } from '@ngx-translate/core';

describe('RgbPickerComponent', () => {
  let component: RgbPickerComponent;
  let fixture: ComponentFixture<RgbPickerComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AppTestingModule, ColorPickerModule],
      declarations: [RgbPickerComponent, AtlasFieldErrorComponent, AtlasLabelFieldComponent],
      providers: [{ provide: TranslatePipe }],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(RgbPickerComponent);
    component = fixture.componentInstance;

    component.formGroup = new FormGroup({
      colorRgb: new FormControl(),
    });
    component.attributeName = 'colorRgb';
    fixture.detectChanges();
  });

  it('should create component', () => {
    expect(component).toBeTruthy();
  });

  it('should create rgb input with color indicator', () => {
    component.onChangeColor('#FF0000');
    fixture.detectChanges();

    const squareColor = fixture.debugElement.query(By.css('.color-indicator')).nativeElement.style
      .backgroundColor;
    expect(squareColor).toBe('rgb(255, 0, 0)');
  });

  it('should create rgb input with validation error', () => {
    const colorRgb: AbstractControl = component.formGroup.controls['colorRgb'];
    colorRgb.setValue('#FF0000F');
    colorRgb.markAsTouched();
    fixture.detectChanges();

    const errorMessage = fixture.debugElement.query(By.css('app-atlas-field-error')).nativeElement
      .innerText;
    expect(errorMessage).toBe('VALIDATION.COLOR_INVALID');
  });
});
