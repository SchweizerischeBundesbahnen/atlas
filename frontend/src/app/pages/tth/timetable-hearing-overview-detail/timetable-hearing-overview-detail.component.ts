import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import {
  ContainerTimetableHearingStatement,
  HearingStatus,
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
    { headerTitle: 'TTH.STATEMENT_STATUS', value: 'statementStatus' },
    { headerTitle: 'TTH.SWISS_CANTON', value: 'swissCanton' },
    { headerTitle: 'TTH.TRANSPORT_COMPANY', value: 'responsibleTransportCompanies' },
    { headerTitle: 'TTH.TTFNID', value: 'ttfnid' },
    { headerTitle: 'TTH.TIMETABLE_FIELD_NUMBER', value: 'timetableFieldNumber' },
    { headerTitle: 'COMMON.EDIT_ON', value: 'editionDate', formatAsDate: true },
    { headerTitle: 'COMMON.EDIT_BY', value: 'editor' },
  ];
  showEmptyTimeTableHearingComponent = true;
  data!: ContainerTimetableHearingStatement;
  selectedCantonEnum: SwissCanton | undefined;
  foundTimetableHearingYear: TimetableHearingYear = {
    timetableYear: 2000,
    hearingFrom: moment().toDate(),
    hearingTo: moment().toDate(),
  };
  cantonShort!: string;
  CANTON_OPTIONS = Cantons.cantonsWithSwiss.map((value) => value.short);
  dafaultCantonSelection = this.CANTON_OPTIONS[0];
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
      this.initOverviewActiveTable();
    }
    if (selectedHearingStatus === HearingStatus.Planned) {
      this.initOverviewPlannedTable();
    }
    if (selectedHearingStatus === HearingStatus.Archived) {
      this.initOverviewArchivedTable();
    }
  }

  getOverview($paginationAndSearch: TableSettings) {
    this.selectedCantonEnum = this.getSelectedCanton();
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

  editVersion($event: any) {}

  ngOnDestroy() {
    if (!this.showEmptyTimeTableHearingComponent) {
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
      .getArchivedHearingYears()
      .subscribe((plannedTimetableHearingYears) => {
        this.foundTimetableHearingYear = plannedTimetableHearingYears[0];
        this.showEmptyTimeTableHearingComponent = false;
        this.initOverviewTable();
      });
  }

  private initOverviewPlannedTable() {
    this.timetableHearingService
      .getPlannedHearingYears()
      .subscribe((plannedTimetableHearingYears) => {
        this.foundTimetableHearingYear = plannedTimetableHearingYears[0];
        this.showEmptyTimeTableHearingComponent = false;
        this.initOverviewTable();
      });
  }

  private initOverviewActiveTable() {
    this.timetableHearingService.getActiveHearingYear().subscribe((timetableHearingYear) => {
      this.showEmptyTimeTableHearingComponent = false;
      this.foundTimetableHearingYear = timetableHearingYear;
      this.initOverviewTable();
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

  private getSelectedCanton(): SwissCanton | undefined {
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
}
