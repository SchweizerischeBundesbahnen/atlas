import { Component } from '@angular/core';
import { FormGroup } from '@angular/forms';
import {
  ServicePointDetailFormGroup,
  ServicePointFormGroupBuilder,
} from '../service-point-side-panel/service-point/service-point-detail-form-group';

@Component({
  selector: 'app-service-point-creation',
  templateUrl: './service-point-creation.component.html',
  styleUrls: ['./service-point-creation.component.scss'],
})
export class ServicePointCreationComponent {
  public form: FormGroup<ServicePointDetailFormGroup> =
    ServicePointFormGroupBuilder.buildEmptyFormGroup();
}
