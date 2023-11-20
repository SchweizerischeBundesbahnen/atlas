import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-stop-point-reduced-form',
  templateUrl: './stop-point-reduced-form.component.html',
  styleUrls: ['./stop-point-reduced-form.component.scss'],
})
export class StopPointReducedFormComponent {
  @Input() form!: any;
}
