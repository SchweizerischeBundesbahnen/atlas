import { Component, Directive, input, signal } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { disabled, Field, form } from '@angular/forms/signals';
import { beforeEach, describe, expect, it } from 'vitest';
import { Moment } from 'moment/moment';

import { AtlasLabelFieldComponent } from '@atlas/form';
import { DateIconComponent } from '../date-icon/date-icon.component';
import { MatDatepicker, MatDatepickerInput } from '@angular/material/datepicker';
import { AtlasFieldErrorSfComponent } from '../atlas-field-error-sf/atlas-field-error-sf.component';
import { DateRangeSfComponent } from './date-range-sf.component';

@Component({
  selector: 'atlas-label-field',
  standalone: true,
  template: '',
})
class MockAtlasLabelFieldComponent {
  readonly fieldExamples = input<Array<{ label: string; translate?: boolean }>>([]);
  readonly required = input(false);
  readonly fieldLabel = input('');
  readonly infoIconTitle = input('');
}

@Component({
  selector: 'atlas-form-date-icon',
  standalone: true,
  template: '',
})
class MockDateIconComponent {
  readonly enabled = input(true);
}

@Component({
  selector: 'atlas-field-error-sf',
  standalone: true,
  template: '',
})
class MockAtlasFieldErrorSfComponent {
  readonly field = input.required<Field<Moment | null>>();
}

@Component({
  selector: 'mat-datepicker',
  standalone: true,
  template: '',
})
class MockMatDatepicker {
  readonly calendarHeaderComponent = input<unknown>();
}

@Directive({
  selector: 'input[matDatepicker]',
  standalone: true,
})
class MockMatDatepickerInput {
  readonly matDatepicker = input<unknown>(undefined, { alias: 'matDatepicker' });
  readonly max = input<unknown>();
  readonly min = input<unknown>();
}

describe('DateRangeSfComponent', () => {
  let fixture: ComponentFixture<DateRangeSfComponent>;

  const createField = (disabledState: boolean): Field<Moment | null> =>
    TestBed.runInInjectionContext(() => {
      const model = signal({ value: null });
      const testForm = form(model, (schemaPath) => {
        if (disabledState) {
          disabled(schemaPath.value);
        }
      });
      return testForm.value;
    });

  const getLabelFields = () =>
    fixture.debugElement
      .queryAll(By.directive(MockAtlasLabelFieldComponent))
      .map((debugEl) => debugEl.componentInstance as MockAtlasLabelFieldComponent);

  const getDateIcons = () =>
    fixture.debugElement
      .queryAll(By.directive(MockDateIconComponent))
      .map((debugEl) => debugEl.componentInstance as MockDateIconComponent);

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [DateRangeSfComponent],
    }).overrideComponent(DateRangeSfComponent, {
      remove: {
        imports: [
          AtlasLabelFieldComponent,
          DateIconComponent,
          MatDatepicker,
          AtlasFieldErrorSfComponent,
          MatDatepickerInput,
        ],
      },
      add: {
        imports: [
          MockAtlasLabelFieldComponent,
          MockDateIconComponent,
          MockMatDatepicker,
          MockAtlasFieldErrorSfComponent,
          MockMatDatepickerInput,
        ],
      },
    });

    fixture = TestBed.createComponent(DateRangeSfComponent);
    fixture.componentRef.setInput('validFromField', createField(false));
    fixture.componentRef.setInput('validToField', createField(false));
    fixture.detectChanges();
  });

  it('correct input bindings for child components', () => {
    fixture.componentRef.setInput('labelFrom', 'FROM_LABEL');
    fixture.componentRef.setInput('labelUntil', 'UNTIL_LABEL');
    fixture.componentRef.setInput('required', false);
    fixture.componentRef.setInput('infoIconTitleFrom', 'FROM_INFO');
    fixture.componentRef.setInput('infoIconTitleUntil', 'UNTIL_INFO');
    fixture.detectChanges();

    const labels = getLabelFields();

    expect(labels).toHaveLength(2);
    expect(labels[0].fieldLabel()).toBe('FROM_LABEL');
    expect(labels[1].fieldLabel()).toBe('UNTIL_LABEL');
    expect(labels[0].required()).toBe(false);
    expect(labels[1].required()).toBe(false);
    expect(labels[0].infoIconTitle()).toBe('FROM_INFO');
    expect(labels[1].infoIconTitle()).toBe('UNTIL_INFO');
  });

  it('usage of default date examples when no custom are set', () => {
    fixture.componentRef.setInput('setDateExamples', false);
    fixture.detectChanges();

    const labels = getLabelFields();

    expect(labels[0].fieldExamples()[2].label).toBe('21.01.2021');
    expect(labels[1].fieldExamples()[2].label).toBe('31.12.9999');
  });

  it('usage of custom date examples when set', () => {
    fixture.componentRef.setInput('setDateExamples', true);
    fixture.componentRef.setInput('labelFromExample', '01.01.2024');
    fixture.componentRef.setInput('labelUntilExample', '31.12.2024');
    fixture.detectChanges();

    const labels = getLabelFields();

    expect(labels[0].fieldExamples()[2].label).toBe('01.01.2024');
    expect(labels[1].fieldExamples()[2].label).toBe('31.12.2024');
  });

  it('show validTo autofill only when showMaxValidityAutoFill=true', () => {
    fixture.componentRef.setInput('showMaxValidityAutoFill', true);
    fixture.detectChanges();

    expect(fixture.nativeElement.querySelector('#validToSuggestions')).toBeTruthy();

    fixture.componentRef.setInput('showMaxValidityAutoFill', false);
    fixture.detectChanges();

    expect(fixture.nativeElement.querySelector('#validToSuggestions')).toBeFalsy();
  });

  it('correct enabled state for date icons', () => {
    fixture.componentRef.setInput('validFromField', createField(true));
    fixture.componentRef.setInput('validToField', createField(false));
    fixture.detectChanges();

    const icons = getDateIcons();

    expect(icons).toHaveLength(2);
    expect(icons[0].enabled()).toBe(false);
    expect(icons[1].enabled()).toBe(true);
  });
});
