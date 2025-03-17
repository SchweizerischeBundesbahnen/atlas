import {Component, Input, OnChanges, OnInit, SimpleChanges} from '@angular/core';
import {Tab} from "../../../../tab";

export const PRM_DETAIL_TAB_LINK = 'detail';
export const PRM_RELATIONS_TAB_LINK = 'relations';

@Component({
    selector: 'prm-detail-with-relation-tab',
    templateUrl: './detail-with-relation-tab.component.html',
    styleUrls: ['./detail-with-relation-tab.component.scss'],
    standalone: false
})
export class DetailWithRelationTabComponent implements OnInit, OnChanges {

  @Input() isNew = false;
  @Input() isReduced = false;
  @Input() detailTitle!: string;

  showTabs = false;

  tabs !: Tab[];

  ngOnInit(): void {
    this.tabs = [
      {
        link: PRM_DETAIL_TAB_LINK,
        title: this.detailTitle,
      },
      {
        link: PRM_RELATIONS_TAB_LINK,
        title: 'PRM.TABS.RELATION',
      }
    ];

    this.calculateShowTabs();
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes.isNew) {
      this.calculateShowTabs();
    }
  }

  private calculateShowTabs() {
    this.showTabs = !this.isReduced && !this.isNew;
  }

}
