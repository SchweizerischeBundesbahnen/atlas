import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UserAdministrationClientDetailComponent } from './user-administration-client-detail.component';
import { ActivatedRoute } from '@angular/router';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { TranslateModule, TranslatePipe } from '@ngx-translate/core';
import { of } from 'rxjs';
import { UserPermissionGivenClientService } from './edit/user-permission-given-client.service';

describe('UserAdministrationClientDetailComponent', () => {
  let component: UserAdministrationClientDetailComponent;
  let fixture: ComponentFixture<UserAdministrationClientDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        UserAdministrationClientDetailComponent,
        TranslateModule.forRoot({}),
      ],
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
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(UserAdministrationClientDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
