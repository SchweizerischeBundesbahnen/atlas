import { Component, Input, input } from '@angular/core';
import { ControlContainer, FormGroup, NgForm, ReactiveFormsModule } from '@angular/forms';
import { ContactPointFormGroup } from '../contact-point-form-group';
import { ContactPointType, StandardAttributeType } from '../../../../../../../api';
import { SelectComponent } from '../../../../../../../core/form-components/select/select.component';
import { TextFieldComponent } from '../../../../../../../core/form-components/text-field/text-field.component';
import { DateRangeComponent } from '../../../../../../../core/form-components/date-range/date-range.component';
import { CommentComponent } from '../../../../../../../core/form-components/comment/comment.component';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
  selector: 'atlas-contact-point-form',
  templateUrl: './contact-point-form.component.html',
  viewProviders: [{ provide: ControlContainer, useExisting: NgForm }],
  imports: [
    SelectComponent,
    ReactiveFormsModule,
    TextFieldComponent,
    DateRangeComponent,
    CommentComponent,
    TranslatePipe,
  ],
})
export class ContactPointFormComponent {
  // TODO: Skipped for migration because:
  //  Your application code writes to the input. This prevents migration.
  @Input() form!: FormGroup<ContactPointFormGroup>;
  readonly isNew = input(false);

  standardAttributeType = Object.values(StandardAttributeType);
  types = Object.values(ContactPointType);
}
