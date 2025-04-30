import { Component, EventEmitter, Input, Output } from '@angular/core';
import { LinkIconComponent } from '../link-icon/link-icon.component';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
  selector: 'atlas-link',
  templateUrl: './link.component.html',
  styleUrls: ['./link.component.scss'],
  imports: [LinkIconComponent, TranslatePipe],
})
export class LinkComponent {
  @Input() label!: string;
  @Output() linkClicked = new EventEmitter<void>();
}
