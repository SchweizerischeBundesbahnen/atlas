import { ComponentFixture, TestBed } from '@angular/core/testing';
import { beforeEach, describe, expect, it } from 'vitest';
import { UserAdministrationClientDetailComponent } from './user-administration-client-detail.component';
import { ActivatedRoute } from '@angular/router';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { TranslatePipe } from '@ngx-translate/core';
import { of } from 'rxjs';
import { UserPermissionGivenClientService } from './edit/user-permission-given-client.service';
import { translateServiceProvider } from '../../../../app.testing.mocks';

describe('UserAdministrationClientDetailComponent', () => {
  let component: UserAdministrationClientDetailComponent;
  let fixture: ComponentFixture<UserAdministrationClientDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [UserAdministrationClientDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ clientCredential: {} }) },
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
});
