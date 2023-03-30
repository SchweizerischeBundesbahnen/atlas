import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import {
  ContainerTimetableHearingStatement,
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

  tableColumns: TableColumn<TimetableHearingStatement>[] = [
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
  noTimetableHearingYearFound = false;
  data!: ContainerTimetableHearingStatement;
  selectedCantonEnum: SwissCanton | undefined;
  foundTimetableHearingYear: TimetableHearingYear = {
    timetableYear: 2000,
    hearingFrom: moment().toDate(),
    hearingTo: moment().toDate(),
  };
  cantonShort!: string;
  CANTON_OPTIONS = Cantons.cantonsWithSwiss.map((value) => value.short);
  COLLECTING_ACTION_OPTIONS = ['STATUS_CHANGE', 'CANTON_DELIVERY', 'DELETE'];
  dafaultCantonSelection = this.CANTON_OPTIONS[0];
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
      this.showManageTimetableHearingButton = this.isSwissCanton();
      this.showAddNewStatementButton = !this.isSwissCanton();
      this.showDownloadCsvButton = true;
      this.initOverviewActiveTable();
    }
    if (selectedHearingStatus === HearingStatus.Planned) {
      this.initOverviewPlannedTable();
      this.showAddNewTimetableHearingButton = true;
      this.showStartTimetableHearingButton = true;
      this.showHearingDetail = true;
    }
    if (selectedHearingStatus === HearingStatus.Archived) {
      this.showDownloadCsvButton = true;
      this.initOverviewArchivedTable();
    }
  }

  getOverview($paginationAndSearch: TableSettings) {
    this.selectedCantonEnum = this.getSelectedCantonFromNavigation();
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

  editVersion($event: any) {
    console.log($event);
  }

  ngOnDestroy() {
    if (!this.noTimetableHearingYearFound) {
      this.getTimetableHearingStatementsSubscription.unsubscribe();
    }
  }

  changeSelectedCanton(canton: MatSelectChange) {
    this.overviewToTabService.changeData(canton.value);
    this.router
      .navigate([Pages.TTH.path, canton.value.toLowerCase(), this.hearingStatus])
      .then(() => {
        this.dafaultCantonSelection = this.getCantonSelection();
        this.ngOnInit();
      });
  }

  changeSelectedYear(year: MatSelectChange) {
    this.foundTimetableHearingYear.timetableYear = year.value;
    this.initOverviewTable();
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
  }

  private initOverviewArchivedTable() {
    this.timetableHearingService
      .getHearingYears([HearingStatus.Archived])
      .subscribe((archivedTimetableHearingYears) => {
        if (archivedTimetableHearingYears.objects) {
          if (archivedTimetableHearingYears.objects.length === 0) {
            this.noTimetableHearingYearFound = true;
          } else if (
            archivedTimetableHearingYears.objects &&
            archivedTimetableHearingYears.objects.length >= 1
          ) {
            archivedTimetableHearingYears.objects
              .sort((n1, n2) => n1.timetableYear - n2.timetableYear)
              .reverse();
            this.YEAR_OPTIONS = archivedTimetableHearingYears.objects.map(
              (value) => value.timetableYear
            );
            this.defaultYearSelection = this.YEAR_OPTIONS[0];
            this.foundTimetableHearingYear = archivedTimetableHearingYears.objects[0];
            this.initOverviewTable();
          }
        }
      });
  }

  private initOverviewPlannedTable() {
    this.timetableHearingService
      .getHearingYears([HearingStatus.Planned])
      .subscribe((plannedTimetableHearingYears) => {
        if (plannedTimetableHearingYears.objects) {
          if (plannedTimetableHearingYears.objects.length === 0) {
            this.noTimetableHearingYearFound = true;
          } else if (plannedTimetableHearingYears.objects.length >= 1) {
            plannedTimetableHearingYears.objects.sort(
              (n1, n2) => n1.timetableYear - n2.timetableYear
            );
            this.YEAR_OPTIONS = plannedTimetableHearingYears.objects.map(
              (value) => value.timetableYear
            );
            this.defaultYearSelection = this.YEAR_OPTIONS[0];
            this.foundTimetableHearingYear = plannedTimetableHearingYears.objects[0];
            this.initOverviewTable();
          }
        }
      });
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
}
