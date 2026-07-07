import { Component, inject, input, output } from '@angular/core';
import { AtlasLabelFieldComponent } from '@atlas/form';
import { BoSelectionDisplayPipe } from '../../pipe/bo-selection-display.pipe';
import { BusinessOrganisation } from '../../../api';
import { Observable, of } from 'rxjs';
import { BusinessOrganisationService } from '../../../api/service/bodi/business-organisation.service';
import { map } from 'rxjs/operators';
import { SearchSelectSfComponent } from '../search-select-sf/search-select-sf.component';
import { AsyncPipe } from '@angular/common';

@Component({
  selector: 'atlas-bo-select-sf',
  imports: [AtlasLabelFieldComponent, BoSelectionDisplayPipe, SearchSelectSfComponent, AsyncPipe],
  templateUrl: './bo-select-sf.component.html',
})
export class BoSelectSfComponent {
  readonly valueExtraction = input('sboid');
  readonly formModus = input(true);
  readonly sboidsRestrictions = input<string[]>([]);
  readonly disabled = input(false);

  readonly boSelectionChanged = output<BusinessOrganisation>();

  businessOrganisations: Observable<BusinessOrganisation[]> = of([]);

  private readonly businessOrganisationService = inject(BusinessOrganisationService);

  searchBusinessOrganisation(searchString: string) {
    if (searchString) {
      this.businessOrganisations = this.businessOrganisationService
        .getAllBusinessOrganisations(
          [searchString],
          this.sboidsRestrictions(),
          undefined,
          undefined,
          undefined,
          undefined,
          ['sboid,ASC']
        )
        .pipe(map((value) => value.objects ?? []));
    }
  }
}
