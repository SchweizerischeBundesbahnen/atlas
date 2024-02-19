import {Component, Input} from '@angular/core';
import {ControlContainer, FormGroup, NgForm} from '@angular/forms';
import {ParkingLotFormGroup} from '../parking-lot-form-group';
import {BooleanOptionalAttributeType} from '../../../../../../../api';

@Component({
  selector: 'app-parking-lot-form',
  templateUrl: './parking-lot-form.component.html',
  viewProviders: [{ provide: ControlContainer, useExisting: NgForm }],
})
export class ParkingLotFormComponent {
  @Input() form!: FormGroup<ParkingLotFormGroup>;
  @Input() isNew = false;

  booleanOptionalAttributeTypes = Object.values(BooleanOptionalAttributeType);
}
