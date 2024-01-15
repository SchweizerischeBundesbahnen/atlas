import { Component, Input, OnInit } from '@angular/core';
import { ControlContainer, FormGroup, NgForm } from '@angular/forms';
import { ReducedPlatformFormGroup } from '../platform-form-group';
import {
  BooleanOptionalAttributeType,
  InfoOpportunityAttributeType,
  VehicleAccessAttributeType,
} from '../../../../../../../api';

@Component({
  selector: 'app-platform-reduced-form',
  templateUrl: './platform-reduced-form.component.html',
  viewProviders: [{ provide: ControlContainer, useExisting: NgForm }],
})
export class PlatformReducedFormComponent implements OnInit {
  @Input() form!: FormGroup<ReducedPlatformFormGroup>;
  @Input() isNew = false;

  booleanOptionalAttributeTypes = Object.values(BooleanOptionalAttributeType);
  vehicleAccess = Object.values(VehicleAccessAttributeType);
  infoOpportunities = Object.values(InfoOpportunityAttributeType);

  ngOnInit(): void {
    this.form.controls.infoOpportunities.valueChanges.subscribe((infoOpportunities) => {
      if (infoOpportunities) {
        if (infoOpportunities.length == 0) {
          this.form.controls.infoOpportunities.setValue([
            InfoOpportunityAttributeType.ToBeCompleted,
          ]);
        }
        if (
          infoOpportunities?.length > 1 &&
          infoOpportunities?.includes(InfoOpportunityAttributeType.ToBeCompleted)
        ) {
          this.form.controls.infoOpportunities.setValue(
            infoOpportunities.filter((i) => i !== InfoOpportunityAttributeType.ToBeCompleted),
          );
        }
      }
    });
  }
}
