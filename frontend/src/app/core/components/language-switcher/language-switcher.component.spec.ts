import { ComponentFixture, TestBed } from '@angular/core/testing';
import { LanguageSwitcherComponent } from './language-switcher.component';
import { By } from '@angular/platform-browser';
import { DateAdapter } from '@angular/material/core';
import deTranslationFile from 'src/assets/i18n/de.json';
import frTranslationFile from 'src/assets/i18n/fr.json';
import itTranslationFile from 'src/assets/i18n/it.json';
import { AppTestingModule } from '../../../app.testing.module';

const dateAdapter = jasmine.createSpyObj('dateAdapter', ['setLocale']);
let component: LanguageSwitcherComponent;
let fixture: ComponentFixture<LanguageSwitcherComponent>;

describe('LanguageSwitcherComponent', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AppTestingModule, LanguageSwitcherComponent],
      providers: [{ provide: DateAdapter, useValue: dateAdapter }],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(LanguageSwitcherComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  describe('switching languages works', () => {
    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should switch to "de"', () => {
      component.setLanguage('de');
      expect(component.currentLanguage).toBe('de');
      expect(dateAdapter.setLocale).toHaveBeenCalledWith('de');
    });

    it('should switch to "fr"', () => {
      component.setLanguage('fr');
      expect(component.currentLanguage).toBe('fr');
    });

    it('should switch to "it"', () => {
      component.setLanguage('it');
      expect(component.currentLanguage).toBe('it');
    });

    it('should have translation for all defined keys', () => {
      expect(component.languages[0]).toBe('de');

      const deJson = JSON.parse(JSON.stringify(deTranslationFile));
      const deProperties = propertiesOf(deJson);

      const frJson = JSON.parse(JSON.stringify(frTranslationFile));
      const frProperties = propertiesOf(frJson);

      expect(frProperties).toEqual(deProperties);

      const itJson = JSON.parse(JSON.stringify(itTranslationFile));
      const itProperties = propertiesOf(itJson);

      expect(itProperties).toEqual(deProperties);
    });

    // eslint-disable-next-line  @typescript-eslint/no-explicit-any
    const propertiesOf = (obj: any, results: string[] = []) => {
      const r = results;
      Object.keys(obj).forEach((key) => {
        const value = obj[key];
        if (typeof value !== 'object') {
          r.push(key);
        } else if (typeof value === 'object') {
          propertiesOf(value, r);
        }
      });
      return r;
    };
  });

  describe('language switch looks fantastic and works', () => {
    it('should create links for languages', () => {
      const links = fixture.debugElement.queryAll(By.css('a'));
      expect(links.length).toBe(component.languages.length);
    });

    it('should link to french', () => {
      const links = fixture.debugElement.queryAll(By.css('a'));
      const frenchLink = links[1];
      frenchLink.nativeElement.click();
      fixture.detectChanges();

      expect(component.currentLanguage).toBe('fr');
      expect(frenchLink.nativeElement).toHaveClass('isSelected');
    });
  });
});
