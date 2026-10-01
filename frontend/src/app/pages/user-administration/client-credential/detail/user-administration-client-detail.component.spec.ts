import { ComponentFixture, TestBed } from '@angular/core/testing';
import { beforeEach, describe, expect, it } from 'vitest';
import { UserAdministrationClientDetailComponent } from './user-administration-client-detail.component';
import { ActivatedRoute, Data } from '@angular/router';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { TranslatePipe } from '@ngx-translate/core';
import { BehaviorSubject } from 'rxjs';
import { UserPermissionGivenClientService } from './edit/user-permission-given-client.service';
import { translateServiceProvider } from '../../../../app.testing.mocks';
import { ApplicationType } from '../../../../api';
import { provideZonelessChangeDetection } from '@angular/core';

describe('UserAdministrationClientDetailComponent', () => {
  let component: UserAdministrationClientDetailComponent;
  let fixture: ComponentFixture<UserAdministrationClientDetailComponent>;
  let routeData: BehaviorSubject<Data>;

  beforeEach(() => {
    routeData = new BehaviorSubject<Data>({ clientCredential: {} });
    TestBed.configureTestingModule({
      imports: [UserAdministrationClientDetailComponent],
      providers: [
        provideZonelessChangeDetection(),
        {
          provide: ActivatedRoute,
          useValue: { data: routeData },
        },
        {
          provide: UserPermissionGivenClientService,
        },
        TranslatePipe,
        provideHttpClient(),
        provideHttpClientTesting(),
        translateServiceProvider,
      ],
    });

    fixture = TestBed.createComponent(UserAdministrationClientDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('updates when the resolved client credential changes', () => {
    routeData.next({ clientCredential: { clientCredentialId: 'new-client' } });

    expect(component.clientCredential().clientCredentialId).toBe('new-client');
  });

  it('exposes unsaved permission edits to the leave guard', () => {
    const permissionService = TestBed.inject(UserPermissionGivenClientService);
    permissionService.clientCredential = { permissions: new Set() };
    permissionService.loadForm(ApplicationType.Ttfn);
    permissionService.setEditable(true);
    permissionService.currentForm()!.role().markAsDirty();

    expect(component.dirty()).toBe(true);

    permissionService.loadForm(ApplicationType.Ttfn);
    expect(component.dirty()).toBe(false);
  });
});
