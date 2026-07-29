import { signal } from '@angular/core';
import { TestBed } from '@angular/core/testing';
import { form, SchemaPath } from '@angular/forms/signals';
import moment from 'moment';
import { beforeEach, describe, expect, it } from 'vitest';
import { FormValidators } from './form-validators.service';
import { InterpolationParameters, TranslateService } from '@ngx-translate/core';
import { mock } from 'vitest-mock-extended';

describe('FormValidators', () => {
  let formValidators: FormValidators;

  const translateService = mock<TranslateService>();
  translateService.instant.mockImplementation((key: string | string[], params?: InterpolationParameters) =>
    params ? `${key}|${JSON.stringify(params)}` : key
  );

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [FormValidators, { provide: TranslateService, useValue: translateService }],
    });

    formValidators = TestBed.inject(FormValidators);
  });

  const createStringField = (initialValue: string, apply: (path: SchemaPath<string>) => void) =>
    TestBed.runInInjectionContext(() => {
      const model = signal({ value: initialValue });
      const testForm = form(model, (schemaPath) => apply(schemaPath.value));
      return testForm.value;
    });

  describe('required', () => {
    it('should be valid when a value is present', () => {
      const field = createStringField('Atlas', (path) => formValidators.required(path));

      expect(field().valid()).toBe(true);
    });

    it('should be invalid and expose the translated message when empty', () => {
      const field = createStringField('', (path) => formValidators.required(path));

      expect(field().valid()).toBe(false);
      expect(field().errors()[0].message).toBe('VALIDATION.REQUIRED');
    });
  });

  describe('maxLength', () => {
    it('should be valid when the value is within the allowed length', () => {
      const field = createStringField('12345', (path) => formValidators.maxLength(path, 5));

      expect(field().valid()).toBe(true);
    });

    it('should be invalid and include the configured length in the message when too long', () => {
      const field = createStringField('123456', (path) => formValidators.maxLength(path, 5));

      expect(field().valid()).toBe(false);
      expect(field().errors()[0].message).toBe(`VALIDATION.MAXLENGTH|${JSON.stringify({ length: 5 })}`);
    });
  });

  describe('email', () => {
    it('should be valid for a correct email address', () => {
      const field = createStringField('muster@sbb.ch', (path) => formValidators.email(path));

      expect(field().valid()).toBe(true);
    });

    it('should be invalid and expose the translated message for an incorrect email address', () => {
      const field = createStringField('not-an-email', (path) => formValidators.email(path));

      expect(field().valid()).toBe(false);
      expect(field().errors()[0].message).toBe(
        `VALIDATION.PATTERN|${JSON.stringify({ allowedChars: 'E-Mail Format' })}`
      );
    });
  });

  describe('numeric', () => {
    it('should be valid for digits only', () => {
      const field = createStringField('12345', (path) => formValidators.numeric(path));

      expect(field().valid()).toBe(true);
    });

    it('should be invalid and expose the translated message for non-digit characters', () => {
      const field = createStringField('123a5', (path) => formValidators.numeric(path));

      expect(field().valid()).toBe(false);
      expect(field().errors()[0].message).toBe(`VALIDATION.PATTERN|${JSON.stringify({ allowedChars: '0-9' })}`);
    });
  });

  describe('ttfnNumber', () => {
    it('should be valid for digits only', () => {
      const field = createStringField('12345', (path) => formValidators.ttfnNumber(path));

      expect(field().valid()).toBe(true);
    });

    it('should be valid for the allowed special characters', () => {
      const field = createStringField('12.34SN', (path) => formValidators.ttfnNumber(path));

      expect(field().valid()).toBe(true);
    });

    it('should be valid for an empty value', () => {
      const field = createStringField('', (path) => formValidators.ttfnNumber(path));

      expect(field().valid()).toBe(true);
    });

    it('should be invalid and expose the translated message for disallowed characters', () => {
      const field = createStringField('12a34', (path) => formValidators.ttfnNumber(path));

      expect(field().valid()).toBe(false);
      expect(field().errors()[0].message).toBe(`VALIDATION.PATTERN|${JSON.stringify({ allowedChars: '.0-9SN' })}`);
    });
  });

  describe('atLeastOneSelected', () => {
    const createArrayField = <T>(initialValue: T[], apply: (path: SchemaPath<T[]>) => void) =>
      TestBed.runInInjectionContext(() => {
        const model = signal({ value: initialValue });
        const testForm = form(model, (schemaPath) => apply(schemaPath.value));
        return testForm.value;
      });

    it('should be valid when at least one element is selected', () => {
      const field = createArrayField(['a'], (path) => formValidators.atLeastOneSelected(path));

      expect(field().valid()).toBe(true);
    });

    it('should be invalid and expose the translated message when the array is empty', () => {
      const field = createArrayField<string>([], (path) => formValidators.atLeastOneSelected(path));

      expect(field().valid()).toBe(false);
      expect(field().errors()[0].message).toBe('VALIDATION.REQUIRED');
    });
  });

  describe('iso88591', () => {
    it('should be valid for ISO-8859-1 characters', () => {
      const field = createStringField('Muster AG', (path) => formValidators.iso88591(path));

      expect(field().valid()).toBe(true);
    });

    it('should be valid for digits', () => {
      const field = createStringField('1234567890', (path) => formValidators.iso88591(path));

      expect(field().valid()).toBe(true);
    });

    it('should be valid for letters', () => {
      const field = createStringField('abcXYZ', (path) => formValidators.iso88591(path));

      expect(field().valid()).toBe(true);
    });

    it('should be valid for a mix of letters and digits', () => {
      const field = createStringField('Muster AG 123', (path) => formValidators.iso88591(path));

      expect(field().valid()).toBe(true);
    });

    it('should be valid for ISO-8859-1 letters with umlauts', () => {
      const field = createStringField('Müller Straße àéç 42', (path) => formValidators.iso88591(path));

      expect(field().valid()).toBe(true);
    });

    it('should be invalid and expose the translated message for characters outside ISO-8859-1', () => {
      const field = createStringField('Müster 😀', (path) => formValidators.iso88591(path));

      expect(field().valid()).toBe(false);
      expect(field().errors()[0].message).toBe(`VALIDATION.PATTERN|${JSON.stringify({ allowedChars: 'ISO-8859-1' })}`);
    });
  });

  describe('blankOrEmptySpaceSurrounding', () => {
    it('should be valid for a non-blank value without surrounding whitespace', () => {
      const field = createStringField('Atlas', (path) => formValidators.blankOrEmptySpaceSurrounding(path));

      expect(field().valid()).toBe(true);
    });

    it('should be valid for an empty value', () => {
      const field = createStringField('', (path) => formValidators.blankOrEmptySpaceSurrounding(path));

      expect(field().valid()).toBe(true);
    });

    it('should be invalid and expose the translated message when the value starts with a space', () => {
      const field = createStringField(' Atlas', (path) => formValidators.blankOrEmptySpaceSurrounding(path));

      expect(field().valid()).toBe(false);
      expect(field().errors()[0].message).toBe('VALIDATION.WHITESPACES');
    });

    it('should be invalid and expose the translated message when the value ends with a space', () => {
      const field = createStringField('Atlas ', (path) => formValidators.blankOrEmptySpaceSurrounding(path));

      expect(field().valid()).toBe(false);
      expect(field().errors()[0].message).toBe('VALIDATION.WHITESPACES');
    });

    it('should be invalid and expose the translated blank message when the value only contains whitespace', () => {
      const field = createStringField('   ', (path) => formValidators.blankOrEmptySpaceSurrounding(path));

      expect(field().valid()).toBe(false);
      expect(field().errors()[0].message).toBe('VALIDATION.BLANK');
    });
  });

  describe('validToAfterOrEqualValidFrom', () => {
    const createDateRangeField = (validFrom: moment.Moment | null, validTo: moment.Moment | null) =>
      TestBed.runInInjectionContext(() =>
        form(signal({ validFrom, validTo }), (schemaPath) => formValidators.validToAfterOrEqualValidFrom(schemaPath))
      );

    it('should be valid when validFrom is before validTo', () => {
      const testForm = createDateRangeField(moment('2020-01-01'), moment('2020-01-31'));

      expect(testForm.validFrom().valid()).toBe(true);
      expect(testForm.validTo().valid()).toBe(true);
    });

    it('should be valid when validFrom equals validTo', () => {
      const date = moment('2020-01-01');
      const testForm = createDateRangeField(date, date.clone());

      expect(testForm.validFrom().valid()).toBe(true);
      expect(testForm.validTo().valid()).toBe(true);
    });

    it('should be valid when validFrom or validTo is not set', () => {
      const testForm = createDateRangeField(null, null);

      expect(testForm.validFrom().valid()).toBe(true);
      expect(testForm.validTo().valid()).toBe(true);
    });

    it('should be invalid on both fields and expose the translated message when validFrom is after validTo', () => {
      const validFrom = moment('2020-02-01');
      const validTo = moment('2020-01-01');
      const testForm = createDateRangeField(validFrom, validTo);

      const expectedMessage = `VALIDATION.DATE_ORDER_ERROR|${JSON.stringify({
        validFrom: validFrom.format('DD.MM.yyyy'),
        validTo: validTo.format('DD.MM.yyyy'),
      })}`;

      expect(testForm.validFrom().valid()).toBe(false);
      expect(testForm.validFrom().errors()[0].message).toBe(expectedMessage);

      expect(testForm.validTo().valid()).toBe(false);
      expect(testForm.validTo().errors()[0].message).toBe(expectedMessage);
    });
  });
});
