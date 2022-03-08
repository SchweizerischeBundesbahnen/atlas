import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RgbPickerComponent } from './rgb-picker.component';
import { CoreModule } from '../../../../core/module/core.module';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterModule } from '@angular/router';
import { TranslateFakeLoader, TranslateLoader, TranslateModule } from '@ngx-translate/core';
import { AbstractControl, FormControl, FormGroup } from '@angular/forms';
import { By } from '@angular/platform-browser';
import { ColorPickerModule } from 'ngx-color-picker';

describe('RgbPickerComponent', () => {
  let component: RgbPickerComponent;
  let fixture: ComponentFixture<RgbPickerComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        CoreModule,
        ColorPickerModule,
        HttpClientTestingModule,
        BrowserAnimationsModule,
        RouterModule.forRoot([]),
        TranslateModule.forRoot({
          loader: { provide: TranslateLoader, useClass: TranslateFakeLoader },
        }),
      ],
      declarations: [RgbPickerComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(RgbPickerComponent);
    component = fixture.componentInstance;

    component.formGroup = new FormGroup({
      colorRgb: new FormControl(),
    });
    component.attributeName = 'colorRgb';
    component.label = 'Rgb Label';

    fixture.detectChanges();
  });

  it('should create input component with label', () => {
    expect(component).toBeTruthy();
    const label = fixture.debugElement.query(By.css('mat-label')).nativeElement.innerText;
    expect(label).toBe('Rgb Label');
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

    const errorMessage = fixture.debugElement.query(By.css('mat-error')).nativeElement.innerText;
    expect(errorMessage).toBe('COMMON.COLOR_INVALID');
  });
});
