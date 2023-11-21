import { Component, Input, OnInit } from '@angular/core';
import { StopPointDetailFormGroup } from '../stop-point-detail-form-group';
import { MeanOfTransport } from '../../../../../api';
import { ControlContainer, FormGroup, NgForm } from '@angular/forms';

@Component({
  selector: 'app-stop-point-reduced-form',
  templateUrl: './stop-point-reduced-form.component.html',
  styleUrls: ['./stop-point-reduced-form.component.scss'],
  viewProviders: [{ provide: ControlContainer, useExisting: NgForm }],
})
export class StopPointReducedFormComponent implements OnInit {
  @Input() form!: FormGroup<StopPointDetailFormGroup>;
  @Input() selectedMeansOfTransport!: MeanOfTransport[];
  @Input() isNew = false;
  ngOnInit(): void {
    if (this.isNew) {
      this.initForm();
    }
  }

  private initForm() {
    this.form.controls['meansOfTransport'].setValue(this.selectedMeansOfTransport);
  }
}
