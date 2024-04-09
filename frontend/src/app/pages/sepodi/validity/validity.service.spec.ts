import { TestBed } from '@angular/core/testing';

import { ValidityService } from './validity.service';
import {FormControl, FormGroup} from "@angular/forms";
import {Validity} from "../../model/validity";
import moment from "moment";

describe('ValidityService', () => {
  let service: ValidityService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ValidityService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should initialize validity with form values', () => {
    const form = new FormGroup({
      validFrom: new FormControl(moment('2023-01-01')),
      validTo: new FormControl(moment('2023-12-31')),
    });

    const expectedResult = {
      initValidTo: moment('2023-12-31'),
      initValidFrom: moment('2023-01-01'),
      formValidTo: undefined,
      formValidFrom: undefined,
    };

    const result = service.initValidity(form);

    expect(result).toEqual(expectedResult);
  });

  it('should update validity with form values', () => {
    const validity:Validity = {
      initValidTo: moment('2023-01-01'),
      initValidFrom: moment('2023-01-01'),
      formValidTo: undefined,
      formValidFrom: undefined,
    };

    const form = new FormGroup({
      validFrom: new FormControl(moment('2024-01-01')),
      validTo: new FormControl(moment('2024-12-31')),
    });

    const expectedResult = {
      ...validity,
      formValidTo: moment('2024-12-31'),
      formValidFrom: moment('2024-01-01'),
    };

    const result = service.formValidity(validity, form);

    expect(result).toEqual(expectedResult);
  });
});
