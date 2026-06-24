import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { InterpolatableTranslationObject, TranslateService } from '@ngx-translate/core';
import { DateAdapter } from '@angular/material/core';
import { Language } from './language';
import { NgClass, UpperCasePipe } from '@angular/common';
import { RouterLink } from '@angular/router';
import { Observable } from 'rxjs';

@Component({
  selector: 'atlas-language-switcher',
  templateUrl: './language-switcher.component.html',
  changeDetection: ChangeDetectionStrategy.Eager,
  styleUrls: ['./language-switcher.component.scss'],
  imports: [RouterLink, NgClass, UpperCasePipe],
})
export class LanguageSwitcherComponent {
  static readonly STORED_LANGUAGE_KEY = 'language';
  languages = [Language.DE, Language.FR, Language.IT];

  private readonly translateService = inject(TranslateService);
  private readonly dateAdapter = inject(DateAdapter<Date>);

  constructor() {
    const language =
      this.languages.find((lang) => lang === localStorage.getItem(LanguageSwitcherComponent.STORED_LANGUAGE_KEY)) ||
      this.languages.find((lang) => lang === this.translateService.getBrowserLang()) ||
      this.languages[0];
    this.setLanguage(language);
  }

  get currentLanguage(): string {
    return this.translateService.getCurrentLang()!;
  }

  setLanguage(language: string): Observable<InterpolatableTranslationObject> {
    localStorage.setItem(LanguageSwitcherComponent.STORED_LANGUAGE_KEY, language);
    this.dateAdapter.setLocale(language);
    return this.translateService.use(language);
  }
}
