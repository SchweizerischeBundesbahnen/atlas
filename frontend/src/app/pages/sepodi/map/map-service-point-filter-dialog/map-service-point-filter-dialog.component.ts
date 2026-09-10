import { ChangeDetectionStrategy, Component, computed, inject, signal } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { form } from '@angular/forms/signals';
import { TranslatePipe } from '@ngx-translate/core';
import { MatIconButton } from '@angular/material/button';
import { BusinessOrganisation, MeanOfTransport } from '../../../../api';
import { AtlasBoSelectComponent } from '../../../../core/form-components/atlas-bo-select/atlas-bo-select.component';
import { AtlasMeansOfTransportPickerComponent } from '../../../../core/form-components/atlas-means-of-transport-picker/atlas-means-of-transport-picker.component';
import { DialogCloseComponent } from '../../../../core/components/dialog/close/dialog-close.component';
import { DialogContentComponent } from '../../../../core/components/dialog/content/dialog-content.component';
import { DialogFooterComponent } from '../../../../core/components/dialog/footer/dialog-footer.component';
import { BusinessOrganisationLanguageService } from '../../../bodi/business-organisations/shared/business-organisation-language.service';
import {
  MapServicePointFilterDialogData,
  MapServicePointFilterDialogResult,
} from './map-service-point-filter-dialog-data';

interface BoFilterSelectionForm {
  businessOrganisation: BusinessOrganisation | string | null;
}

interface MotFilterSelectionForm {
  meansOfTransport: MeanOfTransport[];
}

@Component({
  selector: 'atlas-map-service-point-filter-dialog',
  templateUrl: './map-service-point-filter-dialog.component.html',
  styleUrl: './map-service-point-filter-dialog.component.scss',
  changeDetection: ChangeDetectionStrategy.Eager,
  imports: [
    AtlasBoSelectComponent,
    AtlasMeansOfTransportPickerComponent,
    DialogCloseComponent,
    DialogContentComponent,
    DialogFooterComponent,
    MatIconButton,
    TranslatePipe,
  ],
})
export class MapServicePointFilterDialogComponent {
  private readonly data: MapServicePointFilterDialogData = inject(MAT_DIALOG_DATA);
  private readonly dialogRef =
    inject<MatDialogRef<MapServicePointFilterDialogComponent, MapServicePointFilterDialogResult | undefined>>(
      MatDialogRef
    );
  private readonly businessOrganisationLanguageService = inject(BusinessOrganisationLanguageService);

  readonly selectedBusinessOrganisations = signal<BusinessOrganisation[]>([...(this.data.businessOrganisations ?? [])]);

  private readonly searchModel = signal<BoFilterSelectionForm>({ businessOrganisation: null });
  protected readonly searchForm = form(this.searchModel);

  private readonly motSelectionModel = signal<MotFilterSelectionForm>({
    meansOfTransport: [...(this.data.meansOfTransport ?? [])],
  });
  protected readonly filterForm = form(this.motSelectionModel);

  readonly selectedMeansOfTransport = computed(() => this.filterForm.meansOfTransport().value());

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
    this.motSelectionModel.set({ meansOfTransport: [] });
  }

  apply() {
    this.dialogRef.close({
      businessOrganisations: this.selectedBusinessOrganisations(),
      meansOfTransport: this.selectedMeansOfTransport(),
    });
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
