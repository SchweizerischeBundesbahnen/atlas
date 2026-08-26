import { ChangeDetectionStrategy, Component, computed, inject, signal } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { form } from '@angular/forms/signals';
import { TranslatePipe } from '@ngx-translate/core';
import { MatIconButton } from '@angular/material/button';
import { BusinessOrganisation } from '../../../../api';
import { AtlasBoSelectComponent } from '../../../../core/form-components/atlas-bo-select/atlas-bo-select.component';
import { DialogCloseComponent } from '../../../../core/components/dialog/close/dialog-close.component';
import { DialogContentComponent } from '../../../../core/components/dialog/content/dialog-content.component';
import { DialogFooterComponent } from '../../../../core/components/dialog/footer/dialog-footer.component';
import { BusinessOrganisationLanguageService } from '../../../bodi/business-organisations/shared/business-organisation-language.service';
import { MapBoFilterDialogData } from './map-bo-filter-dialog-data';

interface BoFilterSelectionForm {
  businessOrganisation: BusinessOrganisation | string | null;
}

@Component({
  selector: 'atlas-map-bo-filter-dialog',
  templateUrl: './map-bo-filter-dialog.component.html',
  styleUrl: './map-bo-filter-dialog.component.scss',
  changeDetection: ChangeDetectionStrategy.Eager,
  imports: [
    AtlasBoSelectComponent,
    DialogCloseComponent,
    DialogContentComponent,
    DialogFooterComponent,
    MatIconButton,
    TranslatePipe,
  ],
})
export class MapBoFilterDialogComponent {
  private readonly data: MapBoFilterDialogData = inject(MAT_DIALOG_DATA);
  private readonly dialogRef =
    inject<MatDialogRef<MapBoFilterDialogComponent, BusinessOrganisation[] | undefined>>(MatDialogRef);
  private readonly businessOrganisationLanguageService = inject(BusinessOrganisationLanguageService);

  readonly selectedBusinessOrganisations = signal<BusinessOrganisation[]>([...(this.data.businessOrganisations ?? [])]);

  private readonly searchModel = signal<BoFilterSelectionForm>({ businessOrganisation: null });
  protected readonly searchForm = form(this.searchModel);

  protected readonly selectionRows = computed(() => {
    const descriptionKey = this.businessOrganisationLanguageService.getCurrentLanguageDescription();
    const abbreviationKey = this.businessOrganisationLanguageService.getCurrentLanguageAbbreviation();
    return this.selectedBusinessOrganisations().map((businessOrganisation) => ({
      businessOrganisation,
      name: businessOrganisation[descriptionKey],
      abbreviation: businessOrganisation[abbreviationKey],
      organisationNumber: businessOrganisation.organisationNumber,
    }));
  });

  boSelectionChanged(selection: BusinessOrganisation | string | null) {
    if (selection && typeof selection !== 'string') {
      this.addBusinessOrganisation(selection);
    }
    this.resetSearchField();
  }

  removeBusinessOrganisation(businessOrganisation: BusinessOrganisation) {
    this.selectedBusinessOrganisations.update((selection) =>
      selection.filter((selected) => selected.sboid !== businessOrganisation.sboid)
    );
  }

  reset() {
    this.selectedBusinessOrganisations.set([]);
    this.resetSearchField();
  }

  apply() {
    this.dialogRef.close(this.selectedBusinessOrganisations());
  }

  cancel() {
    this.dialogRef.close();
  }

  private addBusinessOrganisation(businessOrganisation: BusinessOrganisation) {
    this.selectedBusinessOrganisations.update((selection) =>
      selection.some((selected) => selected.sboid === businessOrganisation.sboid)
        ? selection
        : [...selection, businessOrganisation]
    );
  }

  private resetSearchField() {
    this.searchModel.set({ businessOrganisation: null });
  }
}
