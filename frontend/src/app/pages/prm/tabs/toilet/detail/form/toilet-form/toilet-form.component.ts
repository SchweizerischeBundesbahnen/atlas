import { Component, Input } from '@angular/core';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';
import { StandardAttributeType } from '../../../../../../../api';
import { ToiletFormGroup } from '../toilet-form-group';
import { TextFieldComponent } from '../../../../../../../core/form-components/text-field/text-field.component';
import { SelectComponent } from '../../../../../../../core/form-components/select/select.component';
import { TranslatePipe } from '@ngx-translate/core';
import { CommentComponent } from '../../../../../../../core/form-components/comment/comment.component';
import { DateRangeComponent } from '../../../../../../../core/form-components/date-range/date-range.component';

@Component({
  selector: 'atlas-toilet-form',
  templateUrl: './toilet-form.component.html',
  imports: [
    TextFieldComponent,
    ReactiveFormsModule,
    SelectComponent,
    TranslatePipe,
    CommentComponent,
    DateRangeComponent,
  ],
  providers: [TranslatePipe],
})
export class ToiletFormComponent {

  @Input() form!: FormGroup<ToiletFormGroup>;

  standardAttributeTypes = Object.values(StandardAttributeType);
}
