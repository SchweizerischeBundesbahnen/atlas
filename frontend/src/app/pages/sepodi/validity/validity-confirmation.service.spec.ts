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

  let momentMockFormValidTo = jasmine.createSpyObj('Moment', ['isSame']);
  let momentMockFormValidFrom = jasmine.createSpyObj('Moment', ['isSame']);
  let momentMockInitValidTo = jasmine.createSpyObj('Moment', ['isSame']);
  let momentMockInitValidFrom = jasmine.createSpyObj('Moment', ['isSame']);

  beforeEach(() => {
    dialogService = jasmine.createSpyObj('dialogService', ['confirm']);
    dialogService.confirm.and.returnValue(of(true));

    momentMockFormValidTo = jasmine.createSpyObj('moment', ['isSame']);
    momentMockFormValidFrom = jasmine.createSpyObj('moment', ['isSame']);
    momentMockInitValidTo = jasmine.createSpyObj('moment', ['isSame']);
    momentMockInitValidFrom = jasmine.createSpyObj('moment', ['isSame']);

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
    momentMockFormValidTo.isSame.and.returnValue(true);
    momentMockFormValidFrom.isSame.and.returnValue(true);
    service.confirmValidity(momentMockFormValidTo,momentMockFormValidFrom, momentMockInitValidTo, momentMockInitValidFrom)
    //then
    expect(dialogService.confirm).toHaveBeenCalled();
  });

  it('should not open popup, when formValidTo and formValidFrom did change', (done: DoneFn) => {
    momentMockFormValidTo.isSame.and.returnValue(false);
    momentMockFormValidFrom.isSame.and.returnValue(false);

    service.confirmValidity(momentMockFormValidTo, momentMockFormValidFrom, momentMockInitValidTo, momentMockInitValidFrom).subscribe(result => {
      expect(result).toBeTrue();
      done();
    });

    expect(dialogService.confirm).not.toHaveBeenCalled();
  });

});
