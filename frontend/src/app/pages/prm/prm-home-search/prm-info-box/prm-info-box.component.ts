import { Component } from '@angular/core';
import { ATTACHMENTS, LINKS } from './prm-info-box';
import { NgFor } from '@angular/common';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
  selector: 'app-prm-info-box',
  templateUrl: './prm-info-box.component.html',
  styleUrls: ['./prm-info-box.component.scss'],
  imports: [NgFor, TranslatePipe],
})
export class PrmInfoBoxComponent {
  attachments = ATTACHMENTS;
  links = LINKS;
}
