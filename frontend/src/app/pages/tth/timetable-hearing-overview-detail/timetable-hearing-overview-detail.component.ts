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
import { TableSettingsService } from '../../../core/components/table/table-settings.service';
import { TableColumn } from '../../../core/components/table/table-column';
import { DEFAULT_STATUS_SELECTION } from '../../../core/constants/status.choices';
import { TableSettings } from '../../../core/components/table/table-settings';
import { Pages } from '../../pages';
import { Subscription } from 'rxjs';
import moment from 'moment';
import { TimeTableHearingStatementDisplay } from './time-table-hearing-statement-display';

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
  timeTableHearingStatementDisplays: TimeTableHearingStatementDisplay[] = [];

  tableColumns: TableColumn<TimeTableHearingStatementDisplay>[] = [
    { headerTitle: 'TTH.STATEMENT_STATUS', value: 'statementStatus' },
    { headerTitle: 'TTH.SWISS_CANTON', value: 'cantonDisplay' },
    { headerTitle: 'TTH.TRANSPORT_COMPANY', value: 'responsibleTransportCompanies' },
    { headerTitle: 'TTH.TTFNID', value: 'ttfnid' },
    { headerTitle: 'TTH.TIMETABLE_FIELD_NUMBER', value: 'timetableFieldNumber' },
    { headerTitle: 'COMMON.EDIT_ON', value: 'editionDate', formatAsDate: true },
    { headerTitle: 'COMMON.EDIT_BY', value: 'editorNameDisplay' },
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
    private readonly timetableHearingService: TimetableHearingService,
    private readonly userAdministrationService: UserAdministrationService
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
        this.mapToTimeTableHearingDisplay(container);
        this.totalCount$ = container.totalCount!;
        this.isLoading = false;
      });
  }

  editVersion($event: any) {}

  ngOnDestroy() {
    this.getTimetableHearingStatementsSubscription.unsubscribe();
  }

  private mapToTimeTableHearingDisplay(container: ContainerTimetableHearingStatement) {
    const timetableHearingStatements = container.objects!;
    this.timeTableHearingStatementDisplays = timetableHearingStatements.map((ths) => {
      return {
        statementStatus: ths.statementStatus,
        ttfnid: ths.ttfnid,
        timetableFieldNumber: ths.timetableFieldNumber,
        responsibleTransportCompanies: ths.responsibleTransportCompanies,
        cantonDisplay: this.getCantonShort(ths.swissCanton),
        userNameDisplay: ths.editor,
        editionDate: ths.editionDate,
        editorNameDisplay: this.getEditorDisplay(ths.editor),
      };
    });
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

  private getCantonShort(swissCanton: SwissCanton | undefined) {
    if (swissCanton) {
      return Cantons.fromSwissCanton(swissCanton)?.short;
    }
    return swissCanton;
  }

  private getEditorDisplay(editor: string | undefined) {
    return editor;
  }
}
