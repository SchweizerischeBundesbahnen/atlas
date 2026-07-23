import { Component, input } from '@angular/core';

@Component({
  selector: 'atlas-loading-spinner',
  templateUrl: './loading-spinner.component.html',
  styleUrls: ['./loading-spinner.component.scss'],
})
export class LoadingSpinnerComponent {
  readonly isLoading = input(false);
}
