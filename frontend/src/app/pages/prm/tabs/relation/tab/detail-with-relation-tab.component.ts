import { Component, OnChanges, OnInit, SimpleChanges, input } from '@angular/core';
import { Tab } from '../../../../tab';
import { MatTabLink, MatTabNav, MatTabNavPanel } from '@angular/material/tabs';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';

export const PRM_DETAIL_TAB_LINK = 'detail';
export const PRM_RELATIONS_TAB_LINK = 'relations';

@Component({
  selector: 'atlas-prm-detail-with-relation-tab',
  templateUrl: './detail-with-relation-tab.component.html',
  styleUrls: ['./detail-with-relation-tab.component.scss'],
  imports: [MatTabNav, MatTabLink, RouterLinkActive, RouterLink, MatTabNavPanel, RouterOutlet, TranslatePipe],
})
export class DetailWithRelationTabComponent implements OnInit, OnChanges {
  readonly isNew = input(false);
  readonly isReduced = input(false);
  readonly detailTitle = input.required<string>();

  showTabs = false;

  tabs!: Tab[];

  ngOnInit(): void {
    this.tabs = [
      {
        link: PRM_DETAIL_TAB_LINK,
        title: this.detailTitle(),
      },
      {
        link: PRM_RELATIONS_TAB_LINK,
        title: 'PRM.TABS.RELATION',
      },
    ];

    this.calculateShowTabs();
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes.isNew) {
      this.calculateShowTabs();
    }
  }

  private calculateShowTabs() {
    this.showTabs = !this.isReduced() && !this.isNew();
  }
}
