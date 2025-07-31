import { ComponentFixture, TestBed } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterModule } from '@angular/router';
import { UserProfileComponent } from './user-profile.component';
import {
  adminUserServiceMock,
  translateServiceProvider,
} from '../../app.testing.mocks';
import { provideHttpClient } from '@angular/common/http';
import { UserService } from '../../core/auth/user/user.service';
import { Component } from '@angular/core';
import { PermissionComponent } from '../../core/components/permissions/permission.component';

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
      imports: [
        BrowserAnimationsModule,
        RouterModule.forRoot([]),
        UserProfileComponent,
      ],
      providers: [
        translateServiceProvider,
        provideHttpClient(),
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
