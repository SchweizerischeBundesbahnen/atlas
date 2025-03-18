import { Component, Input } from '@angular/core';
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

@Component({
    selector: 'app-platform-complete-form',
    templateUrl: './platform-complete-form.component.html',
    viewProviders: [{ provide: ControlContainer, useExisting: NgForm }],
    imports: [CommentComponent, ReactiveFormsModule, TextFieldComponent, SelectComponent, TranslatePipe]
})
export class PlatformCompleteFormComponent {
  @Input() form!: FormGroup<CompletePlatformFormGroup>;
  @Input() isNew = false;

  booleanOptionalAttributeTypes = Object.values(BooleanOptionalAttributeType);
  basicAttributeType = Object.values(BasicAttributeType);
  boardingDeviceAttributeTypes = Object.values(BoardingDeviceAttributeType);
}
