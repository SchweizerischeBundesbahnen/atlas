import { Component, Input, OnInit, input } from '@angular/core';
import { ControlContainer, FormGroup, NgForm, ReactiveFormsModule } from '@angular/forms';
import { ReducedPlatformFormGroup } from '../platform-form-group';
import {
  BooleanOptionalAttributeType,
  InfoOpportunityAttributeType,
  MeanOfTransport,
  VehicleAccessAttributeType,
} from '../../../../../../../api';
import { SelectComponent } from '../../../../../../../core/form-components/select/select.component';
import { CommentComponent } from '../../../../../../../core/form-components/comment/comment.component';
import { TextFieldComponent } from '../../../../../../../core/form-components/text-field/text-field.component';
import { MatCheckbox } from '@angular/material/checkbox';
import { InfoIconComponent } from '@atlas/form';
import { AtlasFieldErrorComponent } from '../../../../../../../core/form-components/atlas-field-error/atlas-field-error.component';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
  selector: 'atlas-platform-reduced-form',
  templateUrl: './platform-reduced-form.component.html',
  viewProviders: [{ provide: ControlContainer, useExisting: NgForm }],
  imports: [
    SelectComponent,
    ReactiveFormsModule,
    CommentComponent,
    TextFieldComponent,
    MatCheckbox,
    InfoIconComponent,
    AtlasFieldErrorComponent,
    TranslatePipe,
  ],
  providers: [TranslatePipe],
})
export class PlatformReducedFormComponent implements OnInit {
  // TODO: Skipped for migration because:
  //  Your application code writes to the input. This prevents migration.
  @Input() form!: FormGroup<ReducedPlatformFormGroup>;
  readonly meansOfTransport = input<MeanOfTransport[]>([]);
  readonly isNew = input(false);

  booleanOptionalAttributeTypes = Object.values(BooleanOptionalAttributeType);
  vehicleAccess = Object.values(VehicleAccessAttributeType);
  infoOpportunities = Object.values(InfoOpportunityAttributeType);

  ngOnInit(): void {
    this.form.controls.infoOpportunities.valueChanges.subscribe((infoOpportunities) => {
      if (infoOpportunities) {
        if (infoOpportunities.length == 0) {
          this.form.controls.infoOpportunities.setValue([InfoOpportunityAttributeType.ToBeCompleted]);
        }
        if (infoOpportunities?.length > 1 && infoOpportunities?.includes(InfoOpportunityAttributeType.ToBeCompleted)) {
          this.form.controls.infoOpportunities.setValue(
            infoOpportunities.filter((i) => i !== InfoOpportunityAttributeType.ToBeCompleted)
          );
        }
      }
    });
  }

  protected readonly MeanOfTransport = MeanOfTransport;
}
