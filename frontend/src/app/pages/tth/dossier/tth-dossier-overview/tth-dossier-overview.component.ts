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
import { TranslatePipe, TranslateService } from '@ngx-translate/core';
import { PermissionService, TthApplicationUserType } from '../../../../core/auth/permission/permission.service';
import { UserService } from '../../../../core/auth/user/user.service';
import { DossierStatus } from '../../../../api/model/dossierStatus';
import { AtlasButtonComponent } from '../../../../core/components/button/atlas-button.component';
import { DownloadIconComponent } from '../../../../core/form-components/download-icon/download-icon.component';
import { FileDownloadService } from '../../../../core/components/file-upload/file/file-download.service';
import { mapToLanguageModel } from '../../../../api/mapping/language';
import { Language } from '../../../../api/model/language';

@Component({
  selector: 'atlas-tth-dossier-overview',
  imports: [
    TableComponent,
    TthDossierOverviewMenuComponent,
    TranslatePipe,
    AtlasButtonComponent,
    DownloadIconComponent,
  ],
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
  private readonly translateService = inject(TranslateService);

  readonly cantonShort = this.overviewToTabService.cantonShort;
  readonly timetableYear = this.overviewToTabService.timetableYear;
  readonly hearingStatus = this.overviewToTabService.hearingStatus;
  readonly isTimetableHearingYearFound = this.overviewToTabService.isTimetableHearingYearFound;
  readonly isHearingYearActive = this.overviewToTabService.isHearingYearActive;
  readonly isSwissCanton = this.overviewToTabService.isSwissCanton;
  readonly isYearLoading = this.overviewToTabService.isYearLoading;

  tthDossiers: TthDossier[] = [];
  totalCount = 0;
  tableColumns: TableColumn<TthDossier>[] = [];
  tableFilterConfig!: TableFilter<unknown>[][];

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
    this.tableColumns = this.getTableColumns();
    const filterSettings = TthTableFilterSettingsService.createDossierSettings();
    if (this.userType === 'BO_TTH') {
      filterSettings.multiSelectDossierStatus.activeSearch = [
        DossierStatus.DossierBoCheck,
        DossierStatus.DossierCantonCheck,
        DossierStatus.Accepted,
        DossierStatus.Rejected,
        DossierStatus.Moved,
      ];
    }

    this.tableFilterConfig = this.tableService.initializeFilterConfig(filterSettings, Pages.TTH_DOSSIERS);

    this.initOverviewTable();
  }

  getOverview(pagination: TablePagination) {
    this.dossierInternalService
      .getOverview(
        this.timetableYear().timetableYear,
        this.hearingStatus(),
        Cantons.getSwissCantonFromShort(this.cantonShort()),
        this.userType === 'BO_TTH' ? this.userService.currentUser!.sbbuid : undefined,
        this.tableService.filter.chipSearch.getActiveSearch(),
        this.tableService.filter.multiSelectDossierStatus.getActiveSearch(),
        pagination.page,
        pagination.size,
        addElementsToArrayWhenNotUndefined(pagination.sort, 'id,ASC')
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
        headerTitle: 'TTH.DOSSIER.DEADLINE',
        value: 'boDeadlineToAnswer',
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

  downloadCsv() {
    this.dossierInternalService
      .getDossiersAsCsv(
        mapToLanguageModel(this.translateService.getCurrentLang()) ?? Language.De,
        this.timetableYear().timetableYear,
        this.hearingStatus(),
        Cantons.getSwissCantonFromShort(this.cantonShort()),
        this.userType === 'BO_TTH' ? this.userService.currentUser!.sbbuid : undefined,
        this.tableService.filter.chipSearch.getActiveSearch(),
        this.tableService.filter.multiSelectDossierStatus.getActiveSearch()
      )
      .subscribe((response) => FileDownloadService.downloadFile('dossiers.csv', response));
  }
}
