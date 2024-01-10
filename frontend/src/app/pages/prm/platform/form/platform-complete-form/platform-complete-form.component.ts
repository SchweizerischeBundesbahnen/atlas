import { Component, Input } from '@angular/core';
import { ControlContainer, FormGroup, NgForm } from '@angular/forms';
import {
  BasicAttributeType,
  BoardingDeviceAttributeType,
  BooleanOptionalAttributeType,
} from '../../../../../api';
import { CompletePlatformFormGroup } from '../platform-form-group';

@Component({
  selector: 'app-platform-complete-form',
  templateUrl: './platform-complete-form.component.html',
  viewProviders: [{ provide: ControlContainer, useExisting: NgForm }],
})
export class PlatformCompleteFormComponent {
  @Input() form!: FormGroup<CompletePlatformFormGroup>;
  @Input() isNew = false;

  booleanOptionalAttributeTypes = Object.values(BooleanOptionalAttributeType);
  basicAttributeType = Object.values(BasicAttributeType);
  boardingDeviceAttributeTypes = Object.values(BoardingDeviceAttributeType);
}
