import { Component } from '@angular/core';
import { LoadingSpinnerService } from './core/components/loading-spinner/loading-spinner.service';
import { animate, style, transition, trigger } from '@angular/animations';
import { ServiceWorkerService } from './service-worker.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss'],
  animations: [
    trigger('fadeInOut', [
      transition('true <=> false', [style({ opacity: 0 }), animate(650, style({ opacity: 1 }))]),
    ]),
  ],
  providers: [ServiceWorkerService],
})
export class AppComponent {
  constructor(
    public loadingSpinnerService: LoadingSpinnerService,
    private readonly _swService: ServiceWorkerService
  ) {
    loadingSpinnerService.initLoadingSpinner();
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
