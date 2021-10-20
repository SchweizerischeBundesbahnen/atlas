import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CmykPickerComponent } from './cmyk-picker.component';
import { CoreModule } from '../../../../core/module/core.module';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterModule } from '@angular/router';
import { TranslateFakeLoader, TranslateLoader, TranslateModule } from '@ngx-translate/core';
import { AbstractControl, FormControl, FormGroup } from '@angular/forms';
import { By } from '@angular/platform-browser';

describe('CmykPickerComponent', () => {
  let component: CmykPickerComponent;
  let fixture: ComponentFixture<CmykPickerComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        CoreModule,
        HttpClientTestingModule,
        BrowserAnimationsModule,
        RouterModule.forRoot([]),
        TranslateModule.forRoot({
          loader: { provide: TranslateLoader, useClass: TranslateFakeLoader },
        }),
      ],
      declarations: [CmykPickerComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(CmykPickerComponent);
    component = fixture.componentInstance;

    component.formGroup = new FormGroup({
      colorCmyk: new FormControl(),
    });
    component.attributeName = 'colorCmyk';
    component.label = 'Cmyk Label';

    fixture.detectChanges();
  });

  it('should create input component with label', () => {
    expect(component).toBeTruthy();
    const label = fixture.debugElement.query(By.css('mat-label')).nativeElement.innerText;
    expect(label).toBe('Cmyk Label');
  });

  it('should create cmyk input with color indicator', () => {
    component.formControl.patchValue('10,100,0,50');
    fixture.detectChanges();

    const squareColor = fixture.debugElement.query(By.css('.bi-square-fill')).nativeElement.style
      .color;
    expect(squareColor).toBe('rgb(115, 0, 128)');
  });

  it('should create cmyk input with validation error', () => {
    const colorCmyk: AbstractControl = component.formGroup.controls['colorCmyk'];
    colorCmyk.setValue('10,101,0,50');
    colorCmyk.markAsTouched();
    fixture.detectChanges();

    const squareColor = fixture.debugElement.query(By.css('.bi-square-fill')).nativeElement.style
      .color;
    expect(squareColor).toBe('transparent');

    const errorMessage = fixture.debugElement.query(By.css('mat-error')).nativeElement.innerText;
    expect(errorMessage).toBe('COMMON.CMYK_INVALID');
  });
});
