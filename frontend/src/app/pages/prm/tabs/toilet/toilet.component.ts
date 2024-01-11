import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { BasePrmTabComponentService } from '../base-prm-tab-component.service';
import { PrmTabs } from '../../prm-panel/prm-tabs';
import { Tab } from '../../../tab';

@Component({
  selector: 'app-toilet',
  templateUrl: './toilet.component.html',
})
export class ToiletComponent extends BasePrmTabComponentService implements OnInit {
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
    return PrmTabs.TOILET;
  }
}
