import { beforeEach, describe, expect, it } from 'vitest';
import { TestBed } from '@angular/core/testing';
import { canActivateTimetableHearing, TimetableHearingGuard } from './timetable-hearing.guard';
import { ActivatedRouteSnapshot, convertToParamMap, RouterModule, RouterStateSnapshot, UrlTree } from '@angular/router';
import { PermissionService } from '../permission/permission.service';
import { UserService } from '../user/user.service';
import { firstValueFrom, Observable } from 'rxjs';

describe('TimetableHearingGuard', () => {
  let guard: TimetableHearingGuard;

  let mayAccessTth = false;
  const permissionServiceMock: Partial<PermissionService> = {
    mayAccessTimetableHearing: () => mayAccessTth,
  };

  let permissionsLoadedCalled = false;
  const userServiceMock: Partial<UserService> = {
    get loggedIn(): boolean {
      return true;
    },
    onPermissionsLoaded(): Observable<void> {
      permissionsLoadedCalled = true;
      return new Observable((subscriber) => {
        subscriber.next();
      });
    },
  };

  beforeEach(() => {
    mayAccessTth = false;
    permissionsLoadedCalled = false;

    TestBed.configureTestingModule({
      imports: [RouterModule.forRoot([])],
      providers: [
        {
          provide: UserService,
          useValue: userServiceMock,
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

  it('should be allowed and wait for permissions to be loaded', async () => {
    mayAccessTth = true;

    const mockRoute = {
      paramMap: convertToParamMap({ id: '1234' }),
    } as ActivatedRouteSnapshot;
    const result = TestBed.runInInjectionContext(
      () => canActivateTimetableHearing(mockRoute, {} as RouterStateSnapshot) as Observable<true | UrlTree>
    );

    expect(result).toBeDefined();
    const guardResult = await firstValueFrom(result);
    expect(permissionsLoadedCalled).toBe(true);
    expect(guardResult).toBe(true);
  });

  it('should not be allowed and wait for permissions to be loaded', async () => {
    mayAccessTth = false;

    const mockRoute = {
      paramMap: convertToParamMap({ id: '1234' }),
    } as ActivatedRouteSnapshot;
    const result = TestBed.runInInjectionContext(
      () => canActivateTimetableHearing(mockRoute, {} as RouterStateSnapshot) as Observable<true | UrlTree>
    );

    expect(result).toBeDefined();
    const guardResult = await firstValueFrom(result);
    expect(permissionsLoadedCalled).toBe(true);
    expect(guardResult.toString()).toEqual('/');
  });
});
