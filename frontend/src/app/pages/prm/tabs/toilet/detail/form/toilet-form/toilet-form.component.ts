import { Component, Input } from '@angular/core';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';
import { StandardAttributeType } from '../../../../../../../api';
import { ToiletFormGroup } from '../toilet-form-group';
import { TextFieldComponent } from '../../../../../../../core/form-components/text-field/text-field.component';
import { DateRangeComponent } from '../../../../../../../core/form-components/date-range/date-range.component';
import { SelectComponent } from '../../../../../../../core/form-components/select/select.component';
import { CommentComponent } from '../../../../../../../core/form-components/comment/comment.component';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
  selector: 'app-toilet-form',
  templateUrl: './toilet-form.component.html',
  imports: [
    TextFieldComponent,
    ReactiveFormsModule,
    DateRangeComponent,
    SelectComponent,
    CommentComponent,
    TranslatePipe,
  ],
})
export class ToiletFormComponent {
  @Input() form!: FormGroup<ToiletFormGroup>;

  standardAttributeTypes = Object.values(StandardAttributeType);
}
