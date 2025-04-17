import { Component, Input } from '@angular/core';
import {
  ControlContainer,
  FormGroup,
  NgForm,
  ReactiveFormsModule,
} from '@angular/forms';
import { CompleteReferencePointFormGroup } from '../reference-point-form-group';
import { ReferencePointAttributeType } from '../../../../../../../api';
import { TextFieldComponent } from '../../../../../../../core/form-components/text-field/text-field.component';
import { DateRangeComponent } from '../../../../../../../core/form-components/date-range/date-range.component';
import { SelectComponent } from '../../../../../../../core/form-components/select/select.component';
import { MatCheckbox } from '@angular/material/checkbox';
import { InfoIconComponent } from '../../../../../../../core/form-components/info-icon/info-icon.component';
import { AtlasFieldErrorComponent } from '../../../../../../../core/form-components/atlas-field-error/atlas-field-error.component';
import { CommentComponent } from '../../../../../../../core/form-components/comment/comment.component';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
  selector: 'app-reference-point-complete-form',
  templateUrl: './reference-point-complete-form.component.html',
  viewProviders: [{ provide: ControlContainer, useExisting: NgForm }],
  imports: [
    TextFieldComponent,
    ReactiveFormsModule,
    DateRangeComponent,
    SelectComponent,
    MatCheckbox,
    InfoIconComponent,
    AtlasFieldErrorComponent,
    CommentComponent,
    TranslatePipe,
  ],
})
export class ReferencePointCompleteFormComponent {
  @Input() form!: FormGroup<CompleteReferencePointFormGroup>;
  @Input() isNew = false;

  referencePointTypes = Object.values(ReferencePointAttributeType);
}
