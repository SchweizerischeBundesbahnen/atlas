import { TestBed } from '@angular/core/testing';
import { beforeEach, describe, expect, it } from 'vitest';
import { ActivatedRouteSnapshot, convertToParamMap, RouterStateSnapshot, UrlTree } from '@angular/router';
import { PermissionService } from '../../core/auth/permission/permission.service';
import { AppTestingModule } from '../../app.testing.module';
import { CanActivateServicePointCreationGuard, canCreateServicePoint } from './service-point-creation-guard';
import { Pages } from '../pages';

describe('CanActivateServicePointCreationGuard', () => {
  let permissionsToCreate = true;
  const permissionServiceMock: Partial<PermissionService> = {
    hasPermissionsToCreate: () => permissionsToCreate,
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [AppTestingModule],
      providers: [
        CanActivateServicePointCreationGuard,
        { provide: PermissionService, useValue: permissionServiceMock },
      ],
    });
  });

  it('should allow creation to admin user', () => {
    permissionsToCreate = true;

    const mockRoute = {
      paramMap: convertToParamMap({ id: '1234' }),
    } as ActivatedRouteSnapshot;
    const result = TestBed.runInInjectionContext(() => canCreateServicePoint(mockRoute, {} as RouterStateSnapshot)) as
      | true
      | UrlTree;

    expect(result).toBe(true);
  });

  it('should not allow creation', () => {
    permissionsToCreate = false;

    const mockRoute = {
      paramMap: convertToParamMap({ id: '1234' }),
    } as ActivatedRouteSnapshot;
    const result = TestBed.runInInjectionContext(() =>
      canCreateServicePoint(mockRoute, {} as RouterStateSnapshot)
    ) as UrlTree;

    expect(result.toString()).toBe('/' + Pages.SEPODI.path);
  });
});
