import {TestBed} from '@angular/core/testing';
import {canActivateTimetableHearing, TimetableHearingGuard} from './timetable-hearing-guard.service';
import {ActivatedRouteSnapshot, convertToParamMap, RouterModule, RouterStateSnapshot, UrlTree} from "@angular/router";
import {adminUserServiceMock} from "../../../app.testing.mocks";
import {PermissionService} from "../permission/permission.service";
import {UserService} from "../user/user.service";
import {Observable} from "rxjs";

let mayAccessTth = true;
const permissionServiceMock: Partial<PermissionService> = {
  mayAccessTimetableHearing: () => mayAccessTth,
};

describe('TimetableHearingGuard', () => {
  let guard: TimetableHearingGuard;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterModule.forRoot([])],
      providers: [
        {
          provide: UserService,
          useValue: adminUserServiceMock,
        },
        {
          provide: PermissionService,
          useValue: permissionServiceMock,
        },
      ],
    });
    guard = TestBed.inject(TimetableHearingGuard);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should be allowed to access tth', () => {
    mayAccessTth = true;

    const mockRoute = {paramMap: convertToParamMap({id: '1234'})} as ActivatedRouteSnapshot;
    const result = TestBed.runInInjectionContext(
      () => canActivateTimetableHearing(mockRoute, {} as RouterStateSnapshot)) as Observable<boolean>;

    expect(result).toBeDefined();
    result.subscribe(url => {
      expect(url).toBeTrue();
    })
  });

  it('should not be allowed to access tth', () => {
    mayAccessTth = false;

    const mockRoute = {paramMap: convertToParamMap({id: '1234'})} as ActivatedRouteSnapshot;
    const result = TestBed.runInInjectionContext(
      () => canActivateTimetableHearing(mockRoute, {} as RouterStateSnapshot)) as Observable<UrlTree>;

    expect(result).toBeDefined();
    result.subscribe(url => {
      expect(url.toString()).toEqual('/');
    })
  });
});
