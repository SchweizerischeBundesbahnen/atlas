import { ComponentFixture, TestBed } from '@angular/core/testing';
import { beforeEach, describe, expect, it } from 'vitest';
import { DateComponent } from './date.component';
import { FormControl, FormGroup } from '@angular/forms';
import { DateRangeValidator } from '../../validation/date-range/date-range-validator';
import { inputBinding } from '@angular/core';
import { AppTestingModule } from '../../../app.testing.module';

describe('DateComponent', () => {
  let component: DateComponent;
  let fixture: ComponentFixture<DateComponent>;

  let formGroupInput: FormGroup;

  beforeEach(() => {
    formGroupInput = new FormGroup(
      {
        validFrom: new FormControl(),
        validTo: new FormControl(),
      },
      [DateRangeValidator.fromGreaterThenTo('validFrom', 'validTo')]
    );

    TestBed.configureTestingModule({
      imports: [AppTestingModule],
    });

    const formGroupInputName: keyof DateComponent = 'formGroup';
    fixture = TestBed.createComponent(DateComponent, {
      bindings: [inputBinding(formGroupInputName, () => formGroupInput)],
    });
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('MIN_DATE and MAX_DATE should be defined', () => {
    expect(component.minDate()).toBeDefined();
    expect(component.maxDate()).toBeDefined();
  });
});
