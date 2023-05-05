import { FormControl } from '@angular/forms';
import moment from 'moment';
import { NewHearingYearValidator } from './new-hearing-year-validator';

describe('NewHearingYearValidator', () => {
  it('should return validation error when hearingFrom/hearingTo is 2 years before', () => {
    //given
    const timetableHearingYear = new FormControl(2024);
    const hearingFrom = moment('01.06.2022', 'DD.MM.YYYY');
    const hearingTo = moment('30.06.2022', 'DD.MM.YYYY');
    const hearingFromForm = new FormControl(hearingFrom);
    const hearingToForm = new FormControl(hearingTo);
    //when
    NewHearingYearValidator.validate(timetableHearingYear, hearingFromForm, hearingToForm);
    //then
    expect(hearingFromForm.errors).toBeDefined();
    const dateRangeErrorValidFrom = hearingFromForm.errors?.['one_year_before'];
    expect(dateRangeErrorValidFrom).toBeDefined();
    expect(dateRangeErrorValidFrom.number).toBe(2023);

    expect(hearingToForm.errors).toBeDefined();
    const dateRangeErrorValidTo = hearingToForm.errors?.['one_year_before'];
    expect(dateRangeErrorValidTo).toBeDefined();
    expect(dateRangeErrorValidTo.number).toBe(2023);
  });

  it('should return validation success when hearingFrom/hearingTo is 1 years before', () => {
    //given
    const timetableHearingYear = new FormControl(2023);
    const hearingFrom = moment('01.06.2022', 'DD.MM.YYYY');
    const hearingTo = moment('30.06.2022', 'DD.MM.YYYY');
    const hearingFromForm = new FormControl(hearingFrom);
    const hearingToForm = new FormControl(hearingTo);
    //when
    NewHearingYearValidator.validate(timetableHearingYear, hearingFromForm, hearingToForm);
    //then
    expect(hearingFromForm.errors).toBeFalsy();
    expect(hearingToForm.errors).toBeFalsy();
  });
});
