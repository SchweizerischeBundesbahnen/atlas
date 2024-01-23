import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { BasePrmTabComponentService } from '../base-prm-tab-component.service';
import { PrmTabs } from '../../prm-panel/prm-tabs';
import { Tab } from '../../../tab';

@Component({
  selector: 'app-parking-lot',
  templateUrl: './parking-lot.component.html',
})
export class ParkingLotComponent extends BasePrmTabComponentService implements OnInit {
  constructor(
    readonly router: Router,
    private route: ActivatedRoute,
  ) {
    super(router);
  }

  ngOnInit(): void {
    this.showCurrentTab(this.route.parent!.snapshot.data);
  }

  getTab(): Tab {
    return PrmTabs.PARKING_LOT;
  }
}
