import { ComponentFixture, TestBed } from '@angular/core/testing';
import { beforeEach, describe, expect, it } from 'vitest';
import { HeaderComponent } from './header.component';
import { AuthService } from '../../auth/auth.service';
import { authServiceMock, translateServiceProvider } from '../../../app.testing.mocks';
import { RouterModule } from '@angular/router';
import { DateModule } from '../../module/date.module';
import { LanguageSwitcherComponent } from '../language-switcher/language-switcher.component';
import { UserComponent } from '../user/user.component';
import { Component } from '@angular/core';

@Component({
  selector: 'atlas-language-switcher',
  template: '<h1>LanguageSwitcherComponent</h1>',
})
class MockLanguageSwitcherComponent {}

@Component({
  selector: 'atlas-user',
  template: '<h1>UserComponent</h1>',
})
class MockUserComponent {}

describe('HeaderComponent', () => {
  let component: HeaderComponent;
  let fixture: ComponentFixture<HeaderComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [DateModule.forRoot(), RouterModule.forRoot([])],
      providers: [{ provide: AuthService, useValue: authServiceMock }, translateServiceProvider],
    }).overrideComponent(HeaderComponent, {
      remove: { imports: [LanguageSwitcherComponent, UserComponent] },
      add: { imports: [MockLanguageSwitcherComponent, MockUserComponent] },
    });

    fixture = TestBed.createComponent(HeaderComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  describe('Component Rendering', () => {
    it('should create', () => {
      expect(component).toBeTruthy();
    });
  });

  describe('Component logic', () => {
    it('should show label dev', () => {
      //given
      component.environmentLabel = 'dev';
      //when
      const result = component.showLabel;
      //then
      expect(result).toBeTruthy();
    });

    it('should show label int', () => {
      //given
      component.environmentLabel = 'int';
      //when
      const result = component.showLabel;
      //then
      expect(result).toBeTruthy();
    });

    it('should not show label different from dev, test or int', () => {
      //given
      component.environmentLabel = 'pro';
      component.ngOnInit();
      //when
      const result = component.showLabel;
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
