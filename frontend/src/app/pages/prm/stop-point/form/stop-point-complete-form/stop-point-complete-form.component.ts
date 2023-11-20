import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-stop-point-complete-form',
  templateUrl: './stop-point-complete-form.component.html',
  styleUrls: ['./stop-point-complete-form.component.scss'],
})
export class StopPointCompleteFormComponent {
  @Input() form!: any;
  @Input() standardAttributeTypes!: any;
}
