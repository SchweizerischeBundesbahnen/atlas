import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import {
  ContainerTimetableHearingYear,
  HearingStatus,
  StatementStatus,
  SwissCanton,
  TimetableHearingService,
  TimetableHearingStatement,
  TimetableHearingYear,
  UserAdministrationService,
} from '../../../api';
import { Cantons } from '../overview/canton/Cantons';
import { TableComponent } from '../../../core/components/table/table.component';
import { TableColumn } from '../../../core/components/table/table-column';
import { DEFAULT_STATUS_SELECTION } from '../../../core/constants/status.choices';
import { TableSettings } from '../../../core/components/table/table-settings';
import { Pages } from '../../pages';
import { Subscription } from 'rxjs';
import moment from 'moment';
import { OverviewToTabShareDataService } from '../timetable-hearing-overview-tab/overview-to-tab-share-data.service';
import { MatSelectChange } from '@angular/material/select';

@Component({
  selector: 'app-timetable-hearing-overview-detail',
  templateUrl: './timetable-hearing-overview-detail.component.html',
  styleUrls: ['./timetable-hearing-overview-detail.component.scss'],
})
export class TimetableHearingOverviewDetailComponent implements OnInit, OnDestroy {
  @ViewChild(TableComponent, { static: true })
  tableComponent!: TableComponent<TimetableHearingStatement>;

  hearingStatus = Pages.TTH_ACTIVE.path;

  isLoading = false;
  totalCount$ = 0;
  timeTableHearingStatements: TimetableHearingStatement[] = [];

  tableColumns: TableColumn<TimetableHearingStatement>[] = [];

  noTimetableHearingYearFound = false;

  selectedCantonEnum: SwissCanton | undefined;
  foundTimetableHearingYear: TimetableHearingYear = {
    timetableYear: 2000,
    hearingFrom: moment().toDate(),
    hearingTo: moment().toDate(),
  };
  cantonShort!: string;
  CANTON_OPTIONS = Cantons.cantonsWithSwiss.map((value) => value.short);
  dafaultCantonSelection = this.CANTON_OPTIONS[0];

  COLLECTING_ACTION_OPTIONS = ['STATUS_CHANGE', 'CANTON_DELIVERY', 'DELETE'];

  YEAR_OPTIONS: number[] = [];
  defaultYearSelection = this.YEAR_OPTIONS[0];

  showDownloadCsvButton = false;
  showManageTimetableHearingButton = false;
  showAddNewStatementButton = false;
  showAddNewTimetableHearingButton = false;
  showStartTimetableHearingButton = false;
  showHearingDetail = false;

  private getTimetableHearingStatementsSubscription!: Subscription;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private readonly timetableHearingService: TimetableHearingService,
    private readonly userAdministrationService: UserAdministrationService,
    private overviewToTabService: OverviewToTabShareDataService
  ) {}

  ngOnInit(): void {
    this.syncCantonShortSharedDate();

    const selectedHearingStatus = this.getSelectedHeraingStatus();
    if (selectedHearingStatus === HearingStatus.Active) {
      this.tableColumns = this.getActiveTableColumns();
      this.showManageTimetableHearingButton = this.isSwissCanton();
      this.showAddNewStatementButton = !this.isSwissCanton();
      this.showDownloadCsvButton = true;
      this.initOverviewActiveTable();
    }
    if (selectedHearingStatus === HearingStatus.Planned) {
      this.tableColumns = this.getPlannedTableColumns();
      this.initOverviewPlannedTable();
      this.showAddNewTimetableHearingButton = true;
      this.showStartTimetableHearingButton = true;
      this.showHearingDetail = true;
    }
    if (selectedHearingStatus === HearingStatus.Archived) {
      this.tableColumns = this.getArchivedTableColumns();
      this.showDownloadCsvButton = true;
      this.initOverviewArchivedTable();
    }
  }

  getOverview($paginationAndSearch: TableSettings) {
    this.selectedCantonEnum = this.getSelectedCantonFromNavigation();
    //TODO: clear variables
    this.dafaultCantonSelection = this.getCantonSelection();
    this.isLoading = true;
    this.getTimetableHearingStatementsSubscription = this.timetableHearingService
      .getStatements(
        this.foundTimetableHearingYear.timetableYear,
        this.selectedCantonEnum,
        $paginationAndSearch.searchCriteria,
        $paginationAndSearch.statusRestrictions,
        $paginationAndSearch.ttfid,
        $paginationAndSearch.page,
        $paginationAndSearch.size,
        [$paginationAndSearch.sort!, 'statementStatus,ASC']
      )
      .subscribe((container) => {
        this.timeTableHearingStatements = container.objects!;

        this.totalCount$ = container.totalCount!;
        this.isLoading = false;
      });
  }

  ngOnDestroy() {
    if (!this.noTimetableHearingYearFound) {
      this.getTimetableHearingStatementsSubscription.unsubscribe();
    }
  }

  changeSelectedCanton(selectCanton: MatSelectChange) {
    this.overviewToTabService.changeData(selectCanton.value);
    this.router
      .navigate([Pages.TTH.path, selectCanton.value.toLowerCase(), this.hearingStatus], {
        queryParams: { year: this.foundTimetableHearingYear.timetableYear },
      })
      .then(() => {
        this.dafaultCantonSelection = this.getCantonSelection();
        this.ngOnInit();
      });
  }

  changeSelectedYear(selectYear: MatSelectChange) {
    this.foundTimetableHearingYear.timetableYear = selectYear.value;
    this.router
      .navigate([Pages.TTH.path, this.cantonShort.toLowerCase(), this.hearingStatus], {
        queryParams: { year: selectYear.value },
      })
      .then(() => {
        this.ngOnInit();
      });
  }

  editVersion($event: any) {
    console.log($event);
  }

  downloadCsv() {
    console.log('Download CSV');
  }

  manageTimetableHearing() {
    console.log('manageTimetableHearing');
  }

  addNewStatement() {
    console.log('ADD_NEW_STATEMENT');
  }

  addNewTimetableHearing() {
    console.log('addNewTimetableHearing');
  }

  startTimetableHearing() {
    console.log('showStartTimetableHearing');
  }

  collectingActions(action: MatSelectChange) {
    console.log(action);
  }

  private isSwissCanton() {
    return this.cantonShort.toLowerCase() === Cantons.swiss.short.toLowerCase();
  }

  private getCantonSelection() {
    return this.CANTON_OPTIONS[
      this.CANTON_OPTIONS.findIndex(
        (value) => value.toLowerCase() === this.cantonShort.toLowerCase()
      )
    ];
  }

  private getSelectedHeraingStatus() {
    if (this.route.snapshot.routeConfig && this.route.snapshot.routeConfig.path) {
      this.hearingStatus = this.route.snapshot.routeConfig.path;
    }
    return Object.values(HearingStatus).find((hs) => hs.toLowerCase() === this.hearingStatus);
  }

  private syncCantonShortSharedDate() {
    this.overviewToTabService.cantonShort$.subscribe((res) => (this.cantonShort = res));
    this.overviewToTabService.changeData(this.cantonShort);
    this.checkIfCantonExists();
  }

  private checkIfCantonExists() {
    const swissCantonEnum = Cantons.getSwissCantonEnum(this.cantonShort);
    if (!swissCantonEnum) {
      this.noTimetableHearingYearFound = true;
      this.router.navigate([Pages.TTH.path]).then(() => {});
    }
  }

  private initOverviewArchivedTable() {
    this.timetableHearingService
      .getHearingYears([HearingStatus.Archived])
      .subscribe((timetableHearingYearContainer) => {
        if (timetableHearingYearContainer.objects) {
          if (timetableHearingYearContainer.objects.length === 0) {
            this.noTimetableHearingYearFound = true;
          } else if (timetableHearingYearContainer.objects.length >= 1) {
            timetableHearingYearContainer.objects
              .sort((n1, n2) => n1.timetableYear - n2.timetableYear)
              .reverse();
            this.setFoundHearingYear(timetableHearingYearContainer);
            this.initOverviewTable();
          }
        }
      });
  }

  private initOverviewPlannedTable() {
    this.timetableHearingService
      .getHearingYears([HearingStatus.Planned])
      .subscribe((timetableHearingYearContainer) => {
        //TODO: extract me with sort/reverse
        if (timetableHearingYearContainer.objects) {
          if (timetableHearingYearContainer.objects.length === 0) {
            this.noTimetableHearingYearFound = true;
          } else if (timetableHearingYearContainer.objects.length >= 1) {
            timetableHearingYearContainer.objects.sort(
              (n1, n2) => n1.timetableYear - n2.timetableYear
            );
            this.setFoundHearingYear(timetableHearingYearContainer);
            this.initOverviewTable();
          }
        }
      });
  }

  private setFoundHearingYear(timetableHearingYearContainer: ContainerTimetableHearingYear) {
    if (timetableHearingYearContainer.objects) {
      this.YEAR_OPTIONS = timetableHearingYearContainer.objects.map((value) => value.timetableYear);
      const paramYear = this.route.snapshot.queryParams.year;
      if (paramYear) {
        this.defaultYearSelection =
          this.YEAR_OPTIONS[this.YEAR_OPTIONS.findIndex((value) => value === Number(paramYear))];
        const matchedHearingYear = timetableHearingYearContainer.objects.find(
          (value) => value.timetableYear === Number(paramYear)
        );
        if (matchedHearingYear) {
          this.foundTimetableHearingYear = matchedHearingYear;
        } else {
          this.defaultYearSelection = this.YEAR_OPTIONS[0];
          this.foundTimetableHearingYear = timetableHearingYearContainer.objects[0];
          this.router
            .navigate([Pages.TTH.path, this.cantonShort.toLowerCase(), this.hearingStatus])
            .then(() => {});
        }
      } else {
        this.defaultYearSelection = this.YEAR_OPTIONS[0];
        this.foundTimetableHearingYear = timetableHearingYearContainer.objects[0];
      }
    }
  }

  private initOverviewActiveTable() {
    this.timetableHearingService
      .getHearingYears([HearingStatus.Active])
      .subscribe((timetableHearingYears) => {
        if (timetableHearingYears.objects) {
          if (timetableHearingYears.objects.length === 0) {
            this.noTimetableHearingYearFound = true;
            this.getPlannedTimetableYearWhenNoActiveFound();
          } else if (timetableHearingYears.objects.length >= 1) {
            this.foundTimetableHearingYear = timetableHearingYears.objects[0];
            this.initOverviewTable();
          }
        }
      });
  }

  private getPlannedTimetableYearWhenNoActiveFound() {
    this.timetableHearingService
      .getHearingYears([HearingStatus.Planned])
      .subscribe((plannedTimetableHearingYears) => {
        if (
          plannedTimetableHearingYears.objects &&
          plannedTimetableHearingYears.objects.length >= 1
        ) {
          plannedTimetableHearingYears.objects.sort(
            (n1, n2) => n1.timetableYear - n2.timetableYear
          );
          this.foundTimetableHearingYear = plannedTimetableHearingYears.objects[0];
        } else {
          this.noTimetableHearingYearFound = true;
        }
      });
  }

  private initOverviewTable() {
    this.getOverview({
      page: 0,
      size: 10,
      sort: 'statementStatus,ASC',
      statusChoices: DEFAULT_STATUS_SELECTION,
    });
  }

  private getSelectedCantonFromNavigation(): SwissCanton | undefined {
    if (!this.cantonShort) {
      throw new Error('No canton was provided!');
    }
    if (this.cantonShort.toLowerCase() === Cantons.swiss.path) {
      return undefined;
    } else {
      const swissCantonEnum = Cantons.getSwissCantonEnum(this.cantonShort);
      if (swissCantonEnum) {
        return swissCantonEnum;
      } else {
        throw new Error('No canton found with name: ' + this.cantonShort);
      }
    }
  }

  private mapToShortCanton(canton: SwissCanton) {
    return Cantons.fromSwissCanton(canton)?.short;
  }

  private changeSelectedStatus(event: MatSelectChange) {
    console.log(event.value);
  }

  private getActiveTableColumns(): TableColumn<TimetableHearingStatement>[] {
    return [
      {
        headerTitle: 'TTH.STATEMENT_STATUS_HEADER',
        value: 'statementStatus',
        dropdown: {
          options: Object.values(StatementStatus),
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
      { headerTitle: 'TTH.TTFNID', value: 'ttfnid' },
      { headerTitle: 'TTH.TIMETABLE_FIELD_NUMBER', value: 'timetableFieldNumber' },
      { headerTitle: 'COMMON.EDIT_ON', value: 'editionDate', formatAsDate: true },
      { headerTitle: 'COMMON.EDIT_BY', value: 'editor' },
    ];
  }

  private getPlannedTableColumns(): TableColumn<TimetableHearingStatement>[] {
    return this.getActiveTableColumns().filter((col) => {
      return (
        col.value === 'swissCanton' ||
        col.value === 'responsibleTransportCompaniesDisplay' ||
        col.value === 'ttfnid' ||
        col.value === 'timetableFieldNumber'
      );
    });
  }

  private getArchivedTableColumns(): TableColumn<TimetableHearingStatement>[] {
    return this.getActiveTableColumns().filter((col) => {
      return (
        col.value === 'swissCanton' ||
        col.value === 'responsibleTransportCompaniesDisplay' ||
        col.value === 'ttfnid' ||
        col.value === 'timetableFieldNumber' ||
        col.value === 'editor'
      );
    });
  }
}
