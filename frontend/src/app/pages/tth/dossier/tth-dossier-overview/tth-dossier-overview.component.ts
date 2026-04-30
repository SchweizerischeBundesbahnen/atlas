import { Component, effect, inject } from '@angular/core';
import { catchError, EMPTY } from 'rxjs';
import { DossierInternalService } from '../../../../api/service/workflow/dossier-internal.service';
import { TableComponent } from '../../../../core/components/table/table.component';
import { TableColumn } from '../../../../core/components/table/table-column';
import { TableFilter } from '../../../../core/components/table-filter/config/table-filter';
import { TthDossier } from '../../../../api/model/tthDossier';
import { SwissCanton } from '../../../../api';
import { Cantons } from '../../../../core/cantons/Cantons';
import { ActivatedRoute, Router } from '@angular/router';
import { TthTableFilterSettingsService } from '../../tth-table-filter-settings.service';
import { Pages } from '../../../pages';
import { TableService } from '../../../../core/components/table/table.service';
import { TablePagination } from '../../../../core/components/table/table-pagination';
import { OverviewToTabShareDataService } from '../../overview-tab/service/overview-to-tab-share-data.service';
import { TthDossierOverviewMenuComponent } from '../tth-dossier-overview-menu/tth-dossier-overview-menu.component';
import { addElementsToArrayWhenNotUndefined } from '../../../../core/util/arrays';
import { TranslatePipe } from '@ngx-translate/core';
import { DossierStatus } from '../../../../api/model/dossierStatus';
import { PermissionService, TthApplicationUserType } from '../../../../core/auth/permission/permission.service';
import { UserService } from '../../../../core/auth/user/user.service';

@Component({
  selector: 'atlas-tth-dossier-overview',
  imports: [TableComponent, TthDossierOverviewMenuComponent, TranslatePipe],
  templateUrl: './tth-dossier-overview.component.html',
  providers: [TableService],
})
export class TthDossierOverviewComponent {
  private readonly dossierInternalService = inject(DossierInternalService);
  private readonly tableService = inject(TableService);
  private readonly router = inject(Router);
  private readonly route = inject(ActivatedRoute);
  private readonly overviewToTabService = inject(OverviewToTabShareDataService);
  private readonly permissionService = inject(PermissionService);
  private readonly userService = inject(UserService);

  readonly cantonShort = this.overviewToTabService.cantonShort;
  readonly timetableYear = this.overviewToTabService.timetableYear;
  readonly hearingStatus = this.overviewToTabService.hearingStatus;
  readonly isTimetableHearingYearFound = this.overviewToTabService.isTimetableHearingYearFound;
  readonly isHearingYearActive = this.overviewToTabService.isHearingYearActive;
  readonly isHearingYearArchived = this.overviewToTabService.isHearingYearArchived;
  readonly isSwissCanton = this.overviewToTabService.isSwissCanton;
  readonly isYearLoading = this.overviewToTabService.isYearLoading;

  tthDossiers: TthDossier[] = [];
  totalCount = 0;
  tableColumns: TableColumn<TthDossier>[] = [];
  tableFilterConfig!: TableFilter<unknown>[][];

  sorting = 'topic,asc';
  userType!: TthApplicationUserType;

  constructor() {
    this.userType = this.permissionService.getTthApplicationUserType();

    effect(() => {
      if (!this.isYearLoading()) {
        this.loadData();
      }
    });
  }

  loadData() {
    if (this.isHearingYearActive()) {
      this.tableColumns = this.getTableColumns();
      const filterSettings =
        this.userType === 'BO_TTH'
          ? TthTableFilterSettingsService.createDossierSettingsForBo()
          : TthTableFilterSettingsService.createDossierSettings();

      this.tableFilterConfig = this.tableService.initializeFilterConfig(filterSettings, Pages.TTH_DOSSIERS);

      this.initOverviewTable();
    }

    if (this.isHearingYearArchived()) {
      this.tableColumns = this.getTableColumns();
      this.tableFilterConfig = this.tableService.initializeFilterConfig(
        TthTableFilterSettingsService.createDossierSettings(),
        Pages.TTH_DOSSIERS
      );
      this.initOverviewTable();
    }
  }

  getOverview(pagination: TablePagination) {
    if (this.userType === 'BO_TTH') {
      this.fetchOverview(this.userService.currentUser!.sbbuid, [DossierStatus.DossierBoCheck], pagination);
    } else {
      this.fetchOverview(undefined, this.tableService.filter.multiSelectDossierStatus.getActiveSearch(), pagination);
    }
  }

  private fetchOverview(sbbuid: string | undefined, dossierStatus: DossierStatus[], pagination: TablePagination) {
    this.dossierInternalService
      .getOverview(
        this.timetableYear().timetableYear,
        Cantons.getSwissCantonFromShort(this.cantonShort()),
        sbbuid,
        this.tableService.filter.chipSearch.getActiveSearch(),
        dossierStatus,
        pagination.page,
        pagination.size,
        addElementsToArrayWhenNotUndefined(pagination.sort, this.sorting, 'id,DESC')
      )
      .pipe(catchError(this.handleError()))
      .subscribe((container) => {
        this.tthDossiers = container.objects!;
        this.totalCount = container.totalCount!;
      });
  }

  private handleError() {
    return () => {
      return EMPTY;
    };
  }

  editDossier(id: number) {
    this.router
      .navigate([id], {
        relativeTo: this.route,
      })
      .then();
  }

  mapToShortCanton(canton: SwissCanton) {
    return Cantons.fromSwissCanton(canton)?.short;
  }

  private getTableColumns(): TableColumn<TthDossier>[] {
    return [
      { headerTitle: 'ID', value: 'id' },
      {
        headerTitle: 'TTH.STATEMENT_STATUS_HEADER',
        value: 'dossierStatus',
        translate: {
          withPrefix: 'TTH.DOSSIER.DOSSIER_STATUS.',
        },
      },
      {
        headerTitle: 'TTH.SWISS_CANTON',
        value: 'swissCanton',
        callback: this.mapToShortCanton,
      },
      {
        headerTitle: 'TTH.DOSSIER.TOPIC',
        value: 'topic',
      },
      {
        headerTitle: 'COMMON.EDIT_ON',
        value: 'editionDate',
        formatAsDate: true,
      },
      {
        headerTitle: '',
        value: 'editor',
        disabled: false,
        customCell: true,
      },
    ];
  }

  initOverviewTable() {
    this.getOverview({
      page: this.tableService.pageIndex,
      size: this.tableService.pageSize,
      sort: this.tableService.sortString,
    });
  }
}
