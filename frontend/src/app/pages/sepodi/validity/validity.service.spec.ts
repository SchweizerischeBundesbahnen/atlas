import { TestBed } from '@angular/core/testing';
import { beforeEach, describe, expect, it, type Mocked, vi } from 'vitest';
import { ValidityService } from './validity.service';
import { FormControl, FormGroup } from '@angular/forms';
import moment from 'moment';
import { firstValueFrom, of } from 'rxjs';
import { DialogService } from '../../../core/components/dialog/dialog.service';

describe('ValidityService', () => {
  let service: ValidityService;
  let dialogService: Mocked<Pick<DialogService, 'openDialogDataWithConfirmationResult'>>;

  beforeEach(() => {
    dialogService = {
      openDialogDataWithConfirmationResult: vi.fn().mockReturnValue(of(true)),
    };

    TestBed.configureTestingModule({
      providers: [ValidityService, { provide: DialogService, useValue: dialogService }],
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

  it('should confirm validity unchanged with a dialog', async () => {
    service.validity = {
      initValidFrom: moment('2023-01-01'),
      initValidTo: moment('2023-12-31'),
      formValidFrom: moment('2023-01-01'),
      formValidTo: moment('2023-12-31'),
    };

    const result = await firstValueFrom(service.confirmValidityDialog());
    expect(result).toBe(true);
    expect(dialogService.openDialogDataWithConfirmationResult).toHaveBeenCalled();
  });

  it('should validate and disable form correctly', async () => {
    const updateFunctionSpy = vi.fn();
    const form = new FormGroup({});
    service.validity = {
      initValidFrom: moment('2023-01-01'),
      initValidTo: moment('2023-12-31'),
      formValidFrom: moment('2023-01-01'),
      formValidTo: moment('2023-12-31'),
    };
    service.validateAndDisableCustom(updateFunctionSpy, () => form.disable());

    expect(form.disabled).toBe(true);
    expect(updateFunctionSpy).toHaveBeenCalled();
    expect(dialogService.openDialogDataWithConfirmationResult).toHaveBeenCalled();
  });

  it('should validate and disable function correctly and call update', async () => {
    const updateFunctionSpy = vi.fn();
    const disableFunctionSpy = vi.fn();

    service.validity = {
      initValidFrom: moment('2023-01-01'),
      initValidTo: moment('2023-12-31'),
      formValidFrom: moment('2023-01-01'),
      formValidTo: moment('2023-12-31'),
    };
    service.validateAndDisableCustom(updateFunctionSpy, disableFunctionSpy);

    expect(updateFunctionSpy).toHaveBeenCalled();
    expect(disableFunctionSpy).toHaveBeenCalled();
    expect(dialogService.openDialogDataWithConfirmationResult).toHaveBeenCalled();
  });
});
