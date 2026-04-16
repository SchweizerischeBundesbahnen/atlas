import { ComponentFixture, TestBed } from '@angular/core/testing';
import { beforeEach, describe, expect, it, type Mocked, vi } from 'vitest';
import { UserAdministrationUserCreateComponent } from './user-administration-user-create.component';
import { Permission } from '../../../../../api';
import { NotificationService } from '../../../../../core/notification/notification.service';
import { of } from 'rxjs';
import { provideRouter, Router } from '@angular/router';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { UserAdministrationService } from '../../../../../api/service/user-administration/user-administration.service';
import { translateServiceProvider } from '../../../../../app.testing.mocks';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';

describe('UserAdministrationUserCreateComponent', () => {
  let component: UserAdministrationUserCreateComponent;
  let fixture: ComponentFixture<UserAdministrationUserCreateComponent>;

  let userAdministrationService: Mocked<Pick<UserAdministrationService, 'getUser' | 'createUserPermission'>>;
  let notificationService: Mocked<Pick<NotificationService, 'success'>>;

  beforeEach(() => {
    userAdministrationService = {
      getUser: vi.fn(),
      createUserPermission: vi.fn(),
    };
    notificationService = {
      success: vi.fn(),
    };
    TestBed.configureTestingModule({
      providers: [
        translateServiceProvider,
        provideHttpClient(),
        provideHttpClientTesting(),
        provideRouter([]),
        {
          provide: UserAdministrationService,
          useValue: userAdministrationService,
        },
        {
          provide: NotificationService,
          useValue: notificationService,
        },
        {
          provide: MAT_DIALOG_DATA,
          useValue: { user: undefined },
        },
        {
          provide: MatDialogRef,
          useValue: {
            close: () => {
              // mock implementation
            },
          },
        },
      ],
    });

    fixture = TestBed.createComponent(UserAdministrationUserCreateComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
    expect(component.selectedUser).toBeUndefined();
    expect(component.userHasAlreadyPermissions).toBe(false);
    expect(component.selectedUserHasNoUserId).toBe(false);
  });

  it('test selectUser with valid user', () => {
    userAdministrationService.getUser.mockImplementation((userId) =>
      of({
        sbbUserId: userId,
        permissions: new Set<Permission>(),
      })
    );
    component.selectUser({
      sbbUserId: 'user1',
      permissions: new Set(),
    });
    expect(component.selectedUserHasNoUserId).toBe(false);
    expect(component.userHasAlreadyPermissions).toBe(false);
    expect(component.selectedUser).toEqual({
      sbbUserId: 'user1',
      permissions: new Set(),
    });
    expect(userAdministrationService.getUser).toHaveBeenCalledExactlyOnceWith('user1');
  });

  it('test createUser', async () => {
    const router = TestBed.inject(Router);
    component.selectedUser = {
      sbbUserId: 'user1',
      permissions: new Set(),
    };
    userAdministrationService.createUserPermission.mockReturnValue(
      of({
        sbbUserId: 'user1',
        permissions: new Set<Permission>(),
      })
    );
    vi.spyOn(router, 'navigate').mockResolvedValue(true);

    component.createUser();

    expect(userAdministrationService.createUserPermission).toHaveBeenCalledTimes(1);
    expect(router.navigate).toHaveBeenCalledTimes(1);
    await fixture.whenStable();
    expect(notificationService.success).toHaveBeenCalledExactlyOnceWith('USER_ADMIN.NOTIFICATIONS.ADD_SUCCESS');
  });
});
