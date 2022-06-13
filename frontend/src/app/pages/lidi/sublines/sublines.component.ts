import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';

import { TableColumn } from '../../../core/components/table/table-column';
import { ActivatedRoute, Router } from '@angular/router';
import { Subscription } from 'rxjs';
import { NotificationService } from '../../../core/notification/notification.service';
import { Subline, SublinesService, SublineType } from '../../../api';
import { TableComponent } from '../../../core/components/table/table.component';
import { TableSettings } from '../../../core/components/table/table-settings';
import { Pages } from '../../pages';
import { TableSettingsService } from '../../../core/components/table/table-settings.service';
import {
  DetailDialogEvents,
  RouteToDialogService,
} from '../../../core/components/route-to-dialog/route-to-dialog.service';
import { filter } from 'rxjs/operators';

@Component({
  selector: 'app-lidi-sublines',
  templateUrl: './sublines.component.html',
})
export class SublinesComponent implements OnInit, OnDestroy {
  @ViewChild(TableComponent, { static: true }) tableComponent!: TableComponent<Subline>;

  sublinesTableColumns: TableColumn<Subline>[] = [
    { headerTitle: 'LIDI.SUBLINE.NUMBER', value: 'number' },
    { headerTitle: 'LIDI.SUBLINE.DESCRIPTION', value: 'description' },
    { headerTitle: 'LIDI.SWISS_SUBLINE_NUMBER', value: 'swissSublineNumber' },
    { headerTitle: 'LIDI.SUBLINE.OVERVIEW_MAINLINE', value: 'swissLineNumber' },
    {
      headerTitle: 'LIDI.SUBLINE_TYPE',
      value: 'sublineType',
      translate: { withPrefix: 'LIDI.SUBLINE.TYPES.' },
    },
    { headerTitle: 'LIDI.OVERVIEW_BUSINESS_ORGANISATION', value: 'businessOrganisation' },
    { headerTitle: 'LIDI.SLNID', value: 'slnid' },
    {
      headerTitle: 'COMMON.STATUS',
      value: 'status',
      translate: { withPrefix: 'COMMON.STATUS_TYPES.' },
    },
    { headerTitle: 'COMMON.VALID_FROM', value: 'validFrom', formatAsDate: true },
    { headerTitle: 'COMMON.VALID_TO', value: 'validTo', formatAsDate: true },
  ];

  readonly SUBLINE_TYPES: SublineType[] = Object.values(SublineType);
  activeSublineTypes: SublineType[] = [];
  sublines: Subline[] = [];
  totalCount$ = 0;
  isLoading = false;
  private sublineVersionsSubscription!: Subscription;
  private routeSubscription!: Subscription;

  constructor(
    private sublinesService: SublinesService,
    private route: ActivatedRoute,
    private router: Router,
    private notificationService: NotificationService,
    private tableSettingsService: TableSettingsService,
    private routeToDialogService: RouteToDialogService
  ) {
    this.routeSubscription = this.routeToDialogService.detailDialogEvent
      .pipe(filter((e) => e === DetailDialogEvents.Closed))
      .subscribe(() => this.ngOnInit());
  }

  ngOnInit(): void {
    const storedTableSettings = this.tableSettingsService.getTableSettings(Pages.SUBLINES.path);
    this.getOverview(storedTableSettings || { page: 0, size: 10, sort: 'number,ASC' });
  }

  getOverview($paginationAndSearch: TableSettings) {
    this.tableSettingsService.storeTableSettings(Pages.SUBLINES.path, $paginationAndSearch);
    this.isLoading = true;
    this.sublineVersionsSubscription = this.sublinesService
      .getSublines(
        $paginationAndSearch.searchCriteria,
        $paginationAndSearch.statusChoices,
        $paginationAndSearch.sublineTypes,
        $paginationAndSearch.validOn,
        $paginationAndSearch.page,
        $paginationAndSearch.size,
        [$paginationAndSearch.sort!, 'slnid,ASC']
      )
      .subscribe((sublineContainer) => {
        this.sublines = sublineContainer.objects!;
        this.totalCount$ = sublineContainer.totalCount!;
        this.tableComponent.setTableSettings($paginationAndSearch);
        this.activeSublineTypes = $paginationAndSearch.sublineTypes;
        this.isLoading = false;
      });
  }

  onSublineTypeSelectionChange(): void {
    this.tableComponent.searchData({
      ...this.tableComponent.tableSearchComponent.activeSearch,
      sublineTypes: this.activeSublineTypes,
    });
  }

  editVersion($event: Subline) {
    this.router
      .navigate([$event.slnid], {
        relativeTo: this.route,
      })
      .then();
  }

  ngOnDestroy() {
    this.sublineVersionsSubscription.unsubscribe();
    this.routeSubscription.unsubscribe();
  }
}
