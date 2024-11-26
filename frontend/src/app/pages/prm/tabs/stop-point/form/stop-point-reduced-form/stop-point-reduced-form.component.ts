import { Component, Input, OnInit } from '@angular/core';
import { StopPointDetailFormGroup } from '../stop-point-detail-form-group';
import { MeanOfTransport } from '../../../../../../api';
import { ControlContainer, FormGroup, NgForm } from '@angular/forms';
import { PrmVariantInfoService } from '../../prm-variant-info.service';

@Component({
  selector: 'app-stop-point-reduced-form',
  templateUrl: './stop-point-reduced-form.component.html',
  viewProviders: [{ provide: ControlContainer, useExisting: NgForm }],
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
    this.meansOfTransportToShow = this.prmVariantInfoService.getPrmMeansOfTransportToShow(
      this.form.controls.meansOfTransport.value!,
    );
  }

  private initForm() {
    this.form.controls.meansOfTransport.setValue(this.selectedMeansOfTransport);
  }
}
