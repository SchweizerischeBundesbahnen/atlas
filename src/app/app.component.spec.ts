import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import { SbbUsermenuItem } from '@sbb-esta/angular-business/usermenu';
import { SbbIconTestingModule } from '@sbb-esta/angular-core/icon/testing';

import { AppComponent } from './app.component';
import { AuthService } from './core/auth.service';
import { SbbAngularLibraryModule } from './shared/sbb-angular-library.module';
import { TranslateFakeLoader, TranslateLoader, TranslateModule } from '@ngx-translate/core';
import { HeaderComponent } from './core/components/header/header.component';
import { MaterialModule } from './core/module/material.module';
import { UserComponent } from './core/components/user/user.component';

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
        SbbAngularLibraryModule,
        SbbIconTestingModule,
        MaterialModule,
        TranslateModule.forRoot({
          loader: { provide: TranslateLoader, useClass: TranslateFakeLoader },
        }),
      ],
      declarations: [AppComponent, HeaderComponent, UserComponent],
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
