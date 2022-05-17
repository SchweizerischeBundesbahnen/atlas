import { DateRangeValidator } from './date-range-validator';
import { FormControl } from '@angular/forms';
import moment from 'moment';

describe('Date Validator', () => {
  it('should return validation error when validFrom greater then validTo ', () => {
    //given
    const validFrom = moment('12.12.2000', 'DD.MM.YYYY');
    const validTo = moment('12.12.1999', 'DD.MM.YYYY');
    const validFromForm = new FormControl(validFrom);
    const validToForm = new FormControl(validTo);
    //when
    DateRangeValidator.validate(validFromForm, validToForm);
    //then
    expect(validFromForm.errors).toBeDefined();
    const dateRangeErrorValidFrom = validFromForm.errors?.['date_range_error'];
    expect(dateRangeErrorValidFrom).toBeDefined();
    expect(dateRangeErrorValidFrom.date).toBeDefined();
    expect(dateRangeErrorValidFrom.date.validFrom).toBe(validFrom);
    expect(dateRangeErrorValidFrom.date.validTo).toBe(validTo);

    expect(validToForm.errors).toBeDefined();
    const dateRangeErrorValidTo = validFromForm.errors?.['date_range_error'];
    expect(dateRangeErrorValidTo).toBeDefined();
    expect(dateRangeErrorValidTo.date).toBeDefined();
    expect(dateRangeErrorValidTo.date.validFrom).toBe(validFrom);
    expect(dateRangeErrorValidTo.date.validTo).toBe(validTo);
  });

  it('should return validation success when validFrom equal to validTo ', () => {
    //given
    const validFrom = moment();
    const validTo = moment();
    const validFromForm = new FormControl(validFrom);
    const validToForm = new FormControl(validTo);
    //when
    DateRangeValidator.validate(validFromForm, validToForm);
    //then
    expect(validFromForm.errors).toBeNull();
    expect(validToForm.errors).toBeNull();
  });

  it('should remove validation error when validTo is greater then validFrom ', () => {
    //given
    const validFrom = moment('2010-12-31');
    const validTo = moment('2011-12-31');
    const validFromForm = new FormControl(validFrom);
    const validToForm = new FormControl(validTo);
    validFromForm.setErrors({
      date_range_error: {
        date: {
          actual: validFrom,
          min: 'validTo',
        },
      },
    });
    validToForm.setErrors({
      date_range_error: {
        date: {
          actual: validFrom,
          min: 'validTo',
        },
      },
    });
    //when
    DateRangeValidator.validate(validFromForm, validToForm);
    //then
    expect(validFromForm.errors).toBeNull();
    expect(validToForm.errors).toBeNull();
  });
  it('should return validation error when validFrom greater then validTo and dates have in the past validation error', () => {
    //given
    const validFrom = moment('2010-12-31');
    const validTo = moment('2009-12-31 23:59:59');
    const validFromForm = new FormControl(validFrom);
    const validToForm = new FormControl(validTo);
    validFromForm.setErrors({
      matDatepickerMin: {
        actual: validFrom,
        min: 'validTo',
      },
    });
    validToForm.setErrors({
      matDatepickerMin: {
        actual: validFrom,
        min: 'validTo',
      },
    });
    //when
    DateRangeValidator.validate(validFromForm, validToForm);
    //then
    expect(validFromForm.errors).toBeDefined();
    const dateRangeErrorValidFrom = validFromForm.errors?.['date_range_error'];
    expect(dateRangeErrorValidFrom).toBeDefined();
    expect(dateRangeErrorValidFrom.date).toBeDefined();
    expect(dateRangeErrorValidFrom.date.validFrom).toBe(validFrom);
    expect(dateRangeErrorValidFrom.date.validTo).toBe(validTo);
    const datePickerMinErrorValidFrom = validFromForm.errors?.['matDatepickerMin'];
    expect(datePickerMinErrorValidFrom).toBeDefined();

    expect(validToForm.errors).toBeDefined();
    const dateRangeErrorValidTo = validFromForm.errors?.['date_range_error'];
    expect(dateRangeErrorValidTo).toBeDefined();
    expect(dateRangeErrorValidTo.date).toBeDefined();
    expect(dateRangeErrorValidTo.date.validFrom).toBe(validFrom);
    expect(dateRangeErrorValidTo.date.validTo).toBe(validTo);
    const datePickerMinErrorValidTo = validFromForm.errors?.['matDatepickerMin'];
    expect(datePickerMinErrorValidTo).toBeDefined();
  });

  it('should successfully validate dates', () => {
    //given
    const validFromForm = new FormControl(moment('12.12.2000', 'DD.MM.YYYY'));
    const validToForm = new FormControl(moment('12.12.2001', 'DD.MM.YYYY'));
    //when
    DateRangeValidator.validate(validFromForm, validToForm);
    //then
    expect(validFromForm.errors).toBeNull();
    expect(validToForm.errors).toBeNull();
  });
});
