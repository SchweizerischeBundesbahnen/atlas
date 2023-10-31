import { Component, DestroyRef, inject, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import {
  ApplicationType,
  HearingStatus,
  StatementStatus,
  SwissCanton,
  TimetableHearingStatement,
  TimetableHearingStatementsService,
  TimetableHearingYear,
  TimetableHearingYearsService,
  TransportCompany,
  UserAdministrationService,
} from '../../../api';
import { Cantons } from '../../../core/cantons/Cantons';
import { TableColumn } from '../../../core/components/table/table-column';
import { Pages } from '../../pages';
import { Observable, take } from 'rxjs';
import moment from 'moment';
import { OverviewToTabShareDataService } from '../overview-tab/service/overview-to-tab-share-data.service';
import { MatSelectChange } from '@angular/material/select';
import { TthUtils } from '../util/tth-utils';
import { TablePagination } from '../../../core/components/table/table-pagination';
import { TthChangeStatusDialogService } from './tth-change-status-dialog/service/tth-change-status-dialog.service';
import { ColumnDropDownEvent } from '../../../core/components/table/column-drop-down-event';
import { addElementsToArrayWhenNotUndefined } from '../../../core/util/arrays';
import { NewTimetableHearingYearDialogService } from '../new-timetable-hearing-year-dialog/service/new-timetable-hearing-year-dialog.service';
import { SelectionModel } from '@angular/cdk/collections';
import { TranslateService } from '@ngx-translate/core';
import { TthChangeCantonDialogService } from './tth-change-canton-dialog/service/tth-change-canton-dialog.service';
import { FileDownloadService } from '../../../core/components/file-upload/file/file-download.service';
import { AuthService } from '../../../core/auth/auth.service';
import { MatDialog } from '@angular/material/dialog';
import { DialogManageTthComponent } from '../dialog-manage-tth/dialog-manage-tth.component';
import { DialogService } from '../../../core/components/dialog/dialog.service';
import { StatementShareService } from './statement-share-service';
import { map } from 'rxjs/operators';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { TableService } from '../../../core/components/table/table.service';
import { TableFilter } from '../../../core/components/table-filter/config/table-filter';
import { TthTableFilterSettingsService } from '../tth-table-filter-settings.service';

@Component({
  selector: 'app-timetable-hearing-overview-detail',
  templateUrl: './overview-detail.component.html',
  styleUrls: ['./overview-detail.component.scss'],
})
export class OverviewDetailComponent implements OnInit {
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
  YEAR_DROPDOWN_OPTIONS: number[] = [];
  yearSelection = this.YEAR_DROPDOWN_OPTIONS[0];

  cantonShort!: string;
  CANTON_DROPDOWN_OPTIONS = Cantons.cantonsWithSwiss.map((value) => value.short);
  CANTON_DROPDOWN_OPTIONS_WITHOUT_SWISS = Cantons.cantons.map((value) => value.short);
  defaultDropdownCantonSelection = this.CANTON_DROPDOWN_OPTIONS[0];

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

  statementEditable = false;
  selectedItems: TimetableHearingStatement[] = [];
  sorting = 'statementStatus,asc';
  selectedCheckBox = new SelectionModel<TimetableHearingStatement>(true, []);
  isCheckBoxModeActive = false;
  private destroyRef = inject(DestroyRef);

  tableFilterConfig!: TableFilter<unknown>[][];

  constructor(
    private readonly route: ActivatedRoute,
    private readonly router: Router,
    private readonly timetableHearingStatementsService: TimetableHearingStatementsService,
    private readonly timetableHearingYearsService: TimetableHearingYearsService,
    private readonly overviewToTabService: OverviewToTabShareDataService,
    private readonly tthStatusChangeDialogService: TthChangeStatusDialogService,
    private readonly tthChangeCantonDialogService: TthChangeCantonDialogService,
    private readonly dialogService: DialogService,
    private readonly tableService: TableService,
    private readonly newTimetableHearingYearDialogService: NewTimetableHearingYearDialogService,
    private readonly translateService: TranslateService,
    private readonly authService: AuthService,
    private readonly statementShareService: StatementShareService,
    private readonly matDialog: MatDialog,
    private readonly userAdministrationService: UserAdministrationService,
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
      this.cantonShort.toLowerCase(),
    );
  }

  ngOnInit(): void {
    this.syncCantonShortSharedDate();
    this.defaultDropdownCantonSelection = this.initDefaultDropdownCantonSelection();
    this.hearingStatus = this.route.snapshot.data.hearingStatus;
    if (TthUtils.isHearingStatusActive(this.hearingStatus)) {
      this.tableFilterConfig = this.tableService.initializeFilterConfig(
        TthTableFilterSettingsService.createSettings(),
        Pages.TTH_ACTIVE,
      );
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
      this.tableFilterConfig = this.tableService.initializeFilterConfig(
        TthTableFilterSettingsService.createSettings(),
        Pages.TTH_PLANNED,
      );
      this.sorting = 'swissCanton,asc';
      this.tableColumns = this.getPlannedTableColumns();
      this.showAddNewTimetableHearingButton = true;
      this.showHearingDetail = true;
      this.initOverviewPlannedTable();
      this.initShowStartTimetableHearingButton();
    }
    if (TthUtils.isHearingStatusArchived(this.hearingStatus)) {
      this.removeCheckBoxViewMode();
      this.tableFilterConfig = this.tableService.initializeFilterConfig(
        TthTableFilterSettingsService.createSettings(),
        Pages.TTH_ARCHIVED,
      );
      this.sorting = 'swissCanton,asc';
      this.tableColumns = this.getArchivedTableColumns();
      this.showDownloadCsvButton = true;
      this.initOverviewArchivedTable();
    }
  }

  getOverview(pagination: TablePagination) {
    const selectedCantonEnum = this.getSelectedCantonToBeSearchFromNavigation();
    this.timetableHearingStatementsService
      .getStatements(
        this.foundTimetableHearingYear.timetableYear,
        selectedCantonEnum,
        this.tableService.filter.chipSearch.getActiveSearch(),
        this.tableService.filter.multiSelectStatementStatus.getActiveSearch(),
        this.tableService.filter.searchSelectTTFN.getActiveSearch()?.ttfnid,
        (this.tableService.filter.searchSelectTU.getActiveSearch() as TransportCompany[])
          ?.map((tu) => tu.id)
          .filter((numberOrUndefined): numberOrUndefined is number => !!numberOrUndefined),
        pagination.page,
        pagination.size,
        addElementsToArrayWhenNotUndefined(pagination.sort, this.sorting, 'ttfnid,ASC'),
      )
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe((container) => {
        this.timeTableHearingStatements = container.objects!;
        this.totalCount$ = container.totalCount!;
        this.isTableColumnsInitialized = true;
      });
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
    this.timetableHearingStatementsService
      .getStatementsAsCsv(
        this.translateService.currentLang,
        this.foundTimetableHearingYear.timetableYear,
        this.getSelectedCantonToBeSearchFromNavigation(),
        this.tableService.filter.chipSearch.getActiveSearch(),
        this.tableService.filter.multiSelectStatementStatus.getActiveSearch(),
        this.tableService.filter.searchSelectTTFN.getActiveSearch()?.ttfnid,
        (this.tableService.filter.searchSelectTU.getActiveSearch() as TransportCompany[])
          ?.map((tu) => tu.id)
          .filter((numberOrUndefined): numberOrUndefined is number => !!numberOrUndefined),
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
          this.timetableHearingYearsService.startHearingYear(this.yearSelection).subscribe(() => {
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
    this.YEAR_DROPDOWN_OPTIONS = timetableHearingYears.map((value) => value.timetableYear);
    const paramYear = this.route.snapshot.queryParams.year;
    if (paramYear) {
      this.setFoundHearingYearWhenQueryParamIsProvided(timetableHearingYears, Number(paramYear));
    } else {
      this.setYearSelection(this.YEAR_DROPDOWN_OPTIONS[0]);
      this.foundTimetableHearingYear = timetableHearingYears[0];
    }
  }

  changeSelectedStatus(changedStatus: ColumnDropDownEvent) {
    this.tthStatusChangeDialogService
      .onClick(
        changedStatus.$event.value,
        [changedStatus.value],
        changedStatus.value.justification,
        'SINGLE',
      )
      .subscribe(() => {
        this.ngOnInit();
      });
  }

  cancelCollectingAction() {
    this.removeCheckBoxViewMode();
    this.ngOnInit();
  }

  collectingStatusChangeAction(changedStatus: ColumnDropDownEvent) {
    if (this.selectedItems.length > 0) {
      this.tthStatusChangeDialogService
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
    this.selectedItems = [];
    this.tableService.filterConfig?.enableFilters();
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
      this.tableService.filterConfig?.disableFilters();
    } else {
      this.removeCheckBoxViewMode();
    }
  }

  private disableChangeStatementStatusSelect() {
    const statementStatusTableColumn = this.tableColumns.filter(
      (value) => value.value === 'statementStatus',
    )[0];
    if (statementStatusTableColumn.dropdown) {
      statementStatusTableColumn.dropdown.disabled = true;
    }
  }

  private disableDuplicateButtonAction() {
    const duplicateButtonAction = this.tableColumns.filter(
      (value) => value.value === 'etagVersion',
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

  private initDefaultDropdownCantonSelection() {
    return this.CANTON_DROPDOWN_OPTIONS[
      this.CANTON_DROPDOWN_OPTIONS.findIndex(
        (value) => value.toLowerCase() === this.cantonShort.toLowerCase(),
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
    this.timetableHearingYearsService
      .getHearingYears([hearingStatus])
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe((timetableHearingYears) => {
        if (timetableHearingYears.length === 0) {
          this.noTimetableHearingYearFound = true;
        } else if (timetableHearingYears.length >= 1) {
          const foundTimetableHearingYears = TthUtils.sortByTimetableHearingYear(
            timetableHearingYears,
            sortReverse,
          );
          this.setFoundHearingYear(foundTimetableHearingYears);
          this.initOverviewTable();
        }
      });
  }

  private setFoundHearingYearWhenQueryParamIsProvided(
    timetableHearingYears: TimetableHearingYear[],
    paramYear: number,
  ) {
    const matchedHearingYear = timetableHearingYears.find(
      (value) => value.timetableYear === paramYear,
    );
    if (matchedHearingYear) {
      this.foundTimetableHearingYear = matchedHearingYear;
      this.setYearSelection(
        this.YEAR_DROPDOWN_OPTIONS[
          this.YEAR_DROPDOWN_OPTIONS.findIndex(
            (value) => value === matchedHearingYear.timetableYear,
          )
        ],
      );
    } else {
      this.setYearSelection(this.YEAR_DROPDOWN_OPTIONS[0]);
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
    this.timetableHearingYearsService
      .getHearingYears([HearingStatus.Active])
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe((timetableHearingYears) => {
        if (timetableHearingYears) {
          if (timetableHearingYears.length === 0) {
            this.noTimetableHearingYearFound = true;
            this.getPlannedTimetableYearWhenNoActiveFound();
          } else if (timetableHearingYears.length >= 1) {
            this.foundTimetableHearingYear = timetableHearingYears[0];
            this.statementEditable = this.foundTimetableHearingYear.statementEditable!;
            this.tableColumns = this.getActiveTableColumns();
            this.enableCheckboxViewMode();
            this.initOverviewTable();
          }
        }
      });
  }

  private getPlannedTimetableYearWhenNoActiveFound() {
    this.timetableHearingYearsService
      .getHearingYears([HearingStatus.Planned])
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe((timetableHearingYears) => {
        if (timetableHearingYears && timetableHearingYears?.length >= 1) {
          const foundTimetableHearingYears = TthUtils.sortByTimetableHearingYear(
            timetableHearingYears,
            false,
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
      page: this.tableService.pageIndex,
      size: this.tableService.pageSize,
      sort: this.tableService.sortString,
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
          disabled: !this.statementEditable,
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
            map((userDisplayName) => userDisplayName.displayName ?? value),
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
    this.timetableHearingYearsService
      .getHearingYears([HearingStatus.Active])
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe((timetableHearingYears) => {
        if (timetableHearingYears.length > 0) {
          this.showStartTimetableHearingButton = false;
        }
      });
  }
}
