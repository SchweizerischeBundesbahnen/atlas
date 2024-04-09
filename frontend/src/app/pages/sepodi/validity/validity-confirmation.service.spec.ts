import { TestBed } from '@angular/core/testing';
import { ValidityConfirmationService } from './validity-confirmation.service';
import { of } from 'rxjs';
import { DialogService } from '../../../core/components/dialog/dialog.service';
import moment from 'moment';
import SpyObj = jasmine.SpyObj;
import { BERN } from '../../../../test/data/service-point';

describe('ValidityConfirmationService', () => {
  let service: ValidityConfirmationService;

  let dialogService: SpyObj<DialogService>;

  const validity = {
    formValidTo: moment('2024-01-01'),
    initValidTo: moment('2024-01-01'),
    formValidFrom: moment('2023-01-01'),
    initValidFrom: moment('2023-01-01')
  };

  beforeEach(() => {
    dialogService = jasmine.createSpyObj('dialogService', ['confirm']);
    dialogService.confirm.and.returnValue(of(true));

    TestBed.configureTestingModule({
      providers: [{ provide: DialogService, useValue: dialogService }],
    });
    service = TestBed.inject(ValidityConfirmationService);
  });

  it('should confirm', () => {
    //when
    service.confirmValidityOverServicePoint(BERN, moment('1900-01-01'), moment('2020-01-01'));
    //then
    expect(dialogService.confirm).toHaveBeenCalled();
  });

  it('should not need confirmation', () => {
    //when
    service.confirmValidityOverServicePoint(BERN, moment('2000-01-01'), moment('2020-01-01'));
    //then
    expect(dialogService.confirm).not.toHaveBeenCalled();
  });

  it('should open popup when formValidTo and formValidFrom didnt change', () => {
    //when

    service.confirmValidity(validity)
    //then
    expect(dialogService.confirm).toHaveBeenCalled();
  });

  it('should not open popup, when formValidTo and formValidFrom did change', (done: DoneFn) => {
    const validity = {
      formValidTo: moment('2024-01-01'),
      initValidTo: moment('2023-01-01'),
      formValidFrom: moment('2023-01-01'),
      initValidFrom: moment('2021-01-01')
    };

    service.confirmValidity(validity).subscribe(result => {
      expect(result).toBeTrue();
      done();
    });

    expect(dialogService.confirm).not.toHaveBeenCalled();
  });

});
