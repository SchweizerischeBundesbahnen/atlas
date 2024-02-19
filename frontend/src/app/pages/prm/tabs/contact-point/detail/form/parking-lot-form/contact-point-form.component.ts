import {Component, Input} from '@angular/core';
import {ControlContainer, FormGroup, NgForm} from '@angular/forms';
import {ContactPointFormGroup} from '../contact-point-form-group';
import {BooleanOptionalAttributeType} from '../../../../../../../api';

@Component({
  selector: 'app-contact-point-form',
  templateUrl: './contact-point-form.component.html',
  viewProviders: [{ provide: ControlContainer, useExisting: NgForm }],
})
export class ContactPointFormComponent {
  @Input() form!: FormGroup<ContactPointFormGroup>;
  @Input() isNew = false;

  booleanOptionalAttributeTypes = Object.values(BooleanOptionalAttributeType);
}
