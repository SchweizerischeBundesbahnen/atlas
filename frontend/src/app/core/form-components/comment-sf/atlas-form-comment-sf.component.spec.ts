import { Component, input, signal } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { Field, form, FormField } from '@angular/forms/signals';
import { beforeEach, describe, expect, it } from 'vitest';
import { AtlasLabelFieldComponent } from '@atlas/form';
import { FieldExample } from '../text-field/field-example';
import { AtlasFieldErrorSfComponent } from '../atlas-field-error-sf/atlas-field-error-sf.component';
import { AtlasFormCommentSfComponent } from './atlas-form-comment-sf.component';

@Component({
  selector: 'atlas-label-field',
  template: '',
})
class MockAtlasLabelFieldComponent {
  readonly fieldExamples = input<FieldExample[]>([]);
  readonly required = input(false);
  readonly fieldLabel = input('');
  readonly infoIconTitle = input<string>();
}

@Component({
  selector: 'atlas-field-error-sf',
  template: '',
})
class MockAtlasFieldErrorSfComponent {
  readonly field = input.required<Field<string>>();
}

const createField = (initialValue: string): Field<string> =>
  TestBed.runInInjectionContext(() => {
    const model = signal({ value: initialValue });
    const testForm = form(model);
    return testForm.value;
  });

describe('AtlasFormCommentSfComponent', () => {
  let fixture: ComponentFixture<AtlasFormCommentSfComponent>;

  const getLabelField = () =>
    fixture.debugElement.query(By.directive(MockAtlasLabelFieldComponent))?.componentInstance as
      MockAtlasLabelFieldComponent | undefined;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [AtlasFormCommentSfComponent],
    }).overrideComponent(AtlasFormCommentSfComponent, {
      remove: {
        imports: [AtlasLabelFieldComponent, AtlasFieldErrorSfComponent],
      },
      add: {
        imports: [MockAtlasLabelFieldComponent, FormField, MockAtlasFieldErrorSfComponent],
      },
    });

    fixture = TestBed.createComponent(AtlasFormCommentSfComponent);
    fixture.componentRef.setInput('field', createField('Initial comment'));
    fixture.detectChanges();
  });

  it('should render the label child (default input)', () => {
    const labelField = getLabelField();

    expect(labelField).toBeTruthy();
  });

  it('should not render the label child when displayLabel is false', () => {
    fixture.componentRef.setInput('displayLabel', false);
    fixture.detectChanges();

    expect(getLabelField()).toBeUndefined();
  });
});
