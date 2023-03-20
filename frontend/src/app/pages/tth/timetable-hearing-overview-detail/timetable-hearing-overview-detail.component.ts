import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import {
  ContainerTimetableHearingStatement,
  SwissCanton,
  TimetableHearingService,
  TimetableHearingStatement,
  TimetableHearingYear,
} from '../../../api';
import { Cantons } from '../overview/canton/Cantons';
import { TableComponent } from '../../../core/components/table/table.component';
import { TableSettingsService } from '../../../core/components/table/table-settings.service';
import { TableColumn } from '../../../core/components/table/table-column';
import { DEFAULT_STATUS_SELECTION } from '../../../core/constants/status.choices';
import { TableSettings } from '../../../core/components/table/table-settings';
import { Pages } from '../../pages';
import { Subscription } from 'rxjs';
import moment from 'moment';

@Component({
  selector: 'app-timetable-hearing-overview-detail',
  templateUrl: './timetable-hearing-overview-detail.component.html',
  styleUrls: ['./timetable-hearing-overview-detail.component.scss'],
})
export class TimetableHearingOverviewDetailComponent implements OnInit, OnDestroy {
  @ViewChild(TableComponent, { static: true })
  tableComponent!: TableComponent<TimetableHearingStatement>;
  isLoading = false;
  totalCount$ = 0;
  timetableHearingStatements: TimetableHearingStatement[] = [];

  tableColumns: TableColumn<TimetableHearingStatement>[] = [
    { headerTitle: 'TTH.STATEMENT_STATUS', value: 'statementStatus' },
    { headerTitle: 'TTH.SWISS_CANTON', value: 'swissCanton' },
    { headerTitle: 'TTH.TRANSPORT_COMPANY', value: 'responsibleTransportCompanies' },
    { headerTitle: 'TTH.TTFNID', value: 'ttfnid' },
    {
      headerTitle: 'TTH.TIMETABLE_FIELD_NUMBER',
      value: 'timetableFieldNumber',
    },
    { headerTitle: 'COMMON.EDIT_ON', value: 'editionDate', formatAsDate: true },
    { headerTitle: 'COMMON.EDIT_BY', value: 'editor' },
  ];
  data!: ContainerTimetableHearingStatement;
  selectedCantonEnum = SwissCanton.Aargau;
  foundTimetableHearingYear: TimetableHearingYear = {
    timetableYear: 2000,
    hearingFrom: moment().toDate(),
    hearingTo: moment().toDate(),
  };
  cantonShort!: string;
  private getTimetableHearingStatementsSubscription!: Subscription;

  constructor(
    private route: ActivatedRoute,
    private tableSettingsService: TableSettingsService,
    private readonly timetableHearingService: TimetableHearingService
  ) {}

  ngOnInit(): void {
    this.data = this.route.snapshot.data;
    this.initSelectedEnumCanton();
    const actualYear = moment(new Date()).format('YYYY');
    this.timetableHearingService.getHearingYear(Number(actualYear)).subscribe((value) => {
      if (value) {
        this.foundTimetableHearingYear = value;
        this.getOverview({
          page: 0,
          size: 10,
          sort: 'statementStatus,ASC',
          statusChoices: DEFAULT_STATUS_SELECTION,
        });
      }
    });
  }

  getOverview($paginationAndSearch: TableSettings) {
    this.tableSettingsService.storeTableSettings(Pages.TTFN.path, $paginationAndSearch);
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
        this.timetableHearingStatements = container.objects!;
        this.totalCount$ = container.totalCount!;
        this.isLoading = false;
      });
  }

  editVersion($event: any) {}

  ngOnDestroy() {
    this.getTimetableHearingStatementsSubscription.unsubscribe();
  }

  private initSelectedEnumCanton() {
    this.cantonShort = this.route.snapshot.params['canton'];
    if (!this.cantonShort) {
      throw new Error('No canton was provided!');
    }
    const swissCantonEnum = Cantons.getSwissCantonEnum(this.cantonShort);
    if (swissCantonEnum) {
      this.selectedCantonEnum = swissCantonEnum;
    } else {
      throw new Error('No canton found with name: ' + this.cantonShort);
    }
  }
}
