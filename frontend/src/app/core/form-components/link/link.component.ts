import { Component, output, input } from '@angular/core';
import { LinkIconComponent } from '../link-icon/link-icon.component';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
  selector: 'atlas-link',
  templateUrl: './link.component.html',
  styleUrls: ['./link.component.scss'],
  imports: [LinkIconComponent, TranslatePipe],
  providers: [TranslatePipe],
})
export class LinkComponent {
  readonly label = input.required<string>();
  readonly linkClicked = output<void>();
}
