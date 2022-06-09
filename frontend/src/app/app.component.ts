import { Component } from '@angular/core';
import { LoadingSpinnerService } from './core/components/loading-spinner/loading-spinner.service';
import { animate, style, transition, trigger } from '@angular/animations';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss'],
  animations: [
    trigger('fadeInOut', [
      transition('true <=> false', [style({ opacity: 0 }), animate(650, style({ opacity: 1 }))]),
    ]),
  ],
})
export class AppComponent {
  constructor(public loadingSpinnerService: LoadingSpinnerService) {
    this.loadingSpinnerService.initLoadingSpinner();
  }
}

Date.prototype.toISOString = function () {
  return (
    this.getFullYear() +
    '-' +
    ('0' + (this.getMonth() + 1)).slice(-2) +
    '-' +
    ('0' + this.getDate()).slice(-2)
  );
};
