import { ComponentFixture, TestBed } from '@angular/core/testing';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import { LanguageSwitcherComponent } from './language-switcher.component';
import { By } from '@angular/platform-browser';
import { DateAdapter } from '@angular/material/core';
import deTranslationFile from '../../../../assets/i18n/de.json';
import frTranslationFile from '../../../../assets/i18n/fr.json';
import itTranslationFile from '../../../../assets/i18n/it.json';
import { TranslateService } from '@ngx-translate/core';
import { mock, mockClear } from 'vitest-mock-extended';
import { firstValueFrom, of } from 'rxjs';
import { provideRouter } from '@angular/router';

describe('LanguageSwitcherComponent', () => {
  let component: LanguageSwitcherComponent;
  let fixture: ComponentFixture<LanguageSwitcherComponent>;

  const dateAdapter = { setLocale: vi.fn() } as const;
  const translateService = mock<TranslateService>();
  translateService.use.mockReturnValue(of({}));

  beforeEach(() => {
    // Config
    TestBed.configureTestingModule({
      providers: [
        { provide: DateAdapter, useValue: dateAdapter },
        { provide: TranslateService, useValue: translateService },
        provideRouter([]),
      ],
    });

    // Arrangement
    fixture = TestBed.createComponent(LanguageSwitcherComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    mockClear(translateService);
    dateAdapter.setLocale.mockClear();
  });

  it('should switch to "de"', async () => {
    await firstValueFrom(component.setLanguage('de'));
    expect(translateService.use).toHaveBeenCalledExactlyOnceWith('de');
    expect(dateAdapter.setLocale).toHaveBeenCalledExactlyOnceWith('de');
  });

  it('should switch to "fr"', async () => {
    await firstValueFrom(component.setLanguage('fr'));
    expect(translateService.use).toHaveBeenCalledExactlyOnceWith('fr');
    expect(dateAdapter.setLocale).toHaveBeenCalledExactlyOnceWith('fr');
  });

  it('should switch to "it"', async () => {
    await firstValueFrom(component.setLanguage('it'));
    expect(translateService.use).toHaveBeenCalledExactlyOnceWith('it');
    expect(dateAdapter.setLocale).toHaveBeenCalledExactlyOnceWith('it');
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

  it('should create links for languages', () => {
    const links = fixture.debugElement.queryAll(By.css('a'));
    expect(links.length).toBe(component.languages.length);
  });
});
