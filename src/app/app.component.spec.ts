import { ComponentFixture, TestBed } from '@angular/core/testing';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';

import { AppComponent } from './app.component';
import { AuthService } from './core/auth/auth.service';
import { TranslateFakeLoader, TranslateLoader, TranslateModule } from '@ngx-translate/core';
import { HeaderComponent } from './core/components/header/header.component';
import { MaterialModule } from './core/module/material.module';
import { UserComponent } from './core/components/user/user.component';
import { LanguageSwitcherComponent } from './core/components/language-switcher/language-switcher.component';
import { SideNavComponent } from './core/components/side-nav/side-nav.component';
import { BreadcrumbComponent } from './core/components/breadcrumb/breadcrumb.component';

const authServiceMock: Partial<AuthService> = {
  claims: { name: 'Test', email: 'test@test.ch', roles: [] },
  logout: () => Promise.resolve(true),
};

describe('AppComponent', () => {
  let component: AppComponent;
  let fixture: ComponentFixture<AppComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        NoopAnimationsModule,
        RouterTestingModule,
        MaterialModule,
        TranslateModule.forRoot({
          loader: { provide: TranslateLoader, useClass: TranslateFakeLoader },
        }),
      ],
      declarations: [
        AppComponent,
        HeaderComponent,
        UserComponent,
        LanguageSwitcherComponent,
        SideNavComponent,
        BreadcrumbComponent,
      ],
      providers: [{ provide: AuthService, useValue: authServiceMock }],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AppComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
