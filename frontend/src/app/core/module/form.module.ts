import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DateRangeComponent } from '../form-components/date-range/date-range.component';
import { MaterialModule } from './material.module';
import { TranslateModule } from '@ngx-translate/core';
import { FieldErrorComponent } from '../form-components/field-error/field-error.component';
import { CommentComponent } from '../form-components/comment/comment.component';
import { CoreModule } from './core.module';

@NgModule({
  declarations: [DateRangeComponent, FieldErrorComponent, CommentComponent],
  imports: [CommonModule, MaterialModule, TranslateModule, CoreModule],
  exports: [DateRangeComponent, FieldErrorComponent, CommentComponent],
})
export class FormModule {}
