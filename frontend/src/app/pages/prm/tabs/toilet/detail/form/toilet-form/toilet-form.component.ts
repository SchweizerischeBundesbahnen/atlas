import {Component, Input} from '@angular/core';
import {FormGroup} from "@angular/forms";
import {StandardAttributeType} from "../../../../../../../api";
import {ToiletFormGroup} from "../toilet-form-group";

@Component({
    selector: 'app-toilet-form',
    templateUrl: './toilet-form.component.html',
    standalone: false
})
export class ToiletFormComponent {

  @Input() form!: FormGroup<ToiletFormGroup>;

  standardAttributeTypes = Object.values(StandardAttributeType);

}
