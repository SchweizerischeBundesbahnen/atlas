import { Component, Input, input } from '@angular/core';
import { ControlContainer, FormGroup, NgForm, ReactiveFormsModule } from '@angular/forms';
import { CompleteReferencePointFormGroup } from '../reference-point-form-group';
import { ReferencePointAttributeType } from '../../../../../../../api';
import { TextFieldComponent } from '../../../../../../../core/form-components/text-field/text-field.component';
import { DateRangeComponent } from '../../../../../../../core/form-components/date-range/date-range.component';
import { SelectComponent } from '../../../../../../../core/form-components/select/select.component';
import { MatCheckbox } from '@angular/material/checkbox';
import { InfoIconComponent } from '@atlas/form';
import { AtlasFieldErrorComponent } from '../../../../../../../core/form-components/atlas-field-error/atlas-field-error.component';
import { CommentComponent } from '../../../../../../../core/form-components/comment/comment.component';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
  selector: 'atlas-reference-point-complete-form',
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
  providers: [TranslatePipe],
})
export class ReferencePointCompleteFormComponent {

  @Input() form!: FormGroup<CompleteReferencePointFormGroup>;
  readonly isNew = input(false);

  referencePointTypes = Object.values(ReferencePointAttributeType);
}
