import { ComponentFixture, TestBed } from '@angular/core/testing';
import { beforeEach, describe, expect, it } from 'vitest';
import { SloidComponent } from './sloid.component';
import { FormControl, FormGroup } from '@angular/forms';
import { translateServiceProvider } from '../../../app.testing.mocks';
import { inputBinding, signal } from '@angular/core';

describe('SloidComponent', () => {
  let component: SloidComponent;
  let fixture: ComponentFixture<SloidComponent>;

  let formGroupInput: FormGroup;
  let givenPrefixInput: ReturnType<typeof signal<string>>;
  let numberColonsInput: ReturnType<typeof signal<number>>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [translateServiceProvider],
    });

    formGroupInput = new FormGroup({
      sloid: new FormControl(),
    });
    givenPrefixInput = signal('ch:1:sloid:851:');
    numberColonsInput = signal(0);

    const formGroupInputName: keyof SloidComponent = 'formGroup';
    const givenPrefixInputName: keyof SloidComponent = 'givenPrefix';
    const numberColonsInputName: keyof SloidComponent = 'numberColons';
    fixture = TestBed.createComponent(SloidComponent, {
      bindings: [
        inputBinding(formGroupInputName, () => formGroupInput),
        inputBinding(givenPrefixInputName, () => givenPrefixInput()),
        inputBinding(numberColonsInputName, () => numberColonsInput()),
      ],
    });
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should be null for automatic sloid', () => {
    expect(component.automaticSloid).toBe(true);
    expect(component.formGroup().valid).toBe(true);
    expect(component.formGroup().controls.sloid.value).toBeNull();
  });

  it('should be invalid if manual sloid selected without value', () => {
    component.automaticSloid = false;

    expect(component.formGroup().valid).toBe(false);
    expect(component.form.valid).toBe(false);
  });

  it('should be invalid if manual sloid is not SID4PT', () => {
    component.automaticSloid = false;
    component.form.controls.sloid.setValue('@@');

    expect(component.form.valid).toBe(false);
  });

  it('should push sloid to formgroup', () => {
    component.automaticSloid = false;
    component.form.controls.sloid.setValue('123');

    expect(component.formGroup().valid).toBe(true);
    expect(component.form.valid).toBe(true);
    expect(component.formGroup().controls.sloid.value).toBe('ch:1:sloid:851:123');
  });

  it('should switch back to automatic correctly', () => {
    component.automaticSloid = false;
    component.form.controls.sloid.setValue('123');

    // switch back
    component.automaticSloid = true;

    expect(component.formGroup().valid).toBe(true);
    expect(component.form.valid).toBe(true);
    expect(component.formGroup().controls.sloid.value).toBeUndefined();
  });
});
