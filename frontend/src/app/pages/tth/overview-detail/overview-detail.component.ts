import { Component, DestroyRef, effect, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import {
  ApplicationType,
  HearingStatus,
  StatementStatus,
  SwissCanton,
  TimetableHearingStatementDocument,
  TimetableHearingStatementSenderV2,
  TimetableHearingStatementV2,
  TransportCompany,
} from '../../../api';
import { Cantons } from '../../../core/cantons/Cantons';
import { TableColumn } from '../../../core/components/table/table-column';
import { Pages } from '../../pages';
import { take } from 'rxjs';
import { OverviewToTabShareDataService } from '../overview-tab/service/overview-to-tab-share-data.service';
import { MatSelectChange } from '@angular/material/select';
import { TthUtils } from '../util/tth-utils';
import { TablePagination } from '../../../core/components/table/table-pagination';
import { ColumnDropDownEvent } from '../../../core/components/table/column-drop-down-event';
import { addElementsToArrayWhenNotUndefined } from '../../../core/util/arrays';
import { SelectionModel } from '@angular/cdk/collections';
import { TranslatePipe, TranslateService } from '@ngx-translate/core';
import { FileDownloadService } from '../../../core/components/file-upload/file/file-download.service';
import { MatDialog } from '@angular/material/dialog';
import { DialogManageTthComponent } from '../dialog-manage-tth/dialog-manage-tth.component';
import { DialogService } from '../../../core/components/dialog/dialog.service';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { TableService } from '../../../core/components/table/table.service';
import { TableFilter } from '../../../core/components/table-filter/config/table-filter';
import { TthTableFilterSettingsService } from '../tth-table-filter-settings.service';
import { PermissionService } from '../../../core/auth/permission/permission.service';
import { TimetableHearingStatementInternalService } from '../../../api/service/lidi/timetable-hearing-statement-internal.service';
import { TimetableHearingYearInternalService } from '../../../api/service/lidi/timetable-hearing-year-internal.service';
import { NgOptimizedImage } from '@angular/common';
import { SelectComponent } from '../../../core/form-components/select/select.component';
import { AtlasSpacerComponent } from '../../../core/components/spacer/atlas-spacer.component';
import { AtlasButtonComponent } from '../../../core/components/button/atlas-button.component';
import { DownloadIconComponent } from '../../../core/form-components/download-icon/download-icon.component';
import { TableComponent } from '../../../core/components/table/table.component';
import { DisplayDatePipe } from '../../../core/pipe/display-date.pipe';
import { DialogData } from '../../../core/components/dialog/dialog.data';
import { TthExportAnonymizationChoiceDialogComponent } from './tth-export-anonymization-choice-dialog/tth-export-anonymization-choice-dialog.component';
import { StatementOverviewMenuComponent } from './statement-overview-menu/statement-overview-menu.component';
import { TableFilterBoolean } from '../../../core/components/table-filter/config/table-filter-boolean';
import { TthYearInternalService } from '../../../api/service/workflow/tth-year-internal.service';
import { ChangeCantonData } from './tth-change-canton-dialog/model/change-canton-data';
import { TthChangeCantonDialogComponent } from './tth-change-canton-dialog/tth-change-canton-dialog.component';
import { StatusChangeData } from './tth-change-status-dialog/model/status-change-data';
import { TthChangeStatusDialogComponent } from './tth-change-status-dialog/tth-change-status-dialog.component';
import { NewTimetableHearingYearDialogComponent } from '../new-timetable-hearing-year-dialog/new-timetable-hearing-year-dialog.component';

@Component({
  selector: 'atlas-timetable-hearing-overview-detail',
  templateUrl: './overview-detail.component.html',
  styleUrls: ['./overview-detail.component.scss'],
  imports: [
    SelectComponent,
    AtlasSpacerComponent,
    AtlasButtonComponent,
    DownloadIconComponent,
    TableComponent,
    DisplayDatePipe,
    TranslatePipe,
    NgOptimizedImage,
    StatementOverviewMenuComponent,
  ],
})
export class OverviewDetailComponent {
  readonly cantonShort = this.overviewToTabService.cantonShort;
  readonly timetableYear = this.overviewToTabService.timetableYear;
  readonly hearingStatus = this.overviewToTabService.hearingStatus;
  readonly isTimetableHearingYearFound =
    this.overviewToTabService.isTimetableHearingYearFound;
  readonly isHearingYearActive = this.overviewToTabService.isHearingYearActive;
  readonly isHearingYearPlanned =
    this.overviewToTabService.isHearingYearPlanned;
  readonly isHearingYearArchived =
    this.overviewToTabService.isHearingYearArchived;
  readonly isSwissCanton = this.overviewToTabService.isSwissCanton;
  readonly isYearLoading = this.overviewToTabService.isYearLoading;

  timeTableHearingStatements: TimetableHearingStatementV2[] = [];
  totalCount = 0;
  tableColumns: TableColumn<TimetableHearingStatementV2>[] = [];

  CANTON_DROPDOWN_OPTIONS_WITHOUT_SWISS = Cantons.cantons.map(
    (value) => value.short
  );

  STATUS_OPTIONS = Object.values(StatementStatus);

  COLLECTING_ACTION_DROPDOWN_OPTIONS = ['STATUS_CHANGE', 'CANTON_DELIVERY'];

  showDownloadCsvButton = false;
  showManageTimetableHearingButton = false;
  showAddNewStatementButton = false;
  showAddNewTimetableHearingButton = false;
  showStartTimetableHearingButton = false;
  showHearingDetail = false;

  showCollectingActionButton = true;
  statusChangeCollectingActionsEnabled = false;
  cantonDeliveryCollectingActionsEnabled = false;

  isTableColumnsInitialized = false;

  selectedItems: TimetableHearingStatementV2[] = [];
  sorting = 'statementStatus,asc';
  selectedCheckBox = new SelectionModel<TimetableHearingStatementV2>(true, []);
  isCheckBoxModeActive = false;
  private destroyRef = inject(DestroyRef);

  tableFilterConfig!: TableFilter<unknown>[][];

  constructor(
    private readonly route: ActivatedRoute,
    private readonly router: Router,
    private readonly timetableHearingStatementsService: TimetableHearingStatementInternalService,
    private readonly timetableHearingYearsService: TimetableHearingYearInternalService,
    private readonly tthYearInternalService: TthYearInternalService,
    private readonly overviewToTabService: OverviewToTabShareDataService,
    private readonly dialogService: DialogService,
    private readonly tableService: TableService,
    private readonly translateService: TranslateService,
    private readonly permissionService: PermissionService,
    private readonly matDialog: MatDialog
  ) {
    effect(() => {
      if (!this.isYearLoading()) {
        this.loadData();
      }
    });
  }

  get isCollectingActionEnabled(): boolean {
    return this.permissionService.hasWritePermissionsToForCanton(
      ApplicationType.TimetableHearing,
      this.cantonShort().toLowerCase()
    );
  }

  loadData() {
    const settings = TthTableFilterSettingsService.createSettings();

    if (this.isHearingYearActive()) {
      const activeSettings = this.getActiveSettings();
      this.tableFilterConfig = this.tableService.initializeFilterConfig(
        activeSettings,
        Pages.TTH_STATEMENTS
      );

      this.tableColumns = this.getActiveTableColumns();
      if (!this.isCollectingActionEnabled) {
        this.tableColumns = this.tableColumns.filter(
          (value) => value.value !== 'etagVersion'
        );
        this.disableChangeStatementStatusSelect();
      }
      this.enableCheckboxViewMode();
      this.showManageTimetableHearingButton = this.isSwissCanton();
      this.showAddNewStatementButton = !this.isSwissCanton();
      this.showDownloadCsvButton = true;
      this.initOverviewTable();
    }

    if (this.isHearingYearPlanned()) {
      this.removeCheckBoxViewMode();
      this.tableFilterConfig = this.tableService.initializeFilterConfig(
        settings,
        Pages.TTH_PLANNED
      );
      this.sorting = 'swissCanton,asc';
      this.tableColumns = this.getPlannedOrArchivedTableColumns();
      this.showAddNewTimetableHearingButton = true;
      this.showHearingDetail = true;
      this.initShowStartTimetableHearingButton();
      this.initOverviewTable();
    }

    if (this.isHearingYearArchived()) {
      this.removeCheckBoxViewMode();
      this.tableFilterConfig = this.tableService.initializeFilterConfig(
        settings,
        Pages.TTH_ARCHIVED
      );
      this.sorting = 'swissCanton,asc';
      this.tableColumns = this.getPlannedOrArchivedTableColumns();
      this.showDownloadCsvButton = true;
      this.initOverviewTable();
    }
  }

  getActiveSettings() {
    const settings = TthTableFilterSettingsService.createSettings();
    if (this.permissionService.isTthCanton()) {
      Object.assign(settings, this.tableFilterShowStatementInDossier);
    }
    return settings;
  }

  getOverview(pagination: TablePagination) {
    const selectedCantonEnum = this.getSelectedCantonToBeSearchFromNavigation();

    this.timetableHearingStatementsService
      .getStatements(
        this.timetableYear().timetableYear,
        selectedCantonEnum,
        this.tableService.filter.chipSearch.getActiveSearch(),
        this.tableService.filter.multiSelectStatementStatus.getActiveSearch(),
        this.tableService.filter.searchSelectTTFN.getActiveSearch()?.ttfnid,
        TthUtils.toTransportCompanyIds(
          this.tableService.filter.searchSelectTU.getActiveSearch() as TransportCompany[]
        ),
        this.getShowStatementInDossierFilter(),
        pagination.page,
        pagination.size,
        addElementsToArrayWhenNotUndefined(
          pagination.sort,
          this.sorting,
          'id,ASC'
        )
      )
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe((container) => {
        this.timeTableHearingStatements = container.objects!;
        this.totalCount = container.totalCount!;
        this.isTableColumnsInitialized = true;
      });
  }

  getShowStatementInDossierFilter() {
    if (this.isHearingYearActive()) {
      if (
        this.tableService.filter.showStatementInDossierFilter.getActiveSearch()
      ) {
        return undefined;
      }
      return this.tableService.filter.showStatementInDossierFilter.getActiveSearch();
    }
  }

  editStatement(statement: TimetableHearingStatementV2) {
    this.router
      .navigate([statement.id], {
        relativeTo: this.route,
      })
      .then();
  }

  downloadCsv(anonymizedExport: boolean) {
    this.timetableHearingStatementsService
      .getStatementsAsCsv(
        this.translateService.currentLang,
        this.timetableYear().timetableYear,
        this.getSelectedCantonToBeSearchFromNavigation(),
        this.tableService.filter.chipSearch.getActiveSearch(),
        this.tableService.filter.multiSelectStatementStatus.getActiveSearch(),
        this.tableService.filter.searchSelectTTFN.getActiveSearch()?.ttfnid,
        (
          this.tableService.filter.searchSelectTU.getActiveSearch() as TransportCompany[]
        )
          ?.map((tu) => tu.id)
          .filter(
            (numberOrUndefined): numberOrUndefined is number =>
              !!numberOrUndefined
          ),
        this.getShowStatementInDossierFilter(),
        anonymizedExport
      )
      .subscribe((response) =>
        FileDownloadService.downloadFile('statements.csv', response)
      );
  }

  openTthExportAnonymizationChoiceDialog(): void {
    const data: DialogData = {
      title: 'TTH.DIALOG.EXPORT_ANONYMIZATION_CHOICE_TITLE',
      message: 'TTH.DIALOG.EXPORT_ANONYMIZATION_CHOICE_MESSAGE',
      cancelText: 'TTH.DIALOG.CANCEL',
      confirmText: 'TTH.DIALOG.CONFIRM',
    };

    this.matDialog
      .open(TthExportAnonymizationChoiceDialogComponent, {
        data: data,
        disableClose: true,
        panelClass: 'atlas-dialog-panel',
        backdropClass: 'atlas-dialog-backdrop',
      })
      .afterClosed()
      .pipe()
      .subscribe((result: { isAnonymized: boolean }) => {
        if (result != null) {
          this.downloadCsv(result.isAnonymized);
        }
      });
  }

  manageTimetableHearing() {
    this.matDialog
      .open<DialogManageTthComponent, number, boolean>(
        DialogManageTthComponent,
        {
          data: this.timetableYear().timetableYear,
          disableClose: true,
          panelClass: 'atlas-dialog-panel',
          backdropClass: 'atlas-dialog-backdrop',
        }
      )
      .afterClosed()
      .pipe(take(1))
      .subscribe({
        next: (needsInit) => {
          if (needsInit) {
            this.loadData();
            this.router
              .navigate(
                ['..', Pages.TTH_ACTIVE.path, Pages.TTH_STATEMENTS.path],
                {
                  relativeTo: this.route,
                }
              )
              .then();
          }
        },
      });
  }

  addNewStatement() {
    this.router
      .navigate(['add'], {
        relativeTo: this.route,
        state: { data: this.cantonShort() },
      })
      .then();
  }

  addNewTimetableHearing() {
    this.dialogService
      .openCustomDataWithConfirmationResult(
        {},
        NewTimetableHearingYearDialogComponent,
        {
          width: '40%',
        }
      )
      .subscribe((result) => {
        if (result) {
          this.overviewToTabService.setTimetableHearingYearFound(false);
          this.loadData();
          this.router
            .navigate(['..'], {
              relativeTo: this.route,
            })
            .then();
        }
      });
  }

  startTimetableHearing() {
    this.dialogService
      .openDialogDataWithConfirmationResult({
        title: 'TTH.START_HEARING_DIALOG.TITLE',
        message: 'TTH.START_HEARING_DIALOG.TEXT',
        confirmText: 'TTH.START_HEARING_DIALOG.CONFIRM',
      })
      .subscribe((confirmed) => {
        if (confirmed) {
          this.tthYearInternalService
            .startTimetableHearingYear(this.timetableYear().timetableYear)
            .subscribe(() => {
              this.router
                .navigate(
                  ['..', Pages.TTH_ACTIVE.path, Pages.TTH_STATEMENTS.path],
                  {
                    relativeTo: this.route.parent,
                  }
                )
                .then();
            });
        }
      });
  }

  collectingActions(action: MatSelectChange) {
    if (action.value === 'STATUS_CHANGE') {
      this.statusChangeCollectingActionsEnabled = true;
      this.showCollectingActionButton = false;
      this.loadData();
    }
    if (action.value === 'CANTON_DELIVERY') {
      this.cantonDeliveryCollectingActionsEnabled = true;
      this.showCollectingActionButton = false;
      this.loadData();
    }
  }

  changeSelectedStatus(changedStatus: ColumnDropDownEvent) {
    const dialogData: StatusChangeData = {
      title: 'TTH.DIALOG.STATUS_CHANGE',
      message: 'TTH.DIALOG.STATUS_CHANGE_MESSAGE',
      cancelText: 'TTH.DIALOG.BACK',
      confirmText: 'TTH.DIALOG.STATUS_CHANGE',
      tths: [changedStatus.value],
      statementStatus: changedStatus.$event.value,
      publicComment: changedStatus.value.justification,
      type: 'SINGLE',
    };

    this.dialogService
      .openDialogDataWithConfirmationResult(
        dialogData,
        TthChangeStatusDialogComponent
      )
      .subscribe(() => {
        this.loadData();
      });
  }

  cancelCollectingAction() {
    this.removeCheckBoxViewMode();
    this.loadData();
  }

  collectingStatusChangeAction(changedStatus: ColumnDropDownEvent) {
    if (this.selectedItems.length > 0) {
      const dialogData: StatusChangeData = {
        title: 'TTH.DIALOG.STATUS_CHANGE',
        message: 'TTH.DIALOG.MULTIPLE_STATUS_CHANGE_MESSAGE',
        cancelText: 'TTH.DIALOG.BACK',
        confirmText: 'TTH.DIALOG.STATUS_CHANGE',
        tths: this.selectedItems,
        statementStatus: changedStatus.value,
        type: 'MULTIPLE',
      };

      this.dialogService
        .openDialogDataWithConfirmationResult(
          dialogData,
          TthChangeStatusDialogComponent
        )
        .subscribe((result) => {
          if (result) {
            this.statusChangeCollectingActionsEnabled = false;
            this.showCollectingActionButton = true;
            this.selectedCheckBox =
              new SelectionModel<TimetableHearingStatementV2>(true, []);
            this.removeCheckBoxViewMode();
            this.loadData();
          }
        });
    }
  }

  collectingCantonDeliveryAction($event: MatSelectChange) {
    if (this.selectedItems.length > 0) {
      const dialogData: ChangeCantonData = {
        title: 'TTH.STATEMENT.DIALOG.TITLE',
        message: 'TTH.DIALOG.MULTIPLE_STATUS_CHANGE_MESSAGE',
        cancelText: 'TTH.DIALOG.BACK',
        confirmText: 'TTH.DIALOG.CANTON_CHANGE',
        tths: this.selectedItems,
        swissCanton: Cantons.getSwissCantonEnum($event.value),
      };
      this.dialogService
        .openDialogDataWithConfirmationResult(
          dialogData,
          TthChangeCantonDialogComponent
        )
        .subscribe((result) => {
          if (result) {
            this.cantonDeliveryCollectingActionsEnabled = false;
            this.showCollectingActionButton = true;
            this.selectedCheckBox =
              new SelectionModel<TimetableHearingStatementV2>(true, []);
            this.removeCheckBoxViewMode();
            this.loadData();
          }
        });
    }
  }

  checkedBoxEvent($event: SelectionModel<TimetableHearingStatementV2>) {
    this.selectedItems = $event.selected;
  }

  private removeCheckBoxViewMode() {
    this.isCheckBoxModeActive = false;
    this.showCollectingActionButton = true;
    this.statusChangeCollectingActionsEnabled = false;
    this.cantonDeliveryCollectingActionsEnabled = false;
    this.selectedCheckBox = new SelectionModel<TimetableHearingStatementV2>(
      true,
      []
    );
    this.selectedItems = [];
    this.tableService.filterConfig?.enableFilters();
  }

  private enableCheckboxViewMode() {
    this.isCheckBoxModeActive =
      this.statusChangeCollectingActionsEnabled ||
      this.cantonDeliveryCollectingActionsEnabled;
    if (this.isCheckBoxModeActive) {
      this.tableColumns = this.getActiveTableColumns();
      this.tableColumns.unshift({
        headerTitle: '',
        disabled: true,
        value: 'id',
        columnDef: 'select',
        checkbox: {
          changeSelectionCallback: this.collectingStatusChangeAction,
        },
      });
      this.tableColumns.forEach((value) => (value.disabled = true));
      this.disableChangeStatementStatusSelect();
      this.disableMenuButton();
      this.tableService.filterConfig?.disableFilters();
    } else {
      this.removeCheckBoxViewMode();
    }
  }

  private disableChangeStatementStatusSelect() {
    const statementStatusTableColumn = this.tableColumns.filter(
      (value) => value.value === 'statementStatus'
    )[0];
    if (statementStatusTableColumn.dropdown) {
      statementStatusTableColumn.dropdown.disabled = true;
    }
  }

  private disableMenuButton() {
    const duplicateButtonAction = this.tableColumns.filter(
      (value) => value.value === 'etagVersion'
    )[0];
    duplicateButtonAction.disabled = true;
  }

  private initOverviewTable() {
    this.getOverview({
      page: this.tableService.pageIndex,
      size: this.tableService.pageSize,
      sort: this.tableService.sortString,
    });
  }

  private getSelectedCantonToBeSearchFromNavigation(): SwissCanton | undefined {
    if (this.cantonShort().toLowerCase() === Cantons.swiss.path) {
      // we return undefined to get all cantons from backend
      return undefined;
    } else {
      return Cantons.getSwissCantonEnum(this.cantonShort());
    }
  }

  mapToShortCanton(canton: SwissCanton) {
    return Cantons.fromSwissCanton(canton)?.short;
  }

  mapToLastname(statementSender: TimetableHearingStatementSenderV2) {
    return statementSender.lastName;
  }

  isDocumentExisting(documents: Array<TimetableHearingStatementDocument>) {
    return documents.length > 0;
  }

  private getTableColumns(): TableColumn<TimetableHearingStatementV2>[] {
    return [
      {
        headerTitle: 'TTH.STATEMENT_STATUS_HEADER',
        value: 'statementStatus',
        dropdown: {
          disabled: !this.timetableYear().statementEditable!,
          options: this.STATUS_OPTIONS,
          changeSelectionCallback: this.changeSelectedStatus,
          selectedOption: '',
          translate: {
            withPrefix: 'TTH.STATEMENT_STATUS.',
          },
        },
      },
      {
        headerTitle: 'TTH.SWISS_CANTON',
        value: 'swissCanton',
        callback: this.mapToShortCanton,
      },
      { headerTitle: 'ID', value: 'id' },
      {
        headerTitle: 'TTH.TIMETABLE_FIELD_LASTNAME',
        value: 'statementSender',
        callback: this.mapToLastname,
      },
      {
        headerTitle: 'TTH.TRANSPORT_COMPANY',
        value: 'responsibleTransportCompaniesDisplay',
      },
      {
        headerTitle: 'TTH.TIMETABLE_FIELD_NUMBER',
        value: 'timetableFieldNumber',
        disabled: true,
      },
      {
        headerTitle: 'TTH.TIMETABLE_FIELD_NUMBER_DESCRIPTION',
        value: 'timetableFieldDescription',
        disabled: true,
      },
      {
        headerTitle: 'COMMON.EDIT_ON',
        value: 'editionDate',
        formatAsDate: true,
      },
      {
        headerTitle: 'TTH.TIMETABLE_FIELD_DOCUMENT',
        value: 'documents',
        icon: {
          icon: 'bi bi-paperclip',
          callback: this.isDocumentExisting,
        },
      },
      {
        headerTitle: '',
        value: 'etagVersion',
        disabled: false,
        customCell: true,
      },
    ];
  }

  getActiveTableColumns(): TableColumn<TimetableHearingStatementV2>[] {
    if (this.isSwissCanton()) {
      return this.getTableColumns().filter((col) =>
        this.activeTthAndSwissCanton(col)
      );
    } else {
      return this.getTableColumns().filter((col) => this.activeTth(col));
    }
  }

  private activeTth(col: TableColumn<TimetableHearingStatementV2>): boolean {
    return (
      col.value === 'statementStatus' ||
      col.value === 'editionDate' ||
      col.value === 'etagVersion' ||
      this.plannedOrArchivedTth(col)
    );
  }

  private activeTthAndSwissCanton(
    col: TableColumn<TimetableHearingStatementV2>
  ): boolean {
    return col.value === 'swissCanton' || this.activeTth(col);
  }

  getPlannedOrArchivedTableColumns(): TableColumn<TimetableHearingStatementV2>[] {
    if (this.isSwissCanton()) {
      return this.getTableColumns().filter((col) =>
        this.plannedOrArchivedTthAndSwissCanton(col)
      );
    } else {
      return this.getTableColumns().filter((col) =>
        this.plannedOrArchivedTth(col)
      );
    }
  }

  private plannedOrArchivedTthAndSwissCanton(
    col: TableColumn<TimetableHearingStatementV2>
  ): boolean {
    return col.value === 'swissCanton' || this.plannedOrArchivedTth(col);
  }

  private plannedOrArchivedTth(
    col: TableColumn<TimetableHearingStatementV2>
  ): boolean {
    return (
      col.value === 'id' ||
      col.value === 'responsibleTransportCompaniesDisplay' ||
      col.value === 'timetableFieldNumber' ||
      col.value === 'documents' ||
      col.value === 'statementSender' ||
      col.value === 'timetableFieldDescription'
    );
  }

  private initShowStartTimetableHearingButton() {
    this.showStartTimetableHearingButton = true;
    this.timetableHearingYearsService
      .getHearingYears([HearingStatus.Active])
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe((timetableHearingYears) => {
        if (timetableHearingYears.length > 0) {
          this.showStartTimetableHearingButton = false;
        }
      });
  }

  private readonly tableFilterShowStatementInDossier = {
    showStatementInDossierFilter: new TableFilterBoolean(
      0,
      'col-6 container-right-position',
      'TTH.OVERVIEW_TAB.STATEMENT_IN_DOSSIER_SLIDE'
    ),
  };
}
