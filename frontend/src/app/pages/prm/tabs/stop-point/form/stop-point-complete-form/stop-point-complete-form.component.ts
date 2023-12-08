import { Component, Input, OnInit } from '@angular/core';
import {
  StopPointDetailFormGroup,
  StopPointFormGroupBuilder,
} from '../stop-point-detail-form-group';
import {
  BooleanOptionalAttributeType,
  MeanOfTransport,
  StandardAttributeType,
} from '../../../../../../api';
import { TranslationSortingService } from '../../../../../../core/translation/translation-sorting.service';
import { ControlContainer, FormGroup, NgForm } from '@angular/forms';
import { MatSelectChange } from '@angular/material/select';

@Component({
  selector: 'app-stop-point-complete-form',
  templateUrl: './stop-point-complete-form.component.html',
  viewProviders: [{ provide: ControlContainer, useExisting: NgForm }],
})
export class StopPointCompleteFormComponent implements OnInit {
  @Input() form!: FormGroup<StopPointDetailFormGroup>;
  standardAttributeTypes: string[] = [];
  booleanOptionalAttributeTypes = Object.values(BooleanOptionalAttributeType);
  @Input() selectedMeansOfTransport!: MeanOfTransport[];
  @Input() isNew = false;

  constructor(private readonly translationSortingService: TranslationSortingService) {}

  ngOnInit(): void {
    if (this.isNew) {
      this.initForm();
    }
    this.setSortedOperatingPointTypes();
  }

  private initForm() {
    this.populateCompleteForm();
    StopPointFormGroupBuilder.addCompleteRecordingValidation(this.form);
  }

  private populateCompleteForm() {
    this.form.controls.meansOfTransport.setValue(this.selectedMeansOfTransport);
    StopPointFormGroupBuilder.populateDropdownsForCompleteWithDefaultValue(this.form);
  }

  private setSortedOperatingPointTypes = (): void => {
    this.standardAttributeTypes = this.translationSortingService.sort(
      Object.values(StandardAttributeType),
      'PRM.STOP_POINTS.STANDARD_ATTRIBUTE_TYPES.',
    );
  };

  updateRelatedFieldsContent(selectedAssistanceRequestFulfilled: MatSelectChange) {
    if (this.isNew) {
      if (selectedAssistanceRequestFulfilled.value === BooleanOptionalAttributeType.Yes) {
        this.form.controls.assistanceService.setValue(StandardAttributeType.NotApplicable);
        this.form.controls.assistanceAvailability.setValue(StandardAttributeType.NotApplicable);
      }
      if (
        selectedAssistanceRequestFulfilled.value === BooleanOptionalAttributeType.No ||
        selectedAssistanceRequestFulfilled.value === BooleanOptionalAttributeType.ToBeCompleted
      ) {
        this.form.controls.assistanceService.setValue(StandardAttributeType.ToBeCompleted);
        this.form.controls.assistanceAvailability.setValue(StandardAttributeType.ToBeCompleted);
      }
    } else if (
      !this.isNew &&
      (selectedAssistanceRequestFulfilled.value === BooleanOptionalAttributeType.No ||
        selectedAssistanceRequestFulfilled.value === BooleanOptionalAttributeType.ToBeCompleted)
    ) {
      this.form.controls.assistanceService.setValue(StandardAttributeType.ToBeCompleted);
      this.form.controls.assistanceAvailability.setValue(StandardAttributeType.ToBeCompleted);
    }
  }
}
