import { ComponentFixture, TestBed } from '@angular/core/testing';

import { HeaderComponent } from './header.component';
import { TranslateFakeLoader, TranslateLoader, TranslateModule } from '@ngx-translate/core';
import { AuthService } from '../../auth/auth.service';
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

  describe('Component Rendering', () => {
    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should render header', () => {
      expect(fixture.nativeElement.querySelector('#header').textContent).toContain('HOME.HEADER');
    });
  });

  describe('Component logic', () => {
    it('should show label dev', () => {
      //given
      component.environmentLabel = 'dev';
      //when
      const result = component.showLabel();
      //then
      expect(result).toBeTruthy();
    });

    it('should show label int', () => {
      //given
      component.environmentLabel = 'int';
      //when
      const result = component.showLabel();
      //then
      expect(result).toBeTruthy();
    });

    it('should not show label different from dev, test or int', () => {
      //given
      component.environmentLabel = 'pro';
      //when
      const result = component.showLabel();
      //then
      expect(result).toBeFalsy();
    });

    it('should return dev class', () => {
      //given
      component.environmentLabel = 'dev';
      //when
      const result = component.getEnvLabelClass();
      //then
      expect(result['bg-primary']).toBeTruthy();
      expect(result['bg-secondary']).toBeFalsy();
      expect(result['bg-warning']).toBeFalsy();
    });

    it('should return test class', () => {
      //given
      component.environmentLabel = 'test';
      //when
      const result = component.getEnvLabelClass();
      //then
      expect(result['bg-secondary']).toBeTruthy();
      expect(result['bg-primary']).toBeFalsy();
      expect(result['bg-warning']).toBeFalsy();
    });

    it('should return int class', () => {
      //given
      component.environmentLabel = 'int';
      //when
      const result = component.getEnvLabelClass();
      //then
      expect(result['bg-warning']).toBeTruthy();
      expect(result['bg-primary']).toBeFalsy();
      expect(result['bg-secondary']).toBeFalsy();
    });

    it('should return class without color', () => {
      //given
      component.environmentLabel = 'prod';
      //when
      const result = component.getEnvLabelClass();
      //then
      expect(result['bg-warning']).toBeFalsy();
      expect(result['bg-primary']).toBeFalsy();
    });
  });
});
