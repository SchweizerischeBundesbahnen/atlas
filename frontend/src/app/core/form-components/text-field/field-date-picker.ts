import { MatDatepicker } from '@angular/material/datepicker';

export interface FieldDatePicker {
  matDatePicker?: MatDatepicker<Date>;
  minDate?: Date;
  maxDate?: Date;
}
