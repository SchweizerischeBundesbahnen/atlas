import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TextFieldComponent } from './text-field.component';
import { FormModule } from '../../module/form.module';
import { TranslatePipe } from '@ngx-translate/core';
import { FormControl, FormGroup } from '@angular/forms';
import { InfoIconComponent } from '../info-icon/info-icon.component';
import { translateServiceProvider } from '../../../app.testing.mocks';
import { provideHttpClient } from '@angular/common/http';

describe('TextFieldComponent', () => {
  let component: TextFieldComponent;
  let fixture: ComponentFixture<TextFieldComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FormModule, TextFieldComponent, InfoIconComponent],
      providers: [TranslatePipe, translateServiceProvider, provideHttpClient()],
    }).compileComponents();

    fixture = TestBed.createComponent(TextFieldComponent);
    component = fixture.componentInstance;
    component.formGroup = new FormGroup({
      number: new FormControl('ch:slnid:12345'),
    });
    component.controlName = 'number';
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
