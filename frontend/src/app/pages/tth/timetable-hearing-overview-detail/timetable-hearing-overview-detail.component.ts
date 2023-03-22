import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import {
  ContainerTimetableHearingStatement,
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

@Component({
  selector: 'app-timetable-hearing-overview-detail',
  templateUrl: './timetable-hearing-overview-detail.component.html',
  styleUrls: ['./timetable-hearing-overview-detail.component.scss'],
})
export class TimetableHearingOverviewDetailComponent implements OnInit, OnDestroy {
  @ViewChild(TableComponent, { static: true })
  tableComponent!: TableComponent<TimetableHearingStatement>;
  hearingPlan = Pages.TTH_ACTUAL.path;
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
  private getTimetableHearingStatementsSubscription!: Subscription;

  constructor(
    private route: ActivatedRoute,
    private readonly timetableHearingService: TimetableHearingService,
    private readonly userAdministrationService: UserAdministrationService,
    private overviewToTabService: OverviewToTabShareDataService
  ) {}

  ngOnInit(): void {
    this.overviewToTabService.cantonShort$.subscribe((res) => (this.cantonShort = res));
    this.overviewToTabService.changeData(this.cantonShort);
    if (this.route.snapshot.routeConfig && this.route.snapshot.routeConfig.path) {
      this.hearingPlan = this.route.snapshot.routeConfig.path;
      console.log(this.hearingPlan);
    }
    this.initSelectedEnumCanton();
    const actualYear = moment(new Date()).format('YYYY');
    this.timetableHearingService.getHearingYear(Number(actualYear)).subscribe((thy) => {
      if (thy) {
        this.showEmptyTimeTableHearingComponent = false;
        this.foundTimetableHearingYear = thy;
        this.getOverview({
          page: 0,
          size: 10,
          sort: 'statementStatus,ASC',
          statusChoices: DEFAULT_STATUS_SELECTION,
        });
      } else {
        this.showEmptyTimeTableHearingComponent = true;
      }
    });
  }

  getOverview($paginationAndSearch: TableSettings) {
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
    this.getTimetableHearingStatementsSubscription.unsubscribe();
  }

  private initSelectedEnumCanton() {
    if (!this.cantonShort) {
      throw new Error('No canton was provided!');
    }
    if (this.cantonShort === Cantons.swiss.path) {
      this.selectedCantonEnum = undefined;
    } else {
      const swissCantonEnum = Cantons.getSwissCantonEnum(this.cantonShort);
      if (swissCantonEnum) {
        this.selectedCantonEnum = swissCantonEnum;
      } else {
        throw new Error('No canton found with name: ' + this.cantonShort);
      }
    }
  }
}
