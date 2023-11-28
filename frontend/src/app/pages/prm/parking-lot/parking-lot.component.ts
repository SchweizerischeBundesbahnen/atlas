import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { BasePrmComponentService } from '../base-prm-component.service';
import { PrmTab } from '../prm-panel/prm-tab';
import { Tab } from '../../tab';

@Component({
  selector: 'app-parking-lot',
  templateUrl: './parking-lot.component.html',
})
export class ParkingLotComponent extends BasePrmComponentService implements OnInit {
  constructor(
    readonly router: Router,
    private route: ActivatedRoute,
  ) {
    super(router);
  }

  ngOnInit(): void {
    this.showCurrentTab(this.route.parent!.snapshot.data);
  }

  getTag(): Tab {
    return PrmTab.PARKING_LOT;
  }
}
