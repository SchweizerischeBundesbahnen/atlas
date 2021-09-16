import { DateValidators } from './date-validators';
import { FormControl } from '@angular/forms';
import moment from 'moment/moment';

describe('Date Validator', () => {
  it('should return validation error when validFrom greater then validTo ', () => {
    //given
    const validFromForm = new FormControl('12.12.2000');
    const validToForm = new FormControl('12.12.1999');
    //when
    DateValidators.validateDates(validFromForm, validToForm);
    //then
    expect(validFromForm.errors).toBeDefined();
    const dateRangeErrorValidFrom = validFromForm.errors?.['date_range_error'];
    expect(dateRangeErrorValidFrom).toBeDefined();
    expect(dateRangeErrorValidFrom.date).toBeDefined();
    expect(dateRangeErrorValidFrom.date.validFrom).toBe('12.12.2000');
    expect(dateRangeErrorValidFrom.date.validTo).toBe('12.12.1999');

    expect(validToForm.errors).toBeDefined();
    const dateRangeErrorValidTo = validFromForm.errors?.['date_range_error'];
    expect(dateRangeErrorValidTo).toBeDefined();
    expect(dateRangeErrorValidTo.date).toBeDefined();
    expect(dateRangeErrorValidTo.date.validFrom).toBe('12.12.2000');
    expect(dateRangeErrorValidTo.date.validTo).toBe('12.12.1999');
  });

  it('should remove validation error when validTo is greater then validFrom ', () => {
    //given
    const validFrom = new Date(moment('2010-12-31 23:59:59').valueOf());
    const validTo = new Date(moment('2011-12-31 23:59:59').valueOf());
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
    DateValidators.validateDates(validFromForm, validToForm);
    //then
    expect(validFromForm.errors).toBeNull();
    expect(validToForm.errors).toBeNull();
  });
  it('should return validation error when validFrom greater then validTo and dates have in the past validation error', () => {
    //given
    const validFrom = new Date(moment('2010-12-31 23:59:59').valueOf());
    const validTo = new Date(moment('2009-12-31 23:59:59').valueOf());
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
    DateValidators.validateDates(validFromForm, validToForm);
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
    const validFromForm = new FormControl('12.12.2000');
    const validToForm = new FormControl('12.12.2001');
    //when
    DateValidators.validateDates(validFromForm, validToForm);
    //then
    expect(validFromForm.errors).toBeNull();
    expect(validToForm.errors).toBeNull();
  });
});
