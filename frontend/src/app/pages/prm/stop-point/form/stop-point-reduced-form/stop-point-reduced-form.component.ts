import { Component, Input, OnInit } from '@angular/core';
import { StopPointFormGroupBuilder } from '../stop-point-detail-form-group';
import { MeanOfTransport } from '../../../../../api';

@Component({
  selector: 'app-stop-point-reduced-form',
  templateUrl: './stop-point-reduced-form.component.html',
  styleUrls: ['./stop-point-reduced-form.component.scss'],
})
export class StopPointReducedFormComponent implements OnInit {
  @Input() form = StopPointFormGroupBuilder.buildEmptyReducedFormGroup();
  @Input() selectedMeansOfTransport!: MeanOfTransport[];

  ngOnInit(): void {
    this.form.controls['meansOfTransport'].setValue(this.selectedMeansOfTransport);
  }
}
