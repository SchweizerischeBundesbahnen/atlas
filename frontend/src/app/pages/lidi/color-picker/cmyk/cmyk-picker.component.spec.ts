import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CmykPickerComponent } from './cmyk-picker.component';
import { AbstractControl, UntypedFormControl, UntypedFormGroup } from '@angular/forms';
import { By } from '@angular/platform-browser';
import { AppTestingModule } from '../../../../app.testing.module';
import { WithDefaultValueDirective } from '../../../../core/text-input/with-default-value.directive';

describe('CmykPickerComponent', () => {
  let component: CmykPickerComponent;
  let fixture: ComponentFixture<CmykPickerComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AppTestingModule],
      declarations: [CmykPickerComponent, WithDefaultValueDirective],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(CmykPickerComponent);
    component = fixture.componentInstance;

    component.formGroup = new UntypedFormGroup({
      colorCmyk: new UntypedFormControl(),
    });
    component.attributeName = 'colorCmyk';
    fixture.detectChanges();
  });

  it('should create component', () => {
    expect(component).toBeTruthy();
  });

  it('should create cmyk input with color indicator', () => {
    component.formControl.patchValue('10,100,0,50');
    fixture.detectChanges();

    const squareColor = fixture.debugElement.query(By.css('.color-indicator')).nativeElement.style
      .backgroundColor;
    expect(squareColor).toBe('rgb(115, 0, 128)');
  });

  it('should create cmyk input with validation error', () => {
    const colorCmyk: AbstractControl = component.formGroup.controls['colorCmyk'];
    colorCmyk.setValue('10,101,0,50');
    colorCmyk.markAsTouched();
    fixture.detectChanges();

    const squareColor = fixture.debugElement.query(By.css('.color-indicator')).nativeElement.style
      .backgroundColor;
    expect(squareColor).toBe('transparent');

    const errorMessage = fixture.debugElement.query(By.css('mat-error')).nativeElement.innerText;
    expect(errorMessage).toBe('VALIDATION.CMYK_INVALID');
  });
});
