import { FormControl } from '@angular/forms';
import { SpatialReference } from '../../../api';

export interface GeographyFormGroup {
  east: FormControl<number | null | undefined>;
  north: FormControl<number | null | undefined>;
  height: FormControl<number | null | undefined>;
  spatialReference: FormControl<SpatialReference | null | undefined>;
}
