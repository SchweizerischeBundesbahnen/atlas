import { Pipe, PipeTransform } from '@angular/core';
import { BusinessOrganisation } from '../../../api';
import { BusinessOrganisationLanguageService } from './business-organisation-language.service';

@Pipe({
  name: 'boSelectionDisplay',
  pure: false,
})
export class BoSelectionDisplayPipe implements PipeTransform {
  constructor(
    private readonly businessOrganisationLanguageService: BusinessOrganisationLanguageService
  ) {}

  transform(value: BusinessOrganisation): string {
    return `${value.organisationNumber} - ${
      value[this.businessOrganisationLanguageService.getCurrentLanguageAbbreviation()]
    } - ${value[this.businessOrganisationLanguageService.getCurrentLanguageDescription()]} - ${
      value.said
    }`;
  }
}
