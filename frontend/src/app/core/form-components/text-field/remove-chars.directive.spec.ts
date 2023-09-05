import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Component } from '@angular/core';
import { RemoveCharsDirective } from './remove-chars.directive';
import { AppTestingModule } from '../../../app.testing.module';
import { TextFieldComponent } from './text-field.component';
import { FormControl, FormGroup } from '@angular/forms';
import { AtlasLabelFieldComponent } from '../atlas-label-field/atlas-label-field.component';
import { AtlasFieldErrorComponent } from '../atlas-field-error/atlas-field-error.component';
import { TranslatePipe } from '@ngx-translate/core';
import { By } from '@angular/platform-browser';

@Component({
  template: ` <atlas-text-field
    [formGroup]="formGroup"
    [required]="true"
    controlName="east"
    fieldLabel="SEPODI.GEOLOCATION.LV95.EAST"
    [removeChars]="['\\'']"
  >
  </atlas-text-field>`,
})
class TestComponent {
  formGroup = new FormGroup({ east: new FormControl('1') });
}

const keyUpEvent = new KeyboardEvent('keyup');

describe('RemoveCharsDirective', () => {
  let component: TestComponent;
  let fixture: ComponentFixture<TestComponent>;

  beforeEach(() => {
    fixture = TestBed.configureTestingModule({
      declarations: [
        TextFieldComponent,
        AtlasLabelFieldComponent,
        AtlasFieldErrorComponent,
        RemoveCharsDirective,
        TestComponent,
      ],
      imports: [AppTestingModule],
      providers: [TranslatePipe],
    }).createComponent(TestComponent);

    component = fixture.componentInstance;

    fixture.detectChanges();
  });

  it('should remove defined char', () => {
    component.formGroup.controls.east.setValue("123'123");

    fixture.debugElement.query(By.css('atlas-text-field')).nativeElement.dispatchEvent(keyUpEvent);
    fixture.detectChanges();

    expect(component.formGroup.value.east).toBe('123123');
  });
});
