import { beforeEach, describe, expect, it } from 'vitest';
import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, convertToParamMap, RouterStateSnapshot } from '@angular/router';
import { UserService } from '../user/user.service';
import { permissionsLoaded, PermissionsLoadedGuard } from './permissions-loaded.guard';
import { firstValueFrom, Observable } from 'rxjs';

describe('PermissionsLoadedGuard', () => {
  let guard: PermissionsLoadedGuard;

  let permissionsLoadedCalled = false;
  const userServiceMock: Partial<UserService> = {
    onPermissionsLoaded(): Observable<void> {
      permissionsLoadedCalled = true;
      return new Observable((subscriber) => {
        subscriber.next();
      });
    },
  };

  beforeEach(() => {
    permissionsLoadedCalled = false;

    TestBed.configureTestingModule({
      providers: [
        {
          provide: UserService,
          useValue: userServiceMock,
        },
      ],
    });

    guard = TestBed.inject(PermissionsLoadedGuard);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should wait for permissions to be loaded', async () => {
    const mockRoute = {
      paramMap: convertToParamMap({ id: '1234' }),
    } as ActivatedRouteSnapshot;

    const result = TestBed.runInInjectionContext(
      () => permissionsLoaded(mockRoute, {} as RouterStateSnapshot) as Observable<boolean>
    );

    expect(result).toBeDefined();
    const guardResult = await firstValueFrom(result);
    expect(permissionsLoadedCalled).toBe(true);
    expect(guardResult).toBe(true);
  });
});
