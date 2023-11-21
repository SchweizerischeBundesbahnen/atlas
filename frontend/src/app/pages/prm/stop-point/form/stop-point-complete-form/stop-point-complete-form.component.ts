import { ChangeDetectionStrategy, Component, Input, OnInit } from '@angular/core';
import { StopPointDetailFormGroup } from '../stop-point-detail-form-group';
import { MeanOfTransport, StandardAttributeType } from '../../../../../api';
import { TranslationSortingService } from '../../../../../core/translation/translation-sorting.service';
import { FormGroup } from '@angular/forms';

@Component({
  selector: 'app-stop-point-complete-form',
  templateUrl: './stop-point-complete-form.component.html',
  styleUrls: ['./stop-point-complete-form.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class StopPointCompleteFormComponent implements OnInit {
  @Input() form!: FormGroup<StopPointDetailFormGroup>;
  @Input() standardAttributeTypes!: any;
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
    this.form.controls['meansOfTransport'].setValue(this.selectedMeansOfTransport);
    this.form.controls['alternativeTransport'].setValue(StandardAttributeType.ToBeCompleted);
    this.form.controls['assistanceAvailability'].setValue(StandardAttributeType.ToBeCompleted);
    this.form.controls['assistanceService'].setValue(StandardAttributeType.ToBeCompleted);
    this.form.controls['audioTicketMachine'].setValue(StandardAttributeType.ToBeCompleted);
    this.form.controls['dynamicAudioSystem'].setValue(StandardAttributeType.ToBeCompleted);
    this.form.controls['dynamicOpticSystem'].setValue(StandardAttributeType.ToBeCompleted);
    this.form.controls['visualInfo'].setValue(StandardAttributeType.ToBeCompleted);
    this.form.controls['wheelchairTicketMachine'].setValue(StandardAttributeType.ToBeCompleted);
    this.form.controls['assistanceRequestFulfilled'].setValue(StandardAttributeType.ToBeCompleted);
    this.form.controls['ticketMachine'].setValue(StandardAttributeType.ToBeCompleted);
  }

  private setSortedOperatingPointTypes = (): void => {
    this.standardAttributeTypes = this.translationSortingService.sort(
      Object.values(StandardAttributeType),
      'PRM.STOP_POINTS.STANDARD_ATTRIBUTE_TYPES.',
    );
  };
}
