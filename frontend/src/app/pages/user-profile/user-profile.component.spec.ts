import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { beforeEach, describe, expect, it } from 'vitest';
import { UserProfileComponent } from './user-profile.component';
import { adminUserServiceMock, translateServiceProvider } from '../../app.testing.mocks';
import { provideHttpClient } from '@angular/common/http';
import { UserService } from '../../core/auth/user/user.service';
import { Component } from '@angular/core';
import { PermissionComponent } from '../../core/components/permissions/permission.component';
import { provideHttpClientTesting } from '@angular/common/http/testing';

@Component({
  selector: 'atlas-permission',
  template: '<h1>PermissionsComponentMock</h1>',
})
export class PermissionsComponentMock {}

describe('UserProfileComponent', () => {
  let component: UserProfileComponent;
  let fixture: ComponentFixture<UserProfileComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        translateServiceProvider,
        provideHttpClient(),
        provideHttpClientTesting(),
        provideRouter([]),
        { provide: UserService, useValue: adminUserServiceMock },
      ],
    }).overrideComponent(UserProfileComponent, {
      remove: {
        imports: [PermissionComponent],
      },
      add: {
        imports: [PermissionsComponentMock],
      },
    });

    fixture = TestBed.createComponent(UserProfileComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
