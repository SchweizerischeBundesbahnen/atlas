import { TestBed } from '@angular/core/testing';

import { ValidationService } from './validation.service';
import moment from 'moment';
import { FormControl, FormGroup } from '@angular/forms';

describe('ValidationService', () => {
  let service: ValidationService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ValidationService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should not add any validation error', () => {
    //when
    const result = service.getValidation(null);
    //then
    expect(result).toBeDefined();
    expect(result.length).toBe(0);
  });

  it('should only add parse error and ignore required', () => {
    //given
    const error = {
      matDatepickerParse: { text: '00001900' },
      required: true,
    };
    //when
    const result = service.getValidation(error);
    //then
    expect(result).toBeDefined();
    expect(result.length).toBe(1);
    expect(result[0].error).toBe('VALIDATION.MATDATEPICKERPARSE');
    expect(result[0].value).toBeDefined();
  });

  it('should add required error', () => {
    //given
    const error = {
      required: true,
    };
    //when
    const result = service.getValidation(error);
    //then
    expect(result).toBeDefined();
    expect(result.length).toBe(1);
    expect(result[0].error).toBe('VALIDATION.REQUIRED');
    expect(result[0].value).toBeDefined();
  });

  it('should return validationError with validFrom and validTo', () => {
    ///given
    const error = {
      error: 'VALIDATION.DATE_RANGE_ERROR',
      date: {
        validFrom: moment('2000-10-09T22:00:00.000Z'),
        validTo: moment('2000-09-08T22:00:00.000Z'),
      },
    };

    //when
    const result = service.displayDate(error);
    //then
    expect(result).toBeDefined();
    expect(result).toBe('10.10.2000 - 09.09.2000');
  });

  it('should return validationError with min', () => {
    ///given
    const error = {
      error: 'VALIDATION.DATE_RANGE_ERROR',
      min: moment('2000-10-09T22:00:00.000Z'),
    };

    //when
    const result = service.displayDate(error);
    //then
    expect(result).toBeDefined();
    expect(result).toBe('10.10.2000');
  });

  it('should return validationError with max', () => {
    ///given
    const error = {
      error: 'VALIDATION.DATE_RANGE_ERROR',
      max: moment('2000-10-09T22:00:00.000Z'),
    };

    //when
    const result = service.displayDate(error);
    //then
    expect(result).toBeDefined();
    expect(result).toBe('10.10.2000');
  });

  it('should set whitespace error', () => {
    ///given
    const control1 = new FormControl(null);
    const control2 = new FormControl(' test ');
    const control3 = new FormControl('test');

    //when
    ValidationService.checkWhitespaceErrors([control1, control2, control3]);

    // then
    expect(control1.touched).toBe(true);
    expect(control3.touched).toBe(false);
    expect(control3.touched).toBe(false);

    expect(control1.errors).toBe(null);
    expect(control2.errors).toEqual({ whitespaces: true });
    expect(control3.errors).toBe(null);
  });
});
