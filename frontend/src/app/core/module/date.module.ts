import { ModuleWithProviders, NgModule } from '@angular/core';
import { MomentDateAdapter } from '@angular/material-moment-adapter';
import {
  DateAdapter,
  MAT_DATE_FORMATS,
  MAT_DATE_LOCALE,
} from '@angular/material/core';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { DATE_PATTERN } from '../date/date.service';

export const FORMAT = {
  parse: {
    dateInput: DATE_PATTERN,
  },
  display: {
    dateInput: DATE_PATTERN,
    monthYearLabel: 'MMM YYYY',
    dateA11yLabel: 'LL',
    monthYearA11yLabel: 'MMMM YYYY',
  },
};

@NgModule({
  imports: [],
  exports: [MatDatepickerModule],
})
export class DateModule {
  static forRoot(): ModuleWithProviders<DateModule> {
    return {
      ngModule: DateModule,
      providers: [
        {
          provide: DateAdapter,
          useClass: MomentDateAdapter,
          deps: [MAT_DATE_LOCALE],
        },
        { provide: MAT_DATE_FORMATS, useValue: FORMAT },
      ],
    };
  }
}
