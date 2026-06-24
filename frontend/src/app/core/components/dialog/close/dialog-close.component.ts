import { ChangeDetectionStrategy, Component, output } from '@angular/core';

@Component({
  selector: 'atlas-dialog-close',
  templateUrl: './dialog-close.component.html',
  changeDetection: ChangeDetectionStrategy.Eager,
  styleUrls: ['./dialog-close.component.scss'],
})
export class DialogCloseComponent {
  readonly clicked = output<void>();
}
