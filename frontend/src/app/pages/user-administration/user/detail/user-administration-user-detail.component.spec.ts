import { ComponentFixture, TestBed } from '@angular/core/testing';
import { beforeEach, describe, expect, it } from 'vitest';
import { UserAdministrationUserDetailComponent } from './user-administration-user-detail.component';
import { Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { UserAdministrationUserCreateComponent } from './create/user-administration-user-create.component';
import { UserAdministrationUserEditComponent } from './edit/user-administration-user-edit.component';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { of } from 'rxjs';
import { translateServiceProvider } from '../../../../app.testing.mocks';
import { UserPermissionGivenUserService } from './edit/user-permission-given-user.service';
import { ApplicationType } from '../../../../api';

@Component({
  selector: 'atlas-user-administration-create',
  template: '',
})
class MockAppUserAdministrationCreateComponent {}

@Component({
  selector: 'atlas-user-administration-user-edit',
  template: '',
})
class MockUserAdministrationUserEditComponent {}

describe('UserAdministrationUserDetailComponent', () => {
  let component: UserAdministrationUserDetailComponent;
  let fixture: ComponentFixture<UserAdministrationUserDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [UserAdministrationUserDetailComponent],
      providers: [
        translateServiceProvider,
        provideHttpClient(),
        provideHttpClientTesting(),
        {
          provide: ActivatedRoute,
          useValue: { data: of({ user: undefined }) },
        },
      ],
    }).overrideComponent(UserAdministrationUserDetailComponent, {
      remove: {
        providers: [UserAdministrationUserCreateComponent, UserAdministrationUserEditComponent],
      },
      add: {
        providers: [MockAppUserAdministrationCreateComponent, MockUserAdministrationUserEditComponent],
      },
    });

    fixture = TestBed.createComponent(UserAdministrationUserDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('exposes Signal Forms dirty state to the leave guard', () => {
    const provider = TestBed.inject(UserPermissionGivenUserService);
    provider.user = { sbbUserId: 'u1', permissions: new Set() };
    provider.loadForm(ApplicationType.Ttfn);
    expect(component.dirty()).toBe(false);

    provider.setEditable(true);
    provider.currentForm()?.role().markAsDirty();
    expect(component.dirty()).toBe(true);
  });
});
