import { ComponentFixture, TestBed } from '@angular/core/testing';

import { HeaderComponent } from './header.component';
import { TranslateFakeLoader, TranslateLoader, TranslateModule } from '@ngx-translate/core';
import { AuthService } from '../../auth.service';
import { MaterialModule } from '../../module/material.module';
import { UserComponent } from '../user/user.component';
import { LanguageSwitcherComponent } from '../language-switcher/language-switcher.component';

describe('HeaderComponent', () => {
  const authServiceMock: Partial<AuthService> = {
    claims: { name: 'Test', email: 'test@test.ch', roles: [] },
    logout: () => Promise.resolve(true),
  };

  let component: HeaderComponent;
  let fixture: ComponentFixture<HeaderComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [HeaderComponent, UserComponent, LanguageSwitcherComponent],
      imports: [
        MaterialModule,
        TranslateModule.forRoot({
          loader: { provide: TranslateLoader, useClass: TranslateFakeLoader },
        }),
      ],
      providers: [{ provide: AuthService, useValue: authServiceMock }],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(HeaderComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render header', () => {
    expect(fixture.nativeElement.querySelector('h1.white.ms-3').textContent).toContain(
      'HOME.HEADER'
    );
  });
});
