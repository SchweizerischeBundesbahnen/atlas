import { Component } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { DateAdapter } from '@angular/material/core';

@Component({
  selector: 'app-language-switcher',
  templateUrl: './language-switcher.component.html',
  styleUrls: ['./language-switcher.component.scss'],
})
export class LanguageSwitcherComponent {
  static readonly STORED_LANGUAGE_KEY = 'language';
  languages = ['de', 'fr', 'it'];

  // eslint-disable-next-line  @typescript-eslint/no-explicit-any
  constructor(private translateService: TranslateService, private dateAdapter: DateAdapter<any>) {
    const language =
      this.languages.find(
        (lang) => lang === localStorage.getItem(LanguageSwitcherComponent.STORED_LANGUAGE_KEY)
      ) ||
      this.languages.find((lang) => lang === translateService.getBrowserLang()) ||
      this.languages[0];
    this.setLanguage(language);
  }

  get currentLanguage(): string {
    return this.translateService.currentLang;
  }

  setLanguage(language: string): void {
    localStorage.setItem(LanguageSwitcherComponent.STORED_LANGUAGE_KEY, language);
    this.translateService.use(language);
    this.dateAdapter.setLocale(language);
  }
}
