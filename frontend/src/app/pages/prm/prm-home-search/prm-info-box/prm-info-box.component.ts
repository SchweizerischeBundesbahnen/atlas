import { Component } from '@angular/core';
import { ATTACHMENTS, LINKS } from './prm-info-box';

@Component({
  selector: 'app-prm-info-box',
  templateUrl: './prm-info-box.component.html',
  styleUrls: ['./prm-info-box.component.scss'],
})
export class PrmInfoBoxComponent {
  attachments = ATTACHMENTS;
  links = LINKS;
}
