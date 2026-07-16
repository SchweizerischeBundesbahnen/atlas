import { Component, input, signal } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { Field, form, FormField } from '@angular/forms/signals';
import { beforeEach, describe, expect, it } from 'vitest';
import { FieldExample } from '../atlas-text-field/field-example';
import { AtlasFieldErrorComponent } from '../atlas-field-error/atlas-field-error.component';
import { AtlasCommentComponent } from './atlas-comment.component';
import { AtlasLabelFieldComponent } from '../atlas-label-field/atlas-label-field.component';

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
  selector: 'atlas-field-error',
  template: '',
})
class MockAtlasFieldErrorComponent {
  readonly field = input.required<Field<string>>();
}

const createField = (initialValue: string): Field<string> =>
  TestBed.runInInjectionContext(() => {
    const model = signal({ value: initialValue });
    const testForm = form(model);
    return testForm.value;
  });

describe('AtlasCommentComponent', () => {
  let fixture: ComponentFixture<AtlasCommentComponent>;

  const getLabelField = () =>
    fixture.debugElement.query(By.directive(MockAtlasLabelFieldComponent))?.componentInstance as
      MockAtlasLabelFieldComponent | undefined;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [AtlasCommentComponent],
    }).overrideComponent(AtlasCommentComponent, {
      remove: {
        imports: [AtlasLabelFieldComponent, AtlasFieldErrorComponent],
      },
      add: {
        imports: [MockAtlasLabelFieldComponent, FormField, MockAtlasFieldErrorComponent],
      },
    });

    fixture = TestBed.createComponent(AtlasCommentComponent);
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
