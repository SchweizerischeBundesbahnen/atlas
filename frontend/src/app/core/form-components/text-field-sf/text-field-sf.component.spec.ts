import { Component, input, signal } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { Field, form } from '@angular/forms/signals';
import { beforeEach, describe, expect, it } from 'vitest';

import { AtlasLabelFieldComponent } from '@atlas/form';
import { AtlasFieldErrorSfComponent } from '../atlas-field-error-sf/atlas-field-error-sf.component';
import { TextFieldSfComponent } from './text-field-sf.component';
import { translateServiceProvider } from '../../../app.testing.mocks';
import { FieldExample } from '../text-field/field-example';

@Component({ selector: 'atlas-label-field', standalone: true, template: '' })
class MockAtlasLabelFieldComponent {
  readonly fieldLabel = input('');
  readonly required = input(false);
  readonly infoIconTitle = input<string>();
  readonly infoIconLink = input<string>();
  readonly fieldExamples = input<FieldExample[]>([]);
}

@Component({ selector: 'atlas-field-error-sf', standalone: true, template: '' })
class MockAtlasFieldErrorSfComponent {
  readonly field = input.required<Field<string>>();
}

describe('TextFieldSfComponent', () => {
  let fixture: ComponentFixture<TextFieldSfComponent>;

  const createField = (): Field<string> =>
    TestBed.runInInjectionContext(() => {
      const model = signal({ value: '' });
      return form(model).value;
    });

  const getLabelField = () =>
    fixture.debugElement.query(By.directive(MockAtlasLabelFieldComponent))?.componentInstance as
      MockAtlasLabelFieldComponent | undefined;

  const getFieldError = () =>
    fixture.debugElement.query(By.directive(MockAtlasFieldErrorSfComponent))?.componentInstance as
      MockAtlasFieldErrorSfComponent | undefined;

  const getInput = (): HTMLInputElement | null => fixture.nativeElement.querySelector('input');

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [TextFieldSfComponent],
      providers: [translateServiceProvider],
    }).overrideComponent(TextFieldSfComponent, {
      remove: { imports: [AtlasLabelFieldComponent, AtlasFieldErrorSfComponent] },
      add: { imports: [MockAtlasLabelFieldComponent, MockAtlasFieldErrorSfComponent] },
    });

    fixture = TestBed.createComponent(TextFieldSfComponent);
    fixture.componentRef.setInput('field', createField());
    fixture.componentRef.setInput('fieldName', 'test-field');
    fixture.detectChanges();
  });

  // --- conditional rendering ---

  it('should not render atlas-label-field when fieldLabel is not provided', () => {
    expect(getLabelField()).toBeUndefined();
  });

  it('should render atlas-label-field when fieldLabel is provided', () => {
    fixture.componentRef.setInput('fieldLabel', 'My Label');
    fixture.detectChanges();

    expect(getLabelField()).toBeTruthy();
  });

  // --- atlas-label-field input bindings ---

  it('should pass all label-related inputs to atlas-label-field', () => {
    const examples: FieldExample[] = [{ label: 'CH' }, { label: 'DE' }];

    fixture.componentRef.setInput('fieldLabel', 'Station Name');
    fixture.componentRef.setInput('required', true);
    fixture.componentRef.setInput('infoIconTitle', 'More info');
    fixture.componentRef.setInput('infoIconLink', 'https://example.com');
    fixture.componentRef.setInput('fieldExamples', examples);
    fixture.detectChanges();

    const label = getLabelField();
    expect(label?.fieldLabel()).toBe('Station Name');
    expect(label?.required()).toBe(true);
    expect(label?.infoIconTitle()).toBe('More info');
    expect(label?.infoIconLink()).toBe('https://example.com');
    expect(label?.fieldExamples()).toEqual(examples);
  });

  // --- native input bindings ---

  it('should set the data-cy attribute from fieldName', () => {
    fixture.componentRef.setInput('fieldName', 'station-name');
    fixture.detectChanges();

    expect(getInput()?.getAttribute('data-cy')).toBe('station-name');
  });

  it('should set the placeholder on the input', () => {
    fixture.componentRef.setInput('placeholder', 'PLACEHOLDER_KEY');
    fixture.detectChanges();

    // TranslatePipe returns the key unchanged when no translations are loaded
    expect(getInput()?.getAttribute('placeholder')).toBe('PLACEHOLDER_KEY');
  });

  it('should use an empty string as default placeholder', () => {
    expect(getInput()?.getAttribute('placeholder')).toBe('');
  });

  // --- conditional CSS class ---

  it('should apply text-field-wrapper class when paddingBottom is true (default)', () => {
    expect(fixture.nativeElement.querySelector('.text-field-wrapper')).toBeTruthy();
  });

  it('should not apply text-field-wrapper class when paddingBottom is false', () => {
    fixture.componentRef.setInput('paddingBottom', false);
    fixture.detectChanges();

    expect(fixture.nativeElement.querySelector('.text-field-wrapper')).toBeFalsy();
  });

  // --- atlas-field-error-sf ---

  it('should always render atlas-field-error-sf', () => {
    expect(getFieldError()).toBeTruthy();
  });

  it('should pass the field input to atlas-field-error-sf', () => {
    const field = createField();
    fixture.componentRef.setInput('field', field);
    fixture.detectChanges();

    expect(getFieldError()?.field()).toBe(field);
  });
});
