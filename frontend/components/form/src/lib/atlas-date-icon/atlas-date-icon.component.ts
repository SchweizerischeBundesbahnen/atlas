import { Component, input } from '@angular/core';

@Component({
  selector: 'atlas-date-icon',
  templateUrl: './atlas-date-icon.component.html',
  styleUrls: ['./atlas-date-icon.component.scss'],
})
export class AtlasDateIconComponent {
  readonly enabled = input.required<boolean>();
}
