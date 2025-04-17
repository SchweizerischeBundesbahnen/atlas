import { Component, Input, OnInit } from '@angular/core';
import { StopPointDetailFormGroup } from '../stop-point-detail-form-group';
import { MeanOfTransport } from '../../../../../../api';
import {
  ControlContainer,
  FormGroup,
  NgForm,
  ReactiveFormsModule,
} from '@angular/forms';
import { PrmVariantInfoService } from '../../prm-variant-info.service';
import { MeansOfTransportPickerComponent } from '../../../../../sepodi/means-of-transport-picker/means-of-transport-picker.component';
import { CommentComponent } from '../../../../../../core/form-components/comment/comment.component';
import { DateRangeComponent } from '../../../../../../core/form-components/date-range/date-range.component';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
  selector: 'app-stop-point-reduced-form',
  templateUrl: './stop-point-reduced-form.component.html',
  viewProviders: [{ provide: ControlContainer, useExisting: NgForm }],
  imports: [
    MeansOfTransportPickerComponent,
    ReactiveFormsModule,
    CommentComponent,
    DateRangeComponent,
    TranslatePipe,
  ],
})
export class StopPointReducedFormComponent implements OnInit {
  @Input() form!: FormGroup<StopPointDetailFormGroup>;
  @Input() selectedMeansOfTransport!: MeanOfTransport[];
  @Input() isNew = false;
  meansOfTransportToShow: MeanOfTransport[] | undefined;

  constructor(private readonly prmVariantInfoService: PrmVariantInfoService) {}

  ngOnInit(): void {
    if (this.isNew) {
      this.initForm();
    }
    this.meansOfTransportToShow =
      this.prmVariantInfoService.getPrmMeansOfTransportToShow(
        this.form.controls.meansOfTransport.value!
      );
  }

  private initForm() {
    this.form.controls.meansOfTransport.setValue(this.selectedMeansOfTransport);
  }
}
