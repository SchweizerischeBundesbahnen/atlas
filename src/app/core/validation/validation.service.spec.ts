import { TestBed } from '@angular/core/testing';

import { ValidationService } from './validation.service';
import { ValidationError } from './validation-error';
import moment from 'moment/moment';

describe('ValidationService', () => {
  let service: ValidationService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ValidationService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should add validation error', () => {
    //given
    const error = {
      error: {
        actual: '20.20.2000',
        min: '21.20.2000',
      },
    };
    //when
    const result = service.getValidation(error);
    //then
    expect(result).toBeDefined();
    expect(result.length).toBe(1);
    expect(result[0].error).toBe('VALIDATION.ERROR');
    expect(result[0].value).toBeDefined();
    expect(result[0].value.actual).toBe('20.20.2000');
    expect(result[0].value.min).toBe('21.20.2000');
  });

  it('should not add any validation error', () => {
    //when
    const result = service.getValidation(null);
    //then
    expect(result).toBeDefined();
    expect(result.length).toBe(0);
  });

  it('should return validationError with validFrom and validTo', () => {
    ///given
    const error: ValidationError = {
      error: 'VALIDATION.DATE_RANGE_ERROR',
      value: {
        date: {
          validFrom: moment('2000-10-09T22:00:00.000Z'),
          validTo: moment('2000-09-08T22:00:00.000Z'),
        },
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
    const error: ValidationError = {
      error: 'VALIDATION.DATE_RANGE_ERROR',
      value: {
        min: moment('2000-10-09T22:00:00.000Z'),
      },
    };

    //when
    const result = service.displayDate(error);
    //then
    expect(result).toBeDefined();
    expect(result).toBe('10.10.2000');
  });

  it('should return validationError with max', () => {
    ///given
    const error: ValidationError = {
      error: 'VALIDATION.DATE_RANGE_ERROR',
      value: {
        max: moment('2000-10-09T22:00:00.000Z'),
      },
    };

    //when
    const result = service.displayDate(error);
    //then
    expect(result).toBeDefined();
    expect(result).toBe('10.10.2000');
  });
});
