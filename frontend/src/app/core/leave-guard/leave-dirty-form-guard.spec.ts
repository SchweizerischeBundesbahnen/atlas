import { beforeEach, describe, expect, it, type Mocked, vi } from 'vitest';
import { TestBed } from '@angular/core/testing';
import { LeaveDirtyFormGuard } from './leave-dirty-form-guard.service';
import { DialogService } from '../components/dialog/dialog.service';
import { ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import { FormGroup } from '@angular/forms';

describe('LeaveDirtyFormGuard', () => {
  const route = {} as ActivatedRouteSnapshot;
  const confirmPayload = {
    title: 'DIALOG.DISCARD_CHANGES_TITLE',
    message: 'DIALOG.LEAVE_SITE',
  } as const;
  type DialogServiceMock = Mocked<Pick<DialogService, 'openDialogDataWithConfirmationResult'>>;
  let dialogService: DialogServiceMock;
  let leaveDirtyFormGuard: LeaveDirtyFormGuard;

  beforeEach(() => {
    // Mocking: create the DialogService stub with only the used member
    dialogService = {
      openDialogDataWithConfirmationResult: vi.fn().mockName('DialogService.confirm'),
    };

    // Config: provide the guard and mocked dependencies via TestBed
    TestBed.configureTestingModule({
      providers: [{ provide: DialogService, useValue: dialogService }, LeaveDirtyFormGuard],
    });

    // Arrangement: inject the guard through TestBed so DI is honored
    leaveDirtyFormGuard = TestBed.inject(LeaveDirtyFormGuard);
  });

  it('should be created', () => {
    expect(leaveDirtyFormGuard).toBeTruthy();
  });

  it('should allow routing if form is not dirty', () => {
    const currentState = {
      url: '/line-directory/lines/add',
    } as RouterStateSnapshot;
    const nextState = {
      url: '/line-directory/sublines',
    } as RouterStateSnapshot;

    const form = new FormGroup({});

    expect(leaveDirtyFormGuard.canDeactivate({ form: form }, route, currentState, nextState)).toBeTruthy();
  });

  it('should allow routing by creation with same url if form is not dirty', () => {
    const currentState = {
      url: '/prm-directory/stop-points/ch:1:sloid:319/stop-point',
    } as RouterStateSnapshot;
    const nextState = {
      url: '/prm-directory/stop-points/ch:1:sloid:319/stop-point',
    } as RouterStateSnapshot;

    const form = new FormGroup({});
    form.markAsDirty();

    expect(leaveDirtyFormGuard.canDeactivate({ form: form }, route, currentState, nextState)).toBeTruthy();
  });

  it('should display confirmation dialog on dirty form', () => {
    const currentState = {
      url: '/line-directory/lines/add',
    } as RouterStateSnapshot;
    const nextState = {
      url: '/line-directory/sublines',
    } as RouterStateSnapshot;

    const form = new FormGroup({});
    form.markAsDirty();

    leaveDirtyFormGuard.canDeactivate({ form: form }, route, currentState, nextState);
    expect(dialogService.openDialogDataWithConfirmationResult).toHaveBeenCalledExactlyOnceWith(confirmPayload);
  });

  it('should display confirmation dialog when leaving dirty service point creation', () => {
    const currentState = {
      url: '/service-point-directory/service-points',
    } as RouterStateSnapshot;
    const nextState = {
      url: '/service-point-directory',
    } as RouterStateSnapshot;

    const form = new FormGroup({});
    form.markAsDirty();

    leaveDirtyFormGuard.canDeactivate({ form: form }, route, currentState, nextState);
    expect(dialogService.openDialogDataWithConfirmationResult).toHaveBeenCalledExactlyOnceWith(confirmPayload);
  });

  it('should not display confirmation dialog when creating service point', () => {
    const currentState = {
      url: '/service-point-directory/service-points',
    } as RouterStateSnapshot;
    const nextState = {
      url: '/service-point-directory/service-points/8510159/service-point',
    } as RouterStateSnapshot;

    const form = new FormGroup({});
    form.markAsDirty();

    leaveDirtyFormGuard.canDeactivate({ form: form }, route, currentState, nextState);
    expect(dialogService.openDialogDataWithConfirmationResult).not.toHaveBeenCalled();
  });

  it('should display confirmation dialog when leaving dirty contact point creation with detail subtab', () => {
    const currentState = {
      url: '/prm-directory/stop-points/ch:1:sloid:7000/contact-points/add/detail',
    } as RouterStateSnapshot;
    const nextState = { url: '/prm-directory' } as RouterStateSnapshot;

    const form = new FormGroup({});
    form.markAsDirty();

    leaveDirtyFormGuard.canDeactivate({ form: form }, route, currentState, nextState);
    expect(dialogService.openDialogDataWithConfirmationResult).toHaveBeenCalledExactlyOnceWith(confirmPayload);
  });
});
