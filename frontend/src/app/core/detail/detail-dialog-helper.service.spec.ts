import { TestBed } from '@angular/core/testing';
import { signal, WritableSignal } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { firstValueFrom, of } from 'rxjs';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import { mock, type MockProxy } from 'vitest-mock-extended';
import { DialogService } from '../components/dialog/dialog.service';
import {
  DetailDialogHelperService,
  DetailWithCancelEdit,
  SignalDetailWithCancelEdit,
} from './detail-dialog-helper.service';

describe('DetailDialogHelperService', () => {
  let service: DetailDialogHelperService;
  let dialogServiceMock: MockProxy<DialogService>;

  beforeEach(() => {
    dialogServiceMock = mock<DialogService>();
    dialogServiceMock.openDialogDataWithConfirmationResult.mockReturnValue(of(true));

    TestBed.configureTestingModule({
      providers: [DetailDialogHelperService, { provide: DialogService, useValue: dialogServiceMock }],
    });

    service = TestBed.inject(DetailDialogHelperService);
  });

  const createDirtyForm = (dirty: boolean): FormGroup => {
    const form = new FormGroup({ value: new FormControl('') });
    if (dirty) {
      form.markAsDirty();
    }
    return form;
  };

  const createFormGroupDetail = (overrides: Partial<DetailWithCancelEdit> = {}): DetailWithCancelEdit => ({
    isNew: false,
    form: createDirtyForm(true),
    back: vi.fn(),
    ngOnInit: vi.fn(),
    ...overrides,
  });

  interface TestFormModel {
    name: string;
  }

  const createSignalDetail = (
    overrides: Partial<SignalDetailWithCancelEdit<TestFormModel>> = {}
  ): SignalDetailWithCancelEdit<TestFormModel> => {
    const formModel: WritableSignal<TestFormModel> = signal({ name: 'changed' });
    const editMode: WritableSignal<boolean> = signal(true);
    return {
      isNew: false,
      emptyFormValue: { name: '' },
      formModel,
      editMode,
      dirty: signal(true),
      back: vi.fn(),
      ngOnInit: vi.fn(),
      ...overrides,
    };
  };

  describe('showCancelEditDialog (FormGroup)', () => {
    it('should reset the form and navigate back when confirmed and detail is new', () => {
      const detail = createFormGroupDetail({ isNew: true });
      const resetSpy = vi.spyOn(detail.form, 'reset');
      dialogServiceMock.openDialogDataWithConfirmationResult.mockReturnValue(of(true));

      service.showCancelEditDialog(detail);

      expect(resetSpy).toHaveBeenCalled();
      expect(detail.back).toHaveBeenCalled();
      expect(detail.ngOnInit).not.toHaveBeenCalled();
    });

    it('should re-init and disable the form when confirmed', () => {
      const detail = createFormGroupDetail({ isNew: false });
      const disableSpy = vi.spyOn(detail.form, 'disable');
      dialogServiceMock.openDialogDataWithConfirmationResult.mockReturnValue(of(true));

      service.showCancelEditDialog(detail);

      expect(detail.ngOnInit).toHaveBeenCalled();
      expect(disableSpy).toHaveBeenCalled();
      expect(detail.back).not.toHaveBeenCalled();
    });

    it('should do nothing when the user does not confirm', () => {
      const detail = createFormGroupDetail({ isNew: true });
      const resetSpy = vi.spyOn(detail.form, 'reset');
      dialogServiceMock.openDialogDataWithConfirmationResult.mockReturnValue(of(false));

      service.showCancelEditDialog(detail);

      expect(resetSpy).not.toHaveBeenCalled();
      expect(detail.back).not.toHaveBeenCalled();
      expect(detail.ngOnInit).not.toHaveBeenCalled();
    });

    it('should skip the dialog and proceed directly when the form is pristine', () => {
      const detail = createFormGroupDetail({ isNew: true, form: createDirtyForm(false) });

      service.showCancelEditDialog(detail);

      expect(dialogServiceMock.openDialogDataWithConfirmationResult).not.toHaveBeenCalled();
      expect(detail.back).toHaveBeenCalled();
    });
  });

  describe('confirmLeave (FormGroup)', () => {
    it('should open the leave dialog when the form is dirty', async () => {
      const detail = createFormGroupDetail({ form: createDirtyForm(true) });
      dialogServiceMock.openDialogDataWithConfirmationResult.mockReturnValue(of(true));

      const confirmed = await firstValueFrom(service.confirmLeave(detail));

      expect(dialogServiceMock.openDialogDataWithConfirmationResult).toHaveBeenCalled();
      expect(confirmed).toBe(true);
    });

    it('should resolve to true without opening the dialog when the form is pristine', async () => {
      const detail = createFormGroupDetail({ form: createDirtyForm(false) });

      const confirmed = await firstValueFrom(service.confirmLeave(detail));

      expect(dialogServiceMock.openDialogDataWithConfirmationResult).not.toHaveBeenCalled();
      expect(confirmed).toBe(true);
    });
  });

  describe('openCancelEditDialog (Signal)', () => {
    it('should reset the form model and navigate back when confirmed and detail is new', () => {
      const detail = createSignalDetail({ isNew: true });
      dialogServiceMock.openDialogDataWithConfirmationResult.mockReturnValue(of(true));

      service.openCancelEditDialog(detail);

      expect(detail.formModel()).toEqual(detail.emptyFormValue);
      expect(detail.back).toHaveBeenCalled();
      expect(detail.ngOnInit).not.toHaveBeenCalled();
    });

    it('should re-init and leave edit mode when confirmed and detail is existing', () => {
      const detail = createSignalDetail({ isNew: false });
      dialogServiceMock.openDialogDataWithConfirmationResult.mockReturnValue(of(true));

      service.openCancelEditDialog(detail);

      expect(detail.ngOnInit).toHaveBeenCalled();
      expect(detail.editMode()).toBe(false);
      expect(detail.back).not.toHaveBeenCalled();
    });

    it('should do nothing when the user does not confirm', () => {
      const detail = createSignalDetail({ isNew: true });
      dialogServiceMock.openDialogDataWithConfirmationResult.mockReturnValue(of(false));

      service.openCancelEditDialog(detail);

      expect(detail.formModel()).toEqual({ name: 'changed' });
      expect(detail.back).not.toHaveBeenCalled();
      expect(detail.ngOnInit).not.toHaveBeenCalled();
    });

    it('should skip the dialog and proceed directly when not dirty', () => {
      const detail = createSignalDetail({ isNew: true, dirty: signal(false) });

      service.openCancelEditDialog(detail);

      expect(dialogServiceMock.openDialogDataWithConfirmationResult).not.toHaveBeenCalled();
      expect(detail.formModel()).toEqual(detail.emptyFormValue);
      expect(detail.back).toHaveBeenCalled();
    });
  });

  describe('confirmLeaving (Signal)', () => {
    it('should open the leave dialog when dirty', async () => {
      const detail = createSignalDetail({ dirty: signal(true) });
      dialogServiceMock.openDialogDataWithConfirmationResult.mockReturnValue(of(true));

      const confirmed = await firstValueFrom(service.confirmLeaving(detail));

      expect(dialogServiceMock.openDialogDataWithConfirmationResult).toHaveBeenCalled();
      expect(confirmed).toBe(true);
    });

    it('should resolve to true without opening the dialog when not dirty', async () => {
      const detail = createSignalDetail({ dirty: signal(false) });

      const confirmed = await firstValueFrom(service.confirmLeaving(detail));

      expect(dialogServiceMock.openDialogDataWithConfirmationResult).not.toHaveBeenCalled();
      expect(confirmed).toBe(true);
    });
  });

  describe('confirmLeaveDirtyForm', () => {
    it('should open the leave dialog when the form is dirty', async () => {
      dialogServiceMock.openDialogDataWithConfirmationResult.mockReturnValue(of(false));

      const confirmed = await firstValueFrom(service.confirmLeaveDirtyForm(createDirtyForm(true)));

      expect(dialogServiceMock.openDialogDataWithConfirmationResult).toHaveBeenCalled();
      expect(confirmed).toBe(false);
    });

    it('should resolve to true without opening the dialog when the form is pristine', async () => {
      const confirmed = await firstValueFrom(service.confirmLeaveDirtyForm(createDirtyForm(false)));

      expect(dialogServiceMock.openDialogDataWithConfirmationResult).not.toHaveBeenCalled();
      expect(confirmed).toBe(true);
    });
  });

  describe('openLeaveDialog', () => {
    it('should open the dialog with the discard changes labels', () => {
      service.openLeaveDialog();

      expect(dialogServiceMock.openDialogDataWithConfirmationResult).toHaveBeenCalledWith({
        title: 'DIALOG.DISCARD_CHANGES_TITLE',
        message: 'DIALOG.LEAVE_SITE',
      });
    });
  });

  describe('confirmWarning', () => {
    it('should invoke the callback when the warning is confirmed', () => {
      const onConfirm = vi.fn();
      dialogServiceMock.openDialogDataWithConfirmationResult.mockReturnValue(of(true));

      service.confirmWarning({ message: 'SOME.WARNING' }, onConfirm);

      expect(dialogServiceMock.openDialogDataWithConfirmationResult).toHaveBeenCalledWith({
        title: 'DIALOG.WARNING',
        cancelText: 'DIALOG.BACK',
        message: 'SOME.WARNING',
      });
      expect(onConfirm).toHaveBeenCalled();
    });

    it('should not invoke the callback when the warning is not confirmed', () => {
      const onConfirm = vi.fn();
      dialogServiceMock.openDialogDataWithConfirmationResult.mockReturnValue(of(false));

      service.confirmWarning({ message: 'SOME.WARNING', confirmText: 'CONFIRM' }, onConfirm);

      expect(onConfirm).not.toHaveBeenCalled();
    });

    it('should merge provided labels over the defaults', () => {
      const onConfirm = vi.fn();
      dialogServiceMock.openDialogDataWithConfirmationResult.mockReturnValue(of(true));

      service.confirmWarning({ message: 'SOME.WARNING', confirmText: 'CONFIRM' }, onConfirm);

      expect(dialogServiceMock.openDialogDataWithConfirmationResult).toHaveBeenCalledWith({
        title: 'DIALOG.WARNING',
        cancelText: 'DIALOG.BACK',
        message: 'SOME.WARNING',
        confirmText: 'CONFIRM',
      });
    });
  });
});
