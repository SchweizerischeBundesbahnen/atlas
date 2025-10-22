import { FormControl } from '@angular/forms';
import { BusinessType } from '../../../../api';
import { BaseDetailFormGroup } from '../../../../core/components/base-detail/base-detail-form-group';

export interface BusinessOrganisationDetailFormGroup
  extends BaseDetailFormGroup {
  descriptionDe: FormControl<string | null | undefined>;
  descriptionFr: FormControl<string | null | undefined>;
  descriptionIt: FormControl<string | null | undefined>;
  descriptionEn: FormControl<string | null | undefined>;
  abbreviationDe: FormControl<string | null | undefined>;
  abbreviationFr: FormControl<string | null | undefined>;
  abbreviationIt: FormControl<string | null | undefined>;
  abbreviationEn: FormControl<string | null | undefined>;
  organisationNumber: FormControl<number | null | undefined>;
  contactEnterpriseEmail: FormControl<string | null | undefined>;
  businessTypes: FormControl<Set<BusinessType> | null | undefined>;
}
