import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DateRangeComponent } from '../form-components/date-range/date-range.component';
import { MaterialModule } from './material.module';
import { TranslateModule } from '@ngx-translate/core';
import { CommentComponent } from '../form-components/comment/comment.component';
import { DateIconComponent } from '../form-components/date-icon/date-icon.component';
import { InfoIconComponent } from '../form-components/info-icon/info-icon.component';
import { SearchSelectComponent } from '../form-components/search-select/search-select.component';
import { BusinessOrganisationSelectComponent } from '../form-components/bo-select/business-organisation-select.component';
import { BoSelectionDisplayPipe } from '../form-components/bo-select/bo-selection-display.pipe';
import { LinkIconComponent } from '../form-components/link-icon/link-icon.component';
import { InfoLinkDirective } from '../form-components/info-icon/info-link.directive';
import { TextFieldComponent } from '../form-components/text-field/text-field.component';
import { AtlasFieldErrorComponent } from '../form-components/atlas-field-error/atlas-field-error.component';
import { AtlasLabelFieldComponent } from '../form-components/atlas-label-field/atlas-label-field.component';
import { DownloadIconComponent } from '../form-components/download-icon/download-icon.component';
import { UploadIconComponent } from '../form-components/upload-icon/upload-icon.component';
import { TimetableFieldNumberSelectComponent } from '../form-components/ttfn-select/timetable-field-number-select.component';
import { TimetableFieldNumberSelectOptionPipe } from '../form-components/ttfn-select/ttfn-select-option.pipe';
import { TransportCompanySelectComponent } from '../form-components/tu-select/transport-company-select.component';
import { AtlasSlideToggleComponent } from '../form-components/atlas-slide-toggle/atlas-slide-toggle.component';
import { SloidComponent } from '../form-components/sloid/sloid.component';
import { TodayAndFutureTimetableHeaderComponent } from '../form-components/date-range/today-and-future-timetable-header/today-and-future-timetable-header.component';
import { AtlasClipboardComponent } from '../form-components/atlas-clipboard/atlas-clipboard.component';
import { CdkCopyToClipboard } from '@angular/cdk/clipboard';
import { BoDisplayPipe } from '../form-components/bo-select/bo-display.pipe';
import { LinkComponent } from '../form-components/link/link.component';
import { DialogCloseComponent } from '../components/dialog/close/dialog-close.component';
import { EmptyToNullDirective } from '../text-input/empty-to-null';

@NgModule({
  imports: [
    CommonModule,
    MaterialModule,
    TranslateModule,
    CdkCopyToClipboard,
    BusinessOrganisationSelectComponent,
    TransportCompanySelectComponent,
    TimetableFieldNumberSelectComponent,
    DateRangeComponent,
    TodayAndFutureTimetableHeaderComponent,
    CommentComponent,
    DateIconComponent,
    DownloadIconComponent,
    UploadIconComponent,
    InfoIconComponent,
    LinkComponent,
    LinkIconComponent,
    SearchSelectComponent,
    DialogCloseComponent,
    BoSelectionDisplayPipe,
    TimetableFieldNumberSelectOptionPipe,
    InfoLinkDirective,
    TextFieldComponent,
    AtlasFieldErrorComponent,
    AtlasLabelFieldComponent,
    AtlasSlideToggleComponent,
    SloidComponent,
    AtlasClipboardComponent,
    BoDisplayPipe,
    EmptyToNullDirective,
  ],
  exports: [
    BusinessOrganisationSelectComponent,
    TimetableFieldNumberSelectComponent,
    TransportCompanySelectComponent,
    DateRangeComponent,
    CommentComponent,
    DateIconComponent,
    DownloadIconComponent,
    UploadIconComponent,
    InfoIconComponent,
    SearchSelectComponent,
    DialogCloseComponent,
    LinkComponent,
    LinkIconComponent,
    InfoLinkDirective,
    TextFieldComponent,
    AtlasFieldErrorComponent,
    AtlasLabelFieldComponent,
    AtlasSlideToggleComponent,
    SloidComponent,
    AtlasClipboardComponent,
    BoSelectionDisplayPipe,
    BoDisplayPipe,
  ],
  providers: [BoSelectionDisplayPipe],
})
export class FormModule {}
