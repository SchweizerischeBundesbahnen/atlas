import { inject, Pipe, PipeTransform } from '@angular/core';
import { BusinessOrganisation } from '../../api';
import { BusinessOrganisationLanguageService } from '../../pages/bodi/business-organisations/shared/business-organisation-language.service';

@Pipe({
  name: 'boSelectionDisplay',
  pure: false,
})
export class BoSelectionDisplayPipe implements PipeTransform {
  private readonly businessOrganisationLanguageService = inject(BusinessOrganisationLanguageService);

  transform(value?: BusinessOrganisation): string {
    if (!value) {
      return '--';
    }
    return `${value.organisationNumber} - ${
      value[this.businessOrganisationLanguageService.getCurrentLanguageAbbreviation()]
    } - ${value[this.businessOrganisationLanguageService.getCurrentLanguageDescription()]} - ${value.sboid}`;
  }
}
