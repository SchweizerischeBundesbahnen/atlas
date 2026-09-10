import { BusinessOrganisation, MeanOfTransport } from '../../../../api';
import { DialogData } from '../../../../core/components/dialog/dialog.data';

export interface MapServicePointFilterDialogData extends DialogData {
  businessOrganisations: BusinessOrganisation[];
  meansOfTransport: MeanOfTransport[];
}

export interface MapServicePointFilterDialogResult {
  businessOrganisations: BusinessOrganisation[];
  meansOfTransport: MeanOfTransport[];
}
