import { Component, input } from '@angular/core';

@Component({
  selector: 'atlas-info-icon',
  templateUrl: './atlas-info-icon.component.html',
  styleUrls: ['./atlas-info-icon.component.scss'],
})
export class AtlasInfoIconComponent {
  readonly infoTitle = input('');
}
