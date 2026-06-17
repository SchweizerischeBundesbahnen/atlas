import { ChangeDetectionStrategy, Component, Input, input } from '@angular/core';
import { ControlContainer, FormGroup, NgForm, ReactiveFormsModule } from '@angular/forms';
import { ParkingLotFormGroup } from '../parking-lot-form-group';
import { BooleanOptionalAttributeType } from '../../../../../../../api';
import { TextFieldComponent } from '../../../../../../../core/form-components/text-field/text-field.component';
import { DateRangeComponent } from '../../../../../../../core/form-components/date-range/date-range.component';
import { SelectComponent } from '../../../../../../../core/form-components/select/select.component';
import { CommentComponent } from '../../../../../../../core/form-components/comment/comment.component';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
  selector: 'atlas-parking-lot-form',
  templateUrl: './parking-lot-form.component.html',
  changeDetection: ChangeDetectionStrategy.Eager,
  viewProviders: [{ provide: ControlContainer, useExisting: NgForm }],
  imports: [
    TextFieldComponent,
    ReactiveFormsModule,
    DateRangeComponent,
    SelectComponent,
    CommentComponent,
    TranslatePipe,
  ],
  providers: [TranslatePipe],
})
export class ParkingLotFormComponent {

  @Input() form!: FormGroup<ParkingLotFormGroup>;
  readonly isNew = input(false);

  booleanOptionalAttributeTypes = Object.values(BooleanOptionalAttributeType);
}
