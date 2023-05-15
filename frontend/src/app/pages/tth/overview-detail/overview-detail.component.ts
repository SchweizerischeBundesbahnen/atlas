import { Component, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import {
  ApplicationType,
  HearingStatus,
  StatementStatus,
  SwissCanton,
  TimetableFieldNumber,
  TimetableHearingService,
  TimetableHearingStatement,
  TimetableHearingYear,
  TransportCompany,
  UserAdministrationService,
} from '../../../api';
import { Cantons } from '../overview/canton/Cantons';
import { TableColumn } from '../../../core/components/table/table-column';
import { Pages } from '../../pages';
import { Observable, Subject, take, takeUntil } from 'rxjs';
import moment from 'moment';
import { OverviewToTabShareDataService } from '../overview-tab/service/overview-to-tab-share-data.service';
import { MatSelectChange } from '@angular/material/select';
import { TthUtils } from '../util/tth-utils';
import { TablePagination } from '../../../core/components/table/table-pagination';
import { TthChangeStatusDialogService } from './tth-change-status-dialog/service/tth-change-status-dialog.service';
import { ColumnDropDownEvent } from '../../../core/components/table/column-drop-down-event';
import { addElementsToArrayWhenNotUndefined } from '../../../core/util/arrays';
import { TthTableService } from '../tth-table.service';
import { NewTimetableHearingYearDialogService } from '../new-timetable-hearing-year-dialog/service/new-timetable-hearing-year-dialog.service';
import { SelectionModel } from '@angular/cdk/collections';
import { TranslateService } from '@ngx-translate/core';
import {
  disableFilters,
  enableFilters,
  OverviewDetailTableFilterConfig,
} from './overview-detail-table-filter-config';
import {
  getActiveMultiSearch,
  getActiveSearch,
  getActiveSearchForChip,
} from '../../../core/components/table-filter/table-filter-config';
import { TthChangeCantonDialogService } from './tth-change-canton-dialog/service/tth-change-canton-dialog.service';
import { FileDownloadService } from '../../../core/components/file-upload/file/file-download.service';
import { AuthService } from '../../../core/auth/auth.service';
import { MatDialog } from '@angular/material/dialog';
import { DialogManageTthComponent } from '../dialog-manage-tth/dialog-manage-tth.component';
import { DialogService } from '../../../core/components/dialog/dialog.service';
import { StatementShareService } from './statement-share-service';
import { map } from 'rxjs/operators';

@Component({
  selector: 'app-timetable-hearing-overview-detail',
  templateUrl: './overview-detail.component.html',
  styleUrls: ['./overview-detail.component.scss'],
})
export class OverviewDetailComponent implements OnInit, OnDestroy {
  readonly TABLE_FILTER_CONFIG = OverviewDetailTableFilterConfig;

  timeTableHearingStatements: TimetableHearingStatement[] = [];
  totalCount$ = 0;
  tableColumns: TableColumn<TimetableHearingStatement>[] = [];

  hearingStatus = HearingStatus.Active;
  noTimetableHearingYearFound = false;
  noPlannedTimetableHearingYearFound = false;
  foundTimetableHearingYear: TimetableHearingYear = {
    timetableYear: moment().toDate().getFullYear() + 1,
    hearingFrom: moment().toDate(),
    hearingTo: moment().toDate(),
  };
  YEAR_DRODOWN_OPTIONS: number[] = [];
  yearSelection = this.YEAR_DRODOWN_OPTIONS[0];

  cantonShort!: string;
  CANTON_DROPDOWN_OPTIONS = Cantons.cantonsWithSwiss.map((value) => value.short);
  CANTON_DROPDOWN_OPTIONS_WITHOUT_SWISS = Cantons.cantons.map((value) => value.short);
  defaultDropdownCantonSelection = this.CANTON_DROPDOWN_OPTIONS[0];

  STATUS_OPTIONS = Object.values(StatementStatus);

  COLLECTING_ACTION_DROWPDOWN_OPTIONS = ['STATUS_CHANGE', 'CANTON_DELIVERY'];

  showDownloadCsvButton = false;
  showManageTimetableHearingButton = false;
  showAddNewStatementButton = false;
  showAddNewTimetableHearingButton = false;
  showStartTimetableHearingButton = false;
  showHearingDetail = false;

  showCollectingActionButton = true;
  statusChangeCollectingActionsEnabled = false;
  cantonDeliveryCollectingActionsEnabled = false;

  selectedItems: TimetableHearingStatement[] = [];
  sorting = 'statementStatus,asc';
  selectedCheckBox = new SelectionModel<TimetableHearingStatement>(true, []);
  isCheckBoxModeActive = false;
  protected readonly tableFilterConfig = OverviewDetailTableFilterConfig;
  private ngUnsubscribe = new Subject<void>();

  constructor(
    private readonly route: ActivatedRoute,
    private readonly router: Router,
    private readonly timetableHearingService: TimetableHearingService,
    private readonly overviewToTabService: OverviewToTabShareDataService,
    private readonly tthStatusChangeDialog: TthChangeStatusDialogService,
    private readonly tthChangeCantonDialogService: TthChangeCantonDialogService,
    private readonly dialogService: DialogService,
    private readonly tthTableService: TthTableService,
    private readonly newTimetableHearingYearDialogService: NewTimetableHearingYearDialogService,
    private readonly translateService: TranslateService,
    private readonly authService: AuthService,
    private readonly statementShareService: StatementShareService,
    private readonly matDialog: MatDialog,
    private readonly userAdministrationService: UserAdministrationService
  ) {}

  get isHearingYearActive(): boolean {
    return TthUtils.isHearingStatusActive(this.hearingStatus);
  }

  get isSwissCanton(): boolean {
    return this.cantonShort.toLowerCase() === Cantons.swiss.short.toLowerCase();
  }

  get isCollectingActionEnabled(): boolean {
    return this.authService.hasWritePermissionsToForCanton(
      ApplicationType.TimetableHearing,
      this.cantonShort.toLowerCase()
    );
  }

  ngOnInit(): void {
    this.syncCantonShortSharedDate();
    this.defaultDropdownCantonSelection = this.initDefatulDropdownCantonSelection();
    this.hearingStatus = this.route.snapshot.data.hearingStatus;
    if (TthUtils.isHearingStatusActive(this.hearingStatus)) {
      this.tthTableService.activeTabPage = Pages.TTH_ACTIVE;
      this.tableColumns = this.getActiveTableColumns();
      if (!this.isCollectingActionEnabled) {
        this.tableColumns = this.tableColumns.filter((value) => value.value !== 'etagVersion');
        this.disableChangeStatementStatusSelect();
      }
      this.enableCheckboxViewMode();
      this.showManageTimetableHearingButton = this.isSwissCanton;
      this.showAddNewStatementButton = !this.isSwissCanton;
      this.showDownloadCsvButton = true;
      this.initOverviewActiveTable();
    }
    if (TthUtils.isHearingStatusPlanned(this.hearingStatus)) {
      this.removeCheckBoxViewMode();
      this.tthTableService.activeTabPage = Pages.TTH_PLANNED;
      this.sorting = 'swissCanton,asc';
      this.tableColumns = this.getPlannedTableColumns();
      this.showAddNewTimetableHearingButton = true;
      this.showHearingDetail = true;
      this.initOverviewPlannedTable();
      this.initShowStartTimetableHearingButton();
    }
    if (TthUtils.isHearingStatusArchived(this.hearingStatus)) {
      this.removeCheckBoxViewMode();
      this.tthTableService.activeTabPage = Pages.TTH_ARCHIVED;
      this.sorting = 'swissCanton,asc';
      this.tableColumns = this.getArchivedTableColumns();
      this.showDownloadCsvButton = true;
      this.initOverviewArchivedTable();
    }
  }

  getOverview(pagination: TablePagination) {
    const selectedCantonEnum = this.getSelectedCantonToBeSearchFromNavigation();
    this.timetableHearingService
      .getStatements(
        this.foundTimetableHearingYear.timetableYear,
        selectedCantonEnum,
        getActiveSearchForChip(this.tableFilterConfig[0][0]),
        getActiveSearch(this.tableFilterConfig[1][0]),
        getActiveSearch<TimetableFieldNumber | undefined, TimetableFieldNumber>(
          this.tableFilterConfig[1][2]
        )?.ttfnid,
        Array.from(getActiveMultiSearch<TransportCompany>(this.tableFilterConfig[1][1])).map(
          (transportCompany) => transportCompany.id!
        ),
        pagination.page,
        pagination.size,
        addElementsToArrayWhenNotUndefined(pagination.sort, this.sorting, 'ttfnid,ASC')
      )
      .pipe(takeUntil(this.ngUnsubscribe))
      .subscribe((container) => {
        this.timeTableHearingStatements = container.objects!;
        this.totalCount$ = container.totalCount!;
      });
  }

  ngOnDestroy() {
    this.ngUnsubscribe.complete();
  }

  changeSelectedCantonFromDropdown(selectedCanton: MatSelectChange) {
    this.removeCheckBoxViewMode();
    const canton = selectedCanton.value.toLowerCase();
    this.overviewToTabService.changeData(canton);
    this.navigateTo(canton, this.foundTimetableHearingYear.timetableYear);
  }

  changeSelectedYearFromDropdown(selectedYear: MatSelectChange) {
    this.foundTimetableHearingYear.timetableYear = selectedYear.value;
    this.navigateTo(this.cantonShort.toLowerCase(), selectedYear.value);
  }

  editStatement(statement: TimetableHearingStatement) {
    this.router
      .navigate([this.hearingStatus.toLowerCase(), statement.id], {
        relativeTo: this.route.parent,
      })
      .then();
  }

  downloadCsv() {
    this.timetableHearingService
      .getStatementsAsCsv(
        this.translateService.currentLang,
        this.foundTimetableHearingYear.timetableYear,
        this.getSelectedCantonToBeSearchFromNavigation(),
        getActiveSearchForChip(this.tableFilterConfig[0][0]),
        getActiveSearch(this.tableFilterConfig[1][0]),
        getActiveSearch<TimetableFieldNumber | undefined, TimetableFieldNumber>(
          this.tableFilterConfig[1][2]
        )?.ttfnid,
        Array.from(getActiveMultiSearch<TransportCompany>(this.tableFilterConfig[1][1])).map(
          (transportCompany) => transportCompany.id!
        )
      )
      .subscribe((response) => FileDownloadService.downloadFile('statements.csv', response));
  }

  manageTimetableHearing() {
    this.matDialog
      .open<DialogManageTthComponent, number, boolean>(DialogManageTthComponent, {
        data: this.foundTimetableHearingYear.timetableYear,
        disableClose: true,
        panelClass: 'atlas-dialog-panel',
        backdropClass: 'atlas-dialog-backdrop',
      })
      .afterClosed()
      .pipe(take(1))
      .subscribe({
        next: (needsInit) => {
          if (needsInit) {
            this.ngOnInit();
          }
        },
      });
  }

  addNewStatement() {
    this.router
      .navigate([Pages.TTH.path, this.cantonShort, Pages.TTH_ACTIVE.path, 'add'], {
        state: { data: this.cantonShort },
      })
      .then();
  }

  addNewTimetableHearing() {
    this.newTimetableHearingYearDialogService.openDialog().subscribe((result) => {
      if (result) {
        this.noTimetableHearingYearFound = false;
        this.ngOnInit();
      }
    });
  }

  startTimetableHearing() {
    this.dialogService
      .confirm({
        title: 'TTH.START_HEARING_DIALOG.TITLE',
        message: 'TTH.START_HEARING_DIALOG.TEXT',
        confirmText: 'TTH.START_HEARING_DIALOG.CONFIRM',
      })
      .subscribe((confirmed) => {
        if (confirmed) {
          this.timetableHearingService.startHearingYear(this.yearSelection).subscribe(() => {
            this.router.navigate(['..', 'active'], { relativeTo: this.route }).then();
          });
        }
      });
  }

  collectingActions(action: MatSelectChange) {
    if (action.value === 'STATUS_CHANGE') {
      this.statusChangeCollectingActionsEnabled = true;
      this.showCollectingActionButton = false;
      this.ngOnInit();
    }
    if (action.value === 'CANTON_DELIVERY') {
      this.cantonDeliveryCollectingActionsEnabled = true;
      this.showCollectingActionButton = false;
      this.ngOnInit();
    }
  }

  setFoundHearingYear(timetableHearingYears: TimetableHearingYear[]) {
    this.YEAR_DRODOWN_OPTIONS = timetableHearingYears.map((value) => value.timetableYear);
    const paramYear = this.route.snapshot.queryParams.year;
    if (paramYear) {
      this.setFoundHearingYearWhenQueryParamIsProvided(timetableHearingYears, Number(paramYear));
    } else {
      this.setYearSelection(this.YEAR_DRODOWN_OPTIONS[0]);
      this.foundTimetableHearingYear = timetableHearingYears[0];
    }
  }

  changeSelectedStatus(changedStatus: ColumnDropDownEvent) {
    this.tthStatusChangeDialog
      .onClick(
        changedStatus.$event.value,
        [changedStatus.value],
        changedStatus.value.justification,
        'SINGLE'
      )
      .subscribe((result) => {
        this.ngOnInit();
      });
  }

  cancelCollectingAction() {
    this.removeCheckBoxViewMode();
    this.ngOnInit();
  }

  collectingStatusChangeAction(changedStatus: ColumnDropDownEvent) {
    if (this.selectedItems.length > 0) {
      this.tthStatusChangeDialog
        .onClick(changedStatus.value, this.selectedItems, undefined, 'MULTIPLE')
        .subscribe((result) => {
          if (result) {
            this.statusChangeCollectingActionsEnabled = false;
            this.showCollectingActionButton = true;
            this.selectedCheckBox = new SelectionModel<TimetableHearingStatement>(true, []);
            this.removeCheckBoxViewMode();
            this.ngOnInit();
          }
        });
    }
  }

  collectingCantonDeliveryAction($event: MatSelectChange) {
    if (this.selectedItems.length > 0) {
      this.tthChangeCantonDialogService
        .onClick(Cantons.getSwissCantonEnum($event.value)!, this.selectedItems)
        .subscribe((result) => {
          if (result) {
            this.cantonDeliveryCollectingActionsEnabled = false;
            this.showCollectingActionButton = true;
            this.selectedCheckBox = new SelectionModel<TimetableHearingStatement>(true, []);
            this.removeCheckBoxViewMode();
            this.ngOnInit();
          }
        });
    }
  }

  checkedBoxEvent($event: SelectionModel<TimetableHearingStatement>) {
    this.selectedItems = $event.selected;
  }

  duplicate($event: TimetableHearingStatement) {
    this.dialogService
      .confirm({
        title: 'TTH.DUPLICATE.DIALOG.TITLE',
        message: 'TTH.DUPLICATE.DIALOG.MESSAGE',
        cancelText: 'TTH.DUPLICATE.DIALOG.CANCEL',
        confirmText: 'TTH.DUPLICATE.DIALOG.CONFIRM',
      })
      .subscribe((confirmed) => {
        if (confirmed) {
          this.duplicateStatement($event);
        }
      });
  }

  duplicateStatement(statement: TimetableHearingStatement) {
    this.statementShareService.statement = statement;
    this.router
      .navigate([this.hearingStatus.toLowerCase(), 'add'], {
        relativeTo: this.route.parent,
      })
      .then();
  }

  private removeCheckBoxViewMode() {
    this.isCheckBoxModeActive = false;
    this.showCollectingActionButton = true;
    this.statusChangeCollectingActionsEnabled = false;
    this.cantonDeliveryCollectingActionsEnabled = false;
    this.selectedCheckBox = new SelectionModel<TimetableHearingStatement>(true, []);

    enableFilters(this.tableFilterConfig);
  }

  private enableCheckboxViewMode() {
    this.isCheckBoxModeActive =
      this.statusChangeCollectingActionsEnabled || this.cantonDeliveryCollectingActionsEnabled;
    if (this.isCheckBoxModeActive) {
      this.tableColumns = this.getActiveTableColumns();
      this.tableColumns.unshift({
        headerTitle: '',
        disabled: true,
        value: 'id',
        checkbox: {
          changeSelectionCallback: this.collectingStatusChangeAction,
        },
      });
      this.tableColumns.forEach((value) => (value.disabled = true));
      this.disableChangeStatementStatusSelect();
      this.disableDuplicateButtonAction();
      disableFilters(this.tableFilterConfig);
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

  private disableDuplicateButtonAction() {
    const duplicateButtonAction = this.tableColumns.filter(
      (value) => value.value === 'etagVersion'
    )[0];
    if (duplicateButtonAction.button) {
      duplicateButtonAction.button.disabled = true;
    }
  }

  private navigateTo(canton: string, timetableYear: number) {
    this.router
      .navigate([Pages.TTH.path, canton.toLowerCase(), this.hearingStatus.toLowerCase()], {
        queryParams: { year: timetableYear },
      })
      .then(() => {
        this.ngOnInit();
      });
  }

  private initDefatulDropdownCantonSelection() {
    return this.CANTON_DROPDOWN_OPTIONS[
      this.CANTON_DROPDOWN_OPTIONS.findIndex(
        (value) => value.toLowerCase() === this.cantonShort.toLowerCase()
      )
    ];
  }

  private syncCantonShortSharedDate() {
    this.overviewToTabService.cantonShort$.subscribe((res) => (this.cantonShort = res));
    this.overviewToTabService.changeData(this.cantonShort);
    this.checkIfRoutedCantonExists();
  }

  private checkIfRoutedCantonExists() {
    const swissCantonEnum = Cantons.getSwissCantonEnum(this.cantonShort);
    if (!swissCantonEnum) {
      this.noTimetableHearingYearFound = true;
      this.router.navigate([Pages.TTH.path]).then();
    }
  }

  private initOverviewArchivedTable() {
    this.getTimetableHearingYear(HearingStatus.Archived, true);
  }

  private initOverviewPlannedTable() {
    this.getTimetableHearingYear(HearingStatus.Planned, false);
  }

  private getTimetableHearingYear(hearingStatus: HearingStatus, sortReverse: boolean) {
    this.timetableHearingService
      .getHearingYears([hearingStatus])
      .pipe(takeUntil(this.ngUnsubscribe))
      .subscribe((timetableHearingYears) => {
        if (timetableHearingYears.length === 0) {
          this.noTimetableHearingYearFound = true;
        } else if (timetableHearingYears.length >= 1) {
          const foundTimetableHearingYears = TthUtils.sortByTimetableHearingYear(
            timetableHearingYears,
            sortReverse
          );
          this.setFoundHearingYear(foundTimetableHearingYears);
          this.initOverviewTable();
        }
      });
  }

  private setFoundHearingYearWhenQueryParamIsProvided(
    timetableHearingYears: TimetableHearingYear[],
    paramYear: number
  ) {
    const matchedHearingYear = timetableHearingYears.find(
      (value) => value.timetableYear === paramYear
    );
    if (matchedHearingYear) {
      this.foundTimetableHearingYear = matchedHearingYear;
      this.setYearSelection(
        this.YEAR_DRODOWN_OPTIONS[
          this.YEAR_DRODOWN_OPTIONS.findIndex((value) => value === matchedHearingYear.timetableYear)
        ]
      );
    } else {
      this.setYearSelection(this.YEAR_DRODOWN_OPTIONS[0]);
      this.foundTimetableHearingYear = timetableHearingYears[0];
      this.router
        .navigate([
          Pages.TTH.path,
          this.cantonShort.toLowerCase(),
          this.hearingStatus.toLowerCase(),
        ])
        .then();
    }
  }

  private setYearSelection(year: number) {
    this.yearSelection = year;
  }

  private initOverviewActiveTable() {
    this.timetableHearingService
      .getHearingYears([HearingStatus.Active])
      .pipe(takeUntil(this.ngUnsubscribe))
      .subscribe((timetableHearingYears) => {
        if (timetableHearingYears) {
          if (timetableHearingYears.length === 0) {
            this.noTimetableHearingYearFound = true;
            this.getPlannedTimetableYearWhenNoActiveFound();
          } else if (timetableHearingYears.length >= 1) {
            this.foundTimetableHearingYear = timetableHearingYears[0];
            this.initOverviewTable();
          }
        }
      });
  }

  private getPlannedTimetableYearWhenNoActiveFound() {
    this.timetableHearingService
      .getHearingYears([HearingStatus.Planned])
      .pipe(takeUntil(this.ngUnsubscribe))
      .subscribe((timetableHearingYears) => {
        if (timetableHearingYears && timetableHearingYears?.length >= 1) {
          const foundTimetableHearingYears = TthUtils.sortByTimetableHearingYear(
            timetableHearingYears,
            false
          );
          this.foundTimetableHearingYear = foundTimetableHearingYears[0];
        } else {
          this.noTimetableHearingYearFound = true;
          this.noPlannedTimetableHearingYearFound = true;
        }
      });
  }

  private initOverviewTable() {
    this.getOverview({
      page: this.tthTableService.pageIndex,
      size: this.tthTableService.pageSize,
      sort: this.tthTableService.sortString,
    });
  }

  private getSelectedCantonToBeSearchFromNavigation(): SwissCanton | undefined {
    if (this.cantonShort.toLowerCase() === Cantons.swiss.path) {
      // we return undefined to get all cantons from backend
      return undefined;
    } else {
      return Cantons.getSwissCantonEnum(this.cantonShort);
    }
  }

  private mapToShortCanton(canton: SwissCanton) {
    return Cantons.fromSwissCanton(canton)?.short;
  }

  private getActiveTableColumns(): TableColumn<TimetableHearingStatement>[] {
    return [
      {
        headerTitle: 'TTH.STATEMENT_STATUS_HEADER',
        value: 'statementStatus',
        dropdown: {
          disabled: false,
          options: this.STATUS_OPTIONS,
          changeSelectionCallback: this.changeSelectedStatus,
          selectedOption: '',
          translate: {
            withPrefix: 'TTH.STATEMENT_STATUS.',
          },
        },
      },
      { headerTitle: 'TTH.SWISS_CANTON', value: 'swissCanton', callback: this.mapToShortCanton },
      {
        headerTitle: 'TTH.TRANSPORT_COMPANY',
        value: 'responsibleTransportCompaniesDisplay',
      },
      { headerTitle: 'TTH.TIMETABLE_FIELD_NUMBER', value: 'timetableFieldNumber', disabled: true },
      {
        headerTitle: 'TTH.TIMETABLE_FIELD_NUMBER_DESCRIPTION',
        value: 'timetableFieldDescription',
        disabled: true,
      },
      { headerTitle: 'COMMON.EDIT_ON', value: 'editionDate', formatAsDate: true },
      {
        headerTitle: 'COMMON.EDIT_BY',
        value: 'editor',
        getTitle: (value: string): Observable<string> => {
          return this.userAdministrationService.getUserDisplayName(value).pipe(
            take(1),
            map((userDisplayName) => userDisplayName.displayName ?? value)
          );
        },
      },
      {
        headerTitle: '',
        value: 'etagVersion',
        disabled: true,
        button: {
          icon: 'bi bi-files',
          clickCallback: this.duplicate,
          applicationType: 'TIMETABLE_HEARING',
          buttonDataCy: 'duplicate-hearing',
          title: 'TTH.BUTTON.DUPLICATE',
          buttonType: 'icon',
          disabled: false,
        },
      },
    ];
  }

  private getPlannedTableColumns(): TableColumn<TimetableHearingStatement>[] {
    return this.getActiveTableColumns().filter((col) => {
      return (
        col.value === 'swissCanton' ||
        col.value === 'responsibleTransportCompaniesDisplay' ||
        col.value === 'timetableFieldNumber' ||
        col.value === 'timetableFieldDescription'
      );
    });
  }

  private getArchivedTableColumns(): TableColumn<TimetableHearingStatement>[] {
    return this.getActiveTableColumns().filter((col) => {
      return (
        col.value === 'swissCanton' ||
        col.value === 'responsibleTransportCompaniesDisplay' ||
        col.value === 'timetableFieldNumber' ||
        col.value === 'timetableFieldDescription' ||
        col.value === 'editor'
      );
    });
  }

  private initShowStartTimetableHearingButton() {
    this.showStartTimetableHearingButton = true;
    this.timetableHearingService
      .getHearingYears([HearingStatus.Active])
      .pipe(takeUntil(this.ngUnsubscribe))
      .subscribe((timetableHearingYears) => {
        if (timetableHearingYears.length > 0) {
          this.showStartTimetableHearingButton = false;
        }
      });
  }
}
