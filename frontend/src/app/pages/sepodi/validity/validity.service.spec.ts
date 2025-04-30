import { fakeAsync, TestBed } from '@angular/core/testing';
import { ValidityService } from './validity.service';
import { FormControl, FormGroup } from '@angular/forms';
import moment from 'moment';
import { of } from 'rxjs';
import { DialogService } from '../../../core/components/dialog/dialog.service';
import SpyObj = jasmine.SpyObj;

describe('ValidityService', () => {
  let service: ValidityService;
  const dialogService: SpyObj<DialogService> = jasmine.createSpyObj(
    'dialogService',
    ['confirm']
  );
  dialogService.confirm.and.returnValue(of(true));

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        ValidityService,
        { provide: DialogService, useValue: dialogService },
      ],
    });
    service = TestBed.inject(ValidityService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should initialize validity correctly', () => {
    const form = new FormGroup({
      validFrom: new FormControl(moment('2023-01-01')),
      validTo: new FormControl(moment('2023-12-31')),
    });

    service.initValidity(form);

    expect(service.validity).toEqual({
      initValidFrom: moment('2023-01-01'),
      initValidTo: moment('2023-12-31'),
      formValidFrom: undefined,
      formValidTo: undefined,
    });
  });

  it('should update validity correctly', () => {
    service.initValidity(
      new FormGroup({
        validFrom: new FormControl(moment('2023-01-01')),
        validTo: new FormControl(moment('2023-12-31')),
      })
    );

    const updateForm = new FormGroup({
      validFrom: new FormControl(moment('2024-01-01')),
      validTo: new FormControl(moment('2024-12-31')),
    });

    service.updateValidity(updateForm);

    expect(service.validity.formValidFrom).toEqual(moment('2024-01-01'));
    expect(service.validity.formValidTo).toEqual(moment('2024-12-31'));
  });

  it('should confirm validity unchanged with a dialog', fakeAsync(() => {
    service.validity = {
      initValidFrom: moment('2023-01-01'),
      initValidTo: moment('2023-12-31'),
      formValidFrom: moment('2023-01-01'),
      formValidTo: moment('2023-12-31'),
    };

    service.confirmValidityDialog().subscribe((result) => {
      expect(result).toBeTrue();
      expect(dialogService.confirm).toHaveBeenCalled();
    });
  }));

  it('should validate and disable form correctly', fakeAsync(() => {
    const updateFunctionSpy = jasmine.createSpy();
    const form = new FormGroup({});
    service.validity = {
      initValidFrom: moment('2023-01-01'),
      initValidTo: moment('2023-12-31'),
      formValidFrom: moment('2023-01-01'),
      formValidTo: moment('2023-12-31'),
    };
    service.validateAndDisableCustom(updateFunctionSpy, () => form.disable());

    expect(form.disabled).toBeTrue();
    expect(updateFunctionSpy).toHaveBeenCalled();
    expect(dialogService.confirm).toHaveBeenCalled();
  }));

  it('should validate and disable function correctly and call update', fakeAsync(() => {
    const updateFunctionSpy = jasmine.createSpy();
    const disableFunctionSpy = jasmine.createSpy();

    service.validity = {
      initValidFrom: moment('2023-01-01'),
      initValidTo: moment('2023-12-31'),
      formValidFrom: moment('2023-01-01'),
      formValidTo: moment('2023-12-31'),
    };
    service.validateAndDisableCustom(updateFunctionSpy, disableFunctionSpy);

    expect(updateFunctionSpy).toHaveBeenCalled();
    expect(disableFunctionSpy).toHaveBeenCalled();
    expect(dialogService.confirm).toHaveBeenCalled();
  }));
});
