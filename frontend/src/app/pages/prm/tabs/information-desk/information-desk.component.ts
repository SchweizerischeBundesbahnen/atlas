import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { BasePrmTabComponentService } from '../base-prm-tab-component.service';
import { PrmTab } from '../../prm-panel/prm-tab';
import { Tab } from '../../../tab';

@Component({
  selector: 'app-information-desk',
  templateUrl: './information-desk.component.html',
})
export class InformationDeskComponent extends BasePrmTabComponentService implements OnInit {
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
    return PrmTab.INFORMATION_DESK;
  }
}
