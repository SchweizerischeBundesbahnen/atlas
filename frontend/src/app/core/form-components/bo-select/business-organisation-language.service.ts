import { Injectable } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { Language } from '../../components/language-switcher/language';

export type abbreviationType = 'abbreviationDe' | 'abbreviationFr' | 'abbreviationIt';
export type descriptionType = 'descriptionDe' | 'descriptionFr' | 'descriptionIt';
export type translatableType = 'description' | 'abbreviation';

@Injectable({
  providedIn: 'root',
})
export class BusinessOrganisationLanguageService {
  constructor(private readonly translateService: TranslateService) {}

  public languageChanged() {
    return this.translateService.onLangChange;
  }

  public getCurrentLanguageAbbreviation(): abbreviationType {
    return this.getCurrentLanguageKey('abbreviation');
  }

  public getCurrentLanguageDescription(): descriptionType {
    return this.getCurrentLanguageKey('description');
  }

  private getCurrentLanguageKey<resultType extends descriptionType | abbreviationType>(
    propertyName: translatableType
  ): resultType {
    const selectedLanguage = this.translateService.currentLang ?? Language.DE;
    return `${propertyName}${selectedLanguage[0].toUpperCase()}${
      selectedLanguage[1]
    }` as resultType;
  }
}
