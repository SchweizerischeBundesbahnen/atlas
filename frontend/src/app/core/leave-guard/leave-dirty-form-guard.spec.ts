import { TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { LeaveDirtyFormGuard } from './leave-dirty-form-guard.service';
import { DialogService } from '../components/dialog/dialog.service';
import { ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import { FormGroup } from '@angular/forms';

const dialogServiceSpy = jasmine.createSpyObj('DialogService', ['confirm']);
const route = {} as ActivatedRouteSnapshot;

describe('LeaveDirtyFormGuard', () => {
  let leaveDirtyFormGuard: LeaveDirtyFormGuard;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule, HttpClientTestingModule],
      providers: [
        { provide: DialogService, useValue: dialogServiceSpy },
        LeaveDirtyFormGuard,
      ],
    });

    leaveDirtyFormGuard = TestBed.inject(LeaveDirtyFormGuard);
    dialogServiceSpy.confirm.calls.reset();
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

    expect(
      leaveDirtyFormGuard.canDeactivate(
        { form: form },
        route,
        currentState,
        nextState
      )
    ).toBeTruthy();
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

    expect(
      leaveDirtyFormGuard.canDeactivate(
        { form: form },
        route,
        currentState,
        nextState
      )
    ).toBeTruthy();
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

    leaveDirtyFormGuard.canDeactivate(
      { form: form },
      route,
      currentState,
      nextState
    );
    expect(dialogServiceSpy.confirm).toHaveBeenCalled();
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

    leaveDirtyFormGuard.canDeactivate(
      { form: form },
      route,
      currentState,
      nextState
    );
    expect(dialogServiceSpy.confirm).toHaveBeenCalled();
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

    leaveDirtyFormGuard.canDeactivate(
      { form: form },
      route,
      currentState,
      nextState
    );
    expect(dialogServiceSpy.confirm).not.toHaveBeenCalled();
  });

  it('should display confirmation dialog when leaving dirty contact point creation with detail subtab', () => {
    const currentState = {
      url: '/prm-directory/stop-points/ch:1:sloid:7000/contact-points/add/detail',
    } as RouterStateSnapshot;
    const nextState = { url: '/prm-directory' } as RouterStateSnapshot;

    const form = new FormGroup({});
    form.markAsDirty();

    leaveDirtyFormGuard.canDeactivate(
      { form: form },
      route,
      currentState,
      nextState
    );
    expect(dialogServiceSpy.confirm).toHaveBeenCalled();
  });
});
