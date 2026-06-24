import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ATTACHMENTS, LINKS } from './prm-info-box';

import { TranslatePipe } from '@ngx-translate/core';

@Component({
  selector: 'atlas-prm-info-box',
  templateUrl: './prm-info-box.component.html',
  changeDetection: ChangeDetectionStrategy.Eager,
  styleUrls: ['./prm-info-box.component.scss'],
  imports: [TranslatePipe],
})
export class PrmInfoBoxComponent {
  attachments = ATTACHMENTS;
  links = LINKS;
}
