import { FormControl } from '@angular/forms';
import { BusinessType } from '../../../../api';
import { BaseDetailFormGroup } from '../../../../core/components/base-detail/base-detail-form-group';

export interface BusinessOrganisationDetailFormGroup
  extends BaseDetailFormGroup {
  descriptionDe: FormControl<string | null>;
  descriptionFr: FormControl<string | null>;
  descriptionIt: FormControl<string | null>;
  descriptionEn: FormControl<string | null>;
  abbreviationDe: FormControl<string | null>;
  abbreviationFr: FormControl<string | null>;
  abbreviationIt: FormControl<string | null>;
  abbreviationEn: FormControl<string | null>;
  organisationNumber: FormControl<number | null | undefined>;
  contactEnterpriseEmail: FormControl<string | null | undefined>;
  businessTypes: FormControl<Set<BusinessType> | null | undefined>;
}
