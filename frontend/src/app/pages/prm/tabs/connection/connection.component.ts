import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { BasePrmTabComponentService } from '../base-prm-tab-component.service';
import { PrmTabs } from '../../prm-panel/prm-tabs';
import { Tab } from '../../../tab';

@Component({
  selector: 'app-connection',
  templateUrl: './connection.component.html',
})
export class ConnectionComponent extends BasePrmTabComponentService implements OnInit {
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
    return PrmTabs.CONNECTION;
  }
}
