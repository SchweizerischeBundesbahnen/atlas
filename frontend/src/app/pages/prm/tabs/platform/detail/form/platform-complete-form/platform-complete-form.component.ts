import { Component, Input, input } from '@angular/core';
import { ControlContainer, FormGroup, NgForm, ReactiveFormsModule } from '@angular/forms';
import { CompletePlatformFormGroup } from '../platform-form-group';
import {
  BasicAttributeType,
  BoardingDeviceAttributeType,
  BooleanOptionalAttributeType,
} from '../../../../../../../api';
import { CommentComponent } from '../../../../../../../core/form-components/comment/comment.component';
import { TextFieldComponent } from '../../../../../../../core/form-components/text-field/text-field.component';
import { SelectComponent } from '../../../../../../../core/form-components/select/select.component';
import { TranslatePipe } from '@ngx-translate/core';
import { LevelAccessWheelchairAttributeType } from '../../../../../../../api/model/levelAccessWheelchairAttributeType';

@Component({
  selector: 'atlas-platform-complete-form',
  templateUrl: './platform-complete-form.component.html',
  viewProviders: [{ provide: ControlContainer, useExisting: NgForm }],
  imports: [CommentComponent, ReactiveFormsModule, TextFieldComponent, SelectComponent, TranslatePipe],
})
export class PlatformCompleteFormComponent {

  @Input() form!: FormGroup<CompletePlatformFormGroup>;
  readonly isNew = input(false);

  booleanOptionalAttributeTypes = Object.values(BooleanOptionalAttributeType);
  basicAttributeType = Object.values(BasicAttributeType);
  boardingDeviceAttributeTypes = Object.values(BoardingDeviceAttributeType);
  levelAccessWheelchairAttributeTypes = Object.values(LevelAccessWheelchairAttributeType);
}
