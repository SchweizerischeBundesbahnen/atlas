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
import {
  ControlContainer,
  FormGroup,
  NgForm,
  ReactiveFormsModule,
} from '@angular/forms';
import { MatSelectChange } from '@angular/material/select';
import { PrmVariantInfoService } from '../../prm-variant-info.service';
import { MeansOfTransportPickerComponent } from '../../../../../sepodi/means-of-transport-picker/means-of-transport-picker.component';
import { MatCheckbox } from '@angular/material/checkbox';
import { AtlasFieldErrorComponent } from '../../../../../../core/form-components/atlas-field-error/atlas-field-error.component';
import { CommentComponent } from '../../../../../../core/form-components/comment/comment.component';
import { TextFieldComponent } from '../../../../../../core/form-components/text-field/text-field.component';
import { DateRangeComponent } from '../../../../../../core/form-components/date-range/date-range.component';
import { SelectComponent } from '../../../../../../core/form-components/select/select.component';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
  selector: 'app-stop-point-complete-form',
  templateUrl: './stop-point-complete-form.component.html',
  viewProviders: [{ provide: ControlContainer, useExisting: NgForm }],
  imports: [
    MeansOfTransportPickerComponent,
    ReactiveFormsModule,
    MatCheckbox,
    AtlasFieldErrorComponent,
    CommentComponent,
    TextFieldComponent,
    DateRangeComponent,
    SelectComponent,
    TranslatePipe,
  ],
})
export class StopPointCompleteFormComponent implements OnInit {
  @Input() form!: FormGroup<StopPointDetailFormGroup>;
  @Input() selectedMeansOfTransport!: MeanOfTransport[];
  @Input() isNew = false;
  standardAttributeTypes: string[] = [];
  booleanOptionalAttributeTypes = Object.values(BooleanOptionalAttributeType);
  meansOfTransportToShow: MeanOfTransport[] | undefined;

  constructor(
    private readonly translationSortingService: TranslationSortingService,
    private readonly prmVariantInfoService: PrmVariantInfoService
  ) {}

  ngOnInit(): void {
    if (this.isNew) {
      this.initForm();
    }
    this.meansOfTransportToShow =
      this.prmVariantInfoService.getPrmMeansOfTransportToShow(
        this.form.controls.meansOfTransport.value!
      );
    this.setSortedOperatingPointTypes();
  }

  private initForm() {
    this.populateCompleteForm();
    StopPointFormGroupBuilder.addCompleteRecordingValidation(this.form);
  }

  private populateCompleteForm() {
    this.form.controls.meansOfTransport.setValue(this.selectedMeansOfTransport);
    StopPointFormGroupBuilder.populateDropdownsForCompleteWithDefaultValue(
      this.form
    );
  }

  private setSortedOperatingPointTypes = (): void => {
    this.standardAttributeTypes = this.translationSortingService.sort(
      Object.values(StandardAttributeType),
      'PRM.STOP_POINTS.STANDARD_ATTRIBUTE_TYPES.'
    );
  };

  updateRelatedFieldsContent(
    selectedAssistanceRequestFulfilled: MatSelectChange
  ) {
    if (this.isNew) {
      if (
        selectedAssistanceRequestFulfilled.value ===
        BooleanOptionalAttributeType.Yes
      ) {
        this.form.controls.assistanceService.setValue(
          StandardAttributeType.NotApplicable
        );
        this.form.controls.assistanceAvailability.setValue(
          StandardAttributeType.NotApplicable
        );
      }
      if (
        selectedAssistanceRequestFulfilled.value ===
          BooleanOptionalAttributeType.No ||
        selectedAssistanceRequestFulfilled.value ===
          BooleanOptionalAttributeType.ToBeCompleted
      ) {
        this.form.controls.assistanceService.setValue(
          StandardAttributeType.ToBeCompleted
        );
        this.form.controls.assistanceAvailability.setValue(
          StandardAttributeType.ToBeCompleted
        );
      }
    } else if (
      !this.isNew &&
      (selectedAssistanceRequestFulfilled.value ===
        BooleanOptionalAttributeType.No ||
        selectedAssistanceRequestFulfilled.value ===
          BooleanOptionalAttributeType.ToBeCompleted)
    ) {
      this.form.controls.assistanceService.setValue(
        StandardAttributeType.ToBeCompleted
      );
      this.form.controls.assistanceAvailability.setValue(
        StandardAttributeType.ToBeCompleted
      );
    }
  }
}
