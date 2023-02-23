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
import { BusinessOrganisationSelectComponent } from '../form-components/bo-select/business-organisation-select.component';
import { DialogCloseComponent } from '../form-components/dialog-close/dialog-close.component';
import { BoSelectionDisplayPipe } from '../form-components/bo-select/bo-selection-display.pipe';
import { LinkIconComponent } from '../form-components/link-icon/link-icon.component';
import { InfoLinkDirective } from '../form-components/info-icon/info-link.directive';
import { TextFieldComponent } from '../form-components/text-field/text-field.component';

@NgModule({
  declarations: [
    BusinessOrganisationSelectComponent,
    DateRangeComponent,
    FieldErrorComponent,
    CommentComponent,
    UrlComponent,
    DateIconComponent,
    InfoIconComponent,
    LinkIconComponent,
    SearchSelectComponent,
    DialogCloseComponent,
    BoSelectionDisplayPipe,
    InfoLinkDirective,
    TextFieldComponent,
  ],
  imports: [CommonModule, MaterialModule, TranslateModule],
  exports: [
    BusinessOrganisationSelectComponent,
    DateRangeComponent,
    FieldErrorComponent,
    CommentComponent,
    UrlComponent,
    DateIconComponent,
    InfoIconComponent,
    SearchSelectComponent,
    DialogCloseComponent,
    LinkIconComponent,
    InfoLinkDirective,
    TextFieldComponent,
  ],
})
export class FormModule {}
