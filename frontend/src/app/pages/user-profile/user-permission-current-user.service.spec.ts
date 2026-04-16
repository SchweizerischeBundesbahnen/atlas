import { TestBed } from '@angular/core/testing';
import { RouterModule } from '@angular/router';
import { beforeEach, describe, expect, it, type Mocked } from 'vitest';
import { UserPermissionCurrentUserService } from './user-permission-current-user.service';
import { UserService } from '../../core/auth/user/user.service';
import { ApplicationType } from '../../api';

describe('UserPermissionCurrentUserService', () => {
  let userPermissionCurrentUserService: UserPermissionCurrentUserService;

  let userService: Mocked<Pick<UserService, 'currentUser'>>;

  beforeEach(() => {
    userService = {
      currentUser: {
        email: 'muster@sbb.ch',
        isAdmin: false,
        name: 'Herr Muster',
        permissions: [],
        sbbuid: 'u1234356',
      },
    };
    TestBed.configureTestingModule({
      imports: [RouterModule.forRoot([])],
      providers: [
        {
          provide: UserService,
          useValue: userService,
        },
        { provide: UserPermissionCurrentUserService },
      ],
    });
    userPermissionCurrentUserService = TestBed.inject(UserPermissionCurrentUserService);
  });

  it('should be created', () => {
    expect(userPermissionCurrentUserService).toBeTruthy();
    expect(userPermissionCurrentUserService.showAllSpecialPermissions()).toBe(false);
  });

  it('should load formgroup from user permissions', () => {
    userPermissionCurrentUserService.loadFormGroup(ApplicationType.Ttfn);
    const currentForm = userPermissionCurrentUserService.getCurrentForm();
    expect(currentForm?.controls.application.value).toBe(ApplicationType.Ttfn);
  });
});
