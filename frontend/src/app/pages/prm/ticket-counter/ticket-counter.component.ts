import { Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { BasePrmComponentService } from '../base-prm-component.service';

@Component({
  selector: 'app-ticket-counter',
  templateUrl: './ticket-counter.component.html',
  styleUrls: ['./ticket-counter.component.scss'],
})
export class TicketCounterComponent extends BasePrmComponentService {
  constructor(
    readonly router: Router,
    private route: ActivatedRoute,
  ) {
    super(router);
  }

  ngOnInit(): void {}
}
