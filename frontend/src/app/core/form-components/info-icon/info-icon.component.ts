import { Component, Input } from '@angular/core';

@Component({
  selector: 'form-info-icon',
  templateUrl: './info-icon.component.html',
  styleUrls: ['./info-icon.component.scss'],
})
export class InfoIconComponent {
  @Input() infoTitle = '';
}
