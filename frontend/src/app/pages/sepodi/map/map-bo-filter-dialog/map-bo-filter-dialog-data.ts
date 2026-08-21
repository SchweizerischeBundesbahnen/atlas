import { BusinessOrganisation } from '../../../../api';
import { DialogData } from '../../../../core/components/dialog/dialog.data';

export interface MapBoFilterDialogData extends DialogData {
  businessOrganisations: BusinessOrganisation[];
}
