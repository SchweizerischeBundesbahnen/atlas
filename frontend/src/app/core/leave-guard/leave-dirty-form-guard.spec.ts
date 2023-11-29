import { TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { LeaveDirtyFormGuard } from './leave-dirty-form-guard.service';
import { DialogService } from '../components/dialog/dialog.service';
import { ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';

const dialogServiceSpy = jasmine.createSpyObj('DialogService', ['confirm']);
const route = {} as ActivatedRouteSnapshot;

describe('LeaveDirtyFormGuard', () => {
  let leaveDirtyFormGuard: LeaveDirtyFormGuard;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule, HttpClientTestingModule],
      providers: [{ provide: DialogService, useValue: dialogServiceSpy }, LeaveDirtyFormGuard],
    });

    leaveDirtyFormGuard = TestBed.inject(LeaveDirtyFormGuard);
  });

  it('should be created', () => {
    expect(leaveDirtyFormGuard).toBeTruthy();
  });

  it('should allow routing if form is not dirty', () => {
    const currentState = { url: '/line-directory/lines/add' } as RouterStateSnapshot;
    const nextState = { url: '/line-directory/sublines' } as RouterStateSnapshot;

    expect(
      leaveDirtyFormGuard.canDeactivate(
        { isFormDirty: () => false },
        route,
        currentState,
        nextState,
      ),
    ).toBeTruthy();
  });

  it('should allow routing by creation with same url if form is not dirty', () => {
    const currentState = {
      url: '/prm-directory/stop-points/ch:1:sloid:319/stop-point',
    } as RouterStateSnapshot;
    const nextState = {
      url: '/prm-directory/stop-points/ch:1:sloid:319/stop-point',
    } as RouterStateSnapshot;

    expect(
      leaveDirtyFormGuard.canDeactivate(
        { isFormDirty: () => false },
        route,
        currentState,
        nextState,
      ),
    ).toBeTruthy();
  });

  it('should display confirmation dialog on dirty form', () => {
    const currentState = { url: '/line-directory/lines/add' } as RouterStateSnapshot;
    const nextState = { url: '/line-directory/sublines' } as RouterStateSnapshot;

    leaveDirtyFormGuard.canDeactivate({ isFormDirty: () => true }, route, currentState, nextState);
    expect(dialogServiceSpy.confirm).toHaveBeenCalled();
  });
});
