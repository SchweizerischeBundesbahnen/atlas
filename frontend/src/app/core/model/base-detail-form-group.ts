import { FormControl } from '@angular/forms';
import { Moment } from 'moment';

export interface BaseDetailFormGroup {
  validFrom: FormControl<Moment | null>;
  validTo: FormControl<Moment | null>;
  etagVersion: FormControl<number | null | undefined>;
  creationDate: FormControl<string | null | undefined>;
  editionDate: FormControl<string | null | undefined>;
  creator: FormControl<string | null | undefined>;
  editor: FormControl<string | null | undefined>;
}

export interface BaseDetailFormModel {
  validFrom: Moment | null;
  validTo: Moment | null;
  etagVersion: number | null;
  creationDate: string | null;
  editionDate: string | null;
  editor: string | null;
  creator: string | null;
}
