import { TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { LeaveDirtyFormGuard } from './leave-dirty-form-guard.service';
import { DialogService } from '../components/dialog/dialog.service';

const dialogServiceSpy = jasmine.createSpyObj(['confirm']);

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
    expect(leaveDirtyFormGuard.canDeactivate({ isFormDirty: () => false })).toBeTruthy();
  });

  it('should display confirmation dialog on dirty form', () => {
    leaveDirtyFormGuard.canDeactivate({ isFormDirty: () => true });
    expect(dialogServiceSpy.confirm).toHaveBeenCalled();
  });
});
