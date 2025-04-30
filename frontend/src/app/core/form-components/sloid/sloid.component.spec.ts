import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SloidComponent } from './sloid.component';
import { FormModule } from '../../module/form.module';
import {
  TranslateFakeLoader,
  TranslateLoader,
  TranslateModule,
  TranslatePipe,
} from '@ngx-translate/core';
import { FormControl, FormGroup } from '@angular/forms';

describe('SloidComponent', () => {
  let component: SloidComponent;
  let fixture: ComponentFixture<SloidComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        FormModule,
        TranslateModule.forRoot({
          loader: { provide: TranslateLoader, useClass: TranslateFakeLoader },
        }),
        SloidComponent,
      ],
      providers: [{ provide: TranslatePipe }],
    }).compileComponents();

    fixture = TestBed.createComponent(SloidComponent);
    component = fixture.componentInstance;

    component.formGroup = new FormGroup({
      sloid: new FormControl(),
    });
    component.givenPrefix = 'ch:1:sloid:851:';
    component.numberColons = 0;

    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should be null for automatic sloid', () => {
    expect(component.automaticSloid).toBeTrue();
    expect(component.formGroup.valid).toBeTrue();

    expect(component.formGroup.controls.sloid.value).toBeNull();
  });

  it('should be invalid if manual sloid selected without value', () => {
    component.automaticSloid = false;

    expect(component.formGroup.valid).toBeFalse();
    expect(component.form.valid).toBeFalse();
  });

  it('should be invalid if manual sloid is not SID4PT', () => {
    component.automaticSloid = false;
    component.form.controls.sloid.setValue('@@');

    expect(component.form.valid).toBeFalse();
  });

  it('should push sloid to formgroup', () => {
    component.automaticSloid = false;
    component.form.controls.sloid.setValue('123');

    expect(component.formGroup.valid).toBeTrue();
    expect(component.form.valid).toBeTrue();

    expect(component.formGroup.controls.sloid.value).toBe('ch:1:sloid:851:123');
  });

  it('should switch back to automatic correctly', () => {
    component.automaticSloid = false;
    component.form.controls.sloid.setValue('123');

    // switch back
    component.automaticSloid = true;

    expect(component.formGroup.valid).toBeTrue();
    expect(component.form.valid).toBeTrue();

    expect(component.formGroup.controls.sloid.value).toBeUndefined();
  });
});
