import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DateRangeComponent } from '../form-components/date-range/date-range.component';
import { MaterialModule } from './material.module';
import { TranslateModule } from '@ngx-translate/core';
import { FieldErrorComponent } from '../form-components/field-error/field-error.component';

@NgModule({
  declarations: [DateRangeComponent, FieldErrorComponent],
  imports: [CommonModule, MaterialModule, TranslateModule],
  exports: [DateRangeComponent, FieldErrorComponent],
})
export class FormModule {}
