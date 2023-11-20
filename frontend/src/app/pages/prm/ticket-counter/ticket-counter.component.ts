import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { Pages } from '../../pages';

@Component({
  selector: 'app-ticket-counter',
  templateUrl: './ticket-counter.component.html',
  styleUrls: ['./ticket-counter.component.scss'],
})
export class TicketCounterComponent {
  constructor(private readonly router: Router) {}

  closeSidePanel() {
    this.router.navigate([Pages.PRM.path]).then();
  }
}
