import { Component, Input } from '@angular/core';
import { ControlContainer, FormGroup, NgForm } from '@angular/forms';
import { CompleteReferencePointFormGroup } from '../reference-point-form-group';
import { ReferencePointAttributeType } from '../../../../../../../api';

@Component({
  selector: 'app-reference-point-complete-form',
  templateUrl: './reference-point-complete-form.component.html',
  viewProviders: [{ provide: ControlContainer, useExisting: NgForm }],
})
export class ReferencePointCompleteFormComponent {
  @Input() form!: FormGroup<CompleteReferencePointFormGroup>;
  @Input() isNew = false;

  referencePointTypes = Object.values(ReferencePointAttributeType);
}
