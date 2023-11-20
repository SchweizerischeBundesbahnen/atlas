import { Component, Input, OnInit } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { StopPointDetailFormGroup } from '../stop-point-detail-form-group';
import { MeanOfTransport } from '../../../../../api';

@Component({
  selector: 'app-stop-point-complete-form',
  templateUrl: './stop-point-complete-form.component.html',
  styleUrls: ['./stop-point-complete-form.component.scss'],
})
export class StopPointCompleteFormComponent implements OnInit {
  @Input() form!: FormGroup<StopPointDetailFormGroup>;
  @Input() standardAttributeTypes!: any;
  @Input() selectedMeansOfTransport!: MeanOfTransport[];

  ngOnInit(): void {
    this.form.controls['meansOfTransport'].setValue(this.selectedMeansOfTransport);
  }
}
