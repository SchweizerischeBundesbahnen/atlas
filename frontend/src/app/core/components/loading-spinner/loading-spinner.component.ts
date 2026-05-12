import { ChangeDetectionStrategy, Component, input } from '@angular/core';

@Component({
  selector: 'atlas-loading-spinner',
  templateUrl: './loading-spinner.component.html',
  styleUrls: ['./loading-spinner.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class LoadingSpinnerComponent {
  readonly isLoading = input(false);
}
