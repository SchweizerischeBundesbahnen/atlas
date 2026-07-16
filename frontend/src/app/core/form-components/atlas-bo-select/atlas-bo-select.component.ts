import { Component, inject, input, output } from '@angular/core';
import { BoSelectionDisplayPipe } from '../../pipe/bo-selection-display.pipe';
import { BusinessOrganisation } from '../../../api/index';
import { Observable, of } from 'rxjs';
import { BusinessOrganisationService } from '../../../api/service/bodi/business-organisation.service';
import { map } from 'rxjs/operators';
import { AsyncPipe } from '@angular/common';
import { Field } from '@angular/forms/signals';
import { AtlasLabelFieldComponent } from '@atlas/form/lib/atlas-label-field/atlas-label-field.component';
import { AtlasSearchSelectComponent } from '@atlas/form/lib/atlas-search-select/atlas-search-select.component';

@Component({
  selector: 'atlas-bo-select',
  imports: [AtlasLabelFieldComponent, BoSelectionDisplayPipe, AtlasSearchSelectComponent, AsyncPipe],
  templateUrl: './atlas-bo-select.component.html',
})
export class AtlasBoSelectComponent {
  readonly valueExtraction = input('sboid');
  readonly formModus = input(true);
  readonly sboidsRestrictions = input<string[]>([]);
  readonly disabled = input(false);

  readonly field = input.required<Field<BusinessOrganisation | null>>();

  readonly boSelectionChanged = output<BusinessOrganisation | null>();

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
