import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DateRangeComponent } from '../form-components/date-range/date-range.component';
import { MaterialModule } from './material.module';
import { TranslateModule } from '@ngx-translate/core';
import { FieldErrorComponent } from '../form-components/field-error/field-error.component';
import { CommentComponent } from '../form-components/comment/comment.component';
import { UrlComponent } from '../form-components/url/url.component';
import { DateIconComponent } from '../form-components/date-icon/date-icon.component';
import { InfoIconComponent } from '../form-components/info-icon/info-icon.component';
import { SearchSelectComponent } from '../form-components/search-select/search-select.component';

@NgModule({
  declarations: [
    DateRangeComponent,
    FieldErrorComponent,
    CommentComponent,
    UrlComponent,
    DateIconComponent,
    InfoIconComponent,
    SearchSelectComponent,
  ],
  imports: [CommonModule, MaterialModule, TranslateModule],
  exports: [
    DateRangeComponent,
    FieldErrorComponent,
    CommentComponent,
    UrlComponent,
    DateIconComponent,
    InfoIconComponent,
    SearchSelectComponent,
  ],
})
export class FormModule {}
