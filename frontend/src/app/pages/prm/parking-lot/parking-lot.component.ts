import { Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { BasePrmComponentService } from '../base-prm-component.service';

@Component({
  selector: 'app-parking-lot',
  templateUrl: './parking-lot.component.html',
  styleUrls: ['./parking-lot.component.scss'],
})
export class ParkingLotComponent extends BasePrmComponentService {
  constructor(
    readonly router: Router,
    private route: ActivatedRoute,
  ) {
    super(router);
  }

  ngOnInit(): void {
    this.checkStopPointExists(this.route.parent?.snapshot?.data!);
  }
}
