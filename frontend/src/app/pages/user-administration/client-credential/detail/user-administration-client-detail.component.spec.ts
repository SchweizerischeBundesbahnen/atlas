import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UserAdministrationClientDetailComponent } from './user-administration-client-detail.component';
import { ActivatedRoute } from '@angular/router';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { TranslateModule, TranslatePipe } from '@ngx-translate/core';

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
          useValue: { snapshot: { data: { clientCredential: {} } } },
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
