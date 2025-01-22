import { Component, OnInit } from '@angular/core';
import {
  ApplicationRole,
  ApplicationType,
  HearingStatus,
  StatementStatus,
  SwissCanton,
  TimetableHearingStatement,
  TimetableHearingStatementDocument,
  TimetableHearingStatementsService,
  TimetableHearingStatementV2,
  TimetableHearingYearsService,
  TimetableYearChangeService,
  TransportCompany,
} from '../../../api';
import { ActivatedRoute, Router } from '@angular/router';
import { DialogService } from '../../../core/components/dialog/dialog.service';
import { Cantons } from '../../../core/cantons/Cantons';
import { FormArray, FormControl, FormGroup, Validators } from '@angular/forms';
import { AtlasCharsetsValidator } from '../../../core/validation/charsets/atlas-charsets-validator';
import { AtlasFieldLengthValidator } from '../../../core/validation/field-lengths/atlas-field-length-validator';
import { WhitespaceValidator } from '../../../core/validation/whitespace/whitespace-validator';
import {
  StatementDetailFormGroup,
  StatementSenderFormGroup,
} from './statement-detail-form-group';
import { Canton } from '../../../core/cantons/Canton';
import { map, takeUntil } from 'rxjs/operators';
import { catchError, EMPTY, Observable, of, Subject } from 'rxjs';
import { NotificationService } from '../../../core/notification/notification.service';
import { ValidationService } from '../../../core/validation/validation.service';
import { TthUtils } from '../util/tth-utils';
import { StatementDialogService } from './statement-dialog/service/statement.dialog.service';
import { FileDownloadService } from '../../../core/components/file-upload/file/file-download.service';
import { OpenStatementInMailService } from './open-statement-in-mail.service';
import { StatementShareService } from '../overview-detail/statement-share-service';
import { Pages } from '../../pages';
import { DetailFormComponent } from '../../../core/leave-guard/leave-dirty-form-guard.service';
import { TableService } from '../../../core/components/table/table.service';
import { addElementsToArrayWhenNotUndefined } from '../../../core/util/arrays';
import { PermissionService } from '../../../core/auth/permission/permission.service';

@Component({
  selector: 'app-statement-detail',
  templateUrl: './statement-detail.component.html',
  styleUrls: ['./statement-detail.component.scss'],
})
export class StatementDetailComponent implements OnInit, DetailFormComponent {
  YEAR_OPTIONS: number[] = [];
  CANTON_OPTIONS: Canton[] = [];
  STATUS_OPTIONS: StatementStatus[] = [];
  ttfnValidOn: Date | undefined = undefined;
  statement: TimetableHearingStatementV2 | undefined;
  initialValueForCanton: SwissCanton | null | undefined;
  hearingStatus!: HearingStatus;
  isNew!: boolean;
  form!: FormGroup<StatementDetailFormGroup>;
  isStatementEditable: Observable<boolean | undefined> = of(true);
  uploadedFiles: File[] = [];
  isLoading = false;
  isDuplicating = false;
  isInitializingComponent = true;

  get emails(): string {
    if (this.statement?.statementSender.emails) {
      return Array.from(this.statement?.statementSender.emails).join('\n');
    }
    return '';
  }

  readonly emailValidator = [
    AtlasCharsetsValidator.email,
    AtlasFieldLengthValidator.length_100,
  ];

  private ngUnsubscribe = new Subject<void>();

  constructor(
    private router: Router,
    private route: ActivatedRoute,
    private dialogService: DialogService,
    private timetableHearingYearsService: TimetableHearingYearsService,
    private readonly timetableHearingStatementsService: TimetableHearingStatementsService,
    private notificationService: NotificationService,
    private permissionService: PermissionService,
    private timetableYearChangeService: TimetableYearChangeService,
    private readonly statementDialogService: StatementDialogService,
    private readonly openStatementInMailService: OpenStatementInMailService,
    private readonly statementShareService: StatementShareService,
    private readonly tableService: TableService
  ) {}

  get isHearingStatusArchived() {
    return TthUtils.isHearingStatusArchived(this.hearingStatus);
  }

  get cantonShort() {
    return Cantons.fromSwissCanton(this.form.value.swissCanton!)!.short;
  }

  get alreadySavedDocuments() {
    const documents = this.form.value.documents as { fileName: string }[];
    if (!documents) return [];
    return documents.map((doc) => doc.fileName);
  }

  readonly extractEnumCanton = (option: Canton) => option.enumCanton;

  readonly extractShort = (option: Canton) => option.short;

  ngOnInit() {
    if (this.isInitializingComponent) {
      this.statement = this.route.snapshot.data.statement;
    }

    this.hearingStatus = this.route.snapshot.data.hearingStatus;
    this.isNew = !this.statement;
    this.uploadedFiles = [];

    if (this.hearingStatus === HearingStatus.Active) {
      this.isStatementEditable = this.timetableHearingYearsService
        .getHearingYears([HearingStatus.Active])
        .pipe(
          map((timetableHearingYears) => {
            const foundTimetableHearingYears = timetableHearingYears ?? [];
            if (foundTimetableHearingYears.length > 0) {
              return foundTimetableHearingYears[0].statementEditable;
            }
            return false;
          })
        );
    }

    this.initForm();
    this.initYearOptions();
    this.initTtfnValidOnHandler();
    this.initCantonOptions();
    this.initStatusOptions();
    this.initResponsibleTransportCompanyPrefill();
  }

  cantonSelectionChanged() {
    this.form.controls.editor.setValue(this.statement?.editor);
    this.form.controls.oldSwissCanton.setValue(this.initialValueForCanton);
    this.statementDialogService.openDialog(this.form).subscribe((result) => {
      if (result) {
        const hearingStatement = this.form.value as TimetableHearingStatement;
        this.navigateToStatementDetail(hearingStatement);
      } else {
        this.form.controls.comment.setValue(this.statement?.comment);
      }
    });
  }

  save() {
    if (
      !this.isNew &&
      this.initialValueForCanton != this.form.value.swissCanton
    ) {
      this.cantonSelectionChanged();
    } else {
      ValidationService.validateForm(this.form);
      if (this.form.valid) {
        const hearingStatement = this.form.value as TimetableHearingStatement;
        this.form.disable();
        if (this.isNew) {
          this.createStatement(hearingStatement);
        } else {
          this.updateStatement(this.statement!.id!, hearingStatement);
        }
      }
    }
  }

  toggleEdit() {
    if (this.form.enabled) {
      this.showConfirmationDialog();
    } else if (!this.isHearingStatusArchived) {
      // No event emit to have tu select component show actual data
      this.form.enable({ emitEvent: false });
    }
  }

  backToOverview() {
    this.router
      .navigate(
        [
          Pages.TTH.path,
          this.route.snapshot.params.canton.toLowerCase(),
          this.hearingStatus.toLowerCase(),
        ],
        {
          queryParams: {
            year: this.statement?.timetableYear,
          },
        }
      )
      .then();
  }

  getFormGroup(statement: TimetableHearingStatementV2 | undefined): FormGroup {
    return new FormGroup<StatementDetailFormGroup>({
      id: new FormControl(statement?.id),
      timetableYear: new FormControl(statement?.timetableYear, [
        Validators.required,
      ]),
      statementStatus: new FormControl(statement?.statementStatus, [
        Validators.required,
      ]),
      ttfnid: new FormControl(statement?.ttfnid),
      responsibleTransportCompanies: new FormControl(
        statement?.responsibleTransportCompanies ?? []
      ),
      oldSwissCanton: new FormControl(statement?.oldSwissCanton),
      swissCanton: new FormControl(statement?.swissCanton, [
        Validators.required,
      ]),
      stopPlace: new FormControl(statement?.stopPlace, [
        AtlasFieldLengthValidator.length_255,
        WhitespaceValidator.blankOrEmptySpaceSurrounding,
      ]),
      statementSender: new FormGroup<StatementSenderFormGroup>({
        firstName: new FormControl(statement?.statementSender?.firstName, [
          AtlasFieldLengthValidator.length_100,
        ]),
        lastName: new FormControl(statement?.statementSender?.lastName, [
          AtlasFieldLengthValidator.length_100,
        ]),
        organisation: new FormControl(
          statement?.statementSender?.organisation,
          [AtlasFieldLengthValidator.length_100]
        ),
        zip: new FormControl(statement?.statementSender?.zip, [
          AtlasCharsetsValidator.numeric,
          Validators.min(1000),
          Validators.max(99999),
        ]),
        city: new FormControl(statement?.statementSender?.city, [
          AtlasFieldLengthValidator.length_50,
        ]),
        street: new FormControl(statement?.statementSender?.street, [
          AtlasFieldLengthValidator.length_100,
        ]),
        emails: new FormControl(
          Array.from(statement?.statementSender?.emails ?? []),
          [Validators.required]
        ),
      }),
      statement: new FormControl(statement?.statement, [
        Validators.required,
        AtlasFieldLengthValidator.statement,
      ]),
      justification: new FormControl(statement?.justification, [
        AtlasFieldLengthValidator.statement,
      ]),
      comment: new FormControl(statement?.comment, [
        AtlasFieldLengthValidator.length_280,
      ]),
      documents: new FormArray(
        statement?.documents?.map((document) => new FormControl(document)) ?? []
      ),
      etagVersion: new FormControl(statement?.etagVersion),
      editor: new FormControl(statement?.editor),
    });
  }

  removeDocument(fileName: string) {
    const documents = this.form.value.documents as { fileName: string }[];
    const indexOfFile = documents.findIndex(
      (document) => document.fileName === fileName
    );
    this.form.controls.documents.removeAt(indexOfFile);
    this.form.markAsDirty();
  }

  downloadFile(fileName: string) {
    this.timetableHearingStatementsService
      .getStatementDocument(this.statement!.id!, fileName)
      .subscribe((response) =>
        FileDownloadService.downloadFile(fileName, response)
      );
  }

  openAsMail() {
    this.openStatementInMailService.openAsMail(
      this.statement!,
      this.ttfnValidOn
    );
  }

  private downloadLocalFile(
    id: number,
    documents: Array<TimetableHearingStatementDocument> | undefined
  ) {
    if (documents!.length > 0) {
      this.isLoading = true;
      for (let i = 0; i < documents!.length!; i++) {
        this.timetableHearingStatementsService
          .getStatementDocument(id, documents![i].fileName)
          .pipe(takeUntil(this.ngUnsubscribe))
          .subscribe((response) => {
            this.uploadedFiles.push(
              new File([response], documents![i].fileName)
            );
            if (i === documents!.length! - 1) {
              this.isLoading = false;
            }
          });
      }
    }
  }

  private initYearOptions() {
    this.timetableHearingYearsService
      .getHearingYears([HearingStatus.Active, HearingStatus.Planned])
      .subscribe((timetableHearingYears) => {
        let years = timetableHearingYears.map((year) => year.timetableYear);
        if (!this.isNew) {
          const savedYear = this.form.controls.timetableYear.value!;
          if (years.indexOf(savedYear) === -1) {
            years.push(savedYear);
          }
        }
        years = years.sort((n1, n2) => n1 - n2);
        this.YEAR_OPTIONS = years!;
        if (this.isNew) {
          this.form.controls.timetableYear.setValue(this.YEAR_OPTIONS[0]);
        }
      });
  }

  private initCantonOptions() {
    if (this.isNew) {
      const tthPermissions =
        this.permissionService.getApplicationUserPermission(
          ApplicationType.TimetableHearing
        );
      if (
        tthPermissions.role === ApplicationRole.Supervisor ||
        this.permissionService.isAdmin
      ) {
        this.CANTON_OPTIONS = Cantons.cantons;
      } else if (tthPermissions.role === ApplicationRole.Writer) {
        this.CANTON_OPTIONS = tthPermissions.permissionRestrictions
          .map((restriction) =>
            Cantons.fromSwissCanton(restriction.valueAsString as SwissCanton)
          )
          .filter((element) => element !== undefined)
          .map((e) => e!)
          .sort((n1, n2) => (n1.enumCanton! > n2.enumCanton! ? 1 : -1));
      }
      const defaultCanton = Cantons.getSwissCantonEnum(
        this.route.snapshot.params.canton
      );
      if (
        this.CANTON_OPTIONS.includes(Cantons.fromSwissCanton(defaultCanton)!)
      ) {
        this.form.controls.swissCanton.setValue(defaultCanton);
      }
    } else {
      this.CANTON_OPTIONS = Cantons.cantons;
    }
  }

  private initForm() {
    this.duplicateStatement();
    this.form = this.getFormGroup(this.statement);
    if (this.isDuplicating) {
      this.form.markAsDirty();
    }
    if (!this.isNew) {
      this.initialValueForCanton = this.form.value.swissCanton;
    }
    if (!this.isNew || this.isHearingStatusArchived) {
      this.form.disable();
    }
  }

  private duplicateStatement() {
    if (this.statementShareService.statement) {
      this.isDuplicating = true;
      const localCopyStatement = this.statementShareService.statement;
      this.statement = this.statementShareService.getCloneStatement();
      this.downloadLocalFile(
        localCopyStatement.id!,
        localCopyStatement.documents
      );
      this.statementShareService.clearCachedStatement();
    }
  }

  private initStatusOptions() {
    this.STATUS_OPTIONS = Object.values(StatementStatus);
    if (this.isNew) {
      this.form.controls.statementStatus.setValue(StatementStatus.Received);
      this.form.controls.statementStatus.disable();
    }
  }

  private initTtfnValidOnHandler() {
    this.form.controls.timetableYear.valueChanges.subscribe((year) => {
      if (year) {
        this.timetableYearChangeService
          .getTimetableYearChange(year - 1)
          .subscribe((result) => {
            this.ttfnValidOn = result;
          });
      }
    });
  }

  private initResponsibleTransportCompanyPrefill() {
    this.form.controls.ttfnid.valueChanges.subscribe((ttfnid) => {
      if (ttfnid) {
        this.timetableHearingStatementsService
          .getResponsibleTransportCompanies(
            ttfnid,
            this.form.value.timetableYear! - 1
          )
          .subscribe((result) => {
            this.form.controls.responsibleTransportCompanies.setValue(result);
          });
      }
    });
  }

  private createStatement(statement: TimetableHearingStatementV2) {
    this.isLoading = true;
    this.timetableHearingStatementsService
      .createStatement(statement, this.uploadedFiles)
      .pipe(takeUntil(this.ngUnsubscribe), catchError(this.handleError()))
      .subscribe((statement) => {
        this.isLoading = false;
        this.isDuplicating = false;
        this.notificationService.success(
          'TTH.STATEMENT.NOTIFICATION.ADD_SUCCESS'
        );
        this.navigateToStatementDetail(statement);
      });
  }

  private updateStatement(id: number, statement: TimetableHearingStatementV2) {
    this.isLoading = true;
    this.timetableHearingStatementsService
      .updateHearingStatement(id, statement, this.uploadedFiles)
      .pipe(takeUntil(this.ngUnsubscribe), catchError(this.handleError()))
      .subscribe((statement) => {
        this.isLoading = false;
        this.notificationService.success(
          'TTH.STATEMENT.NOTIFICATION.EDIT_SUCCESS'
        );
        this.navigateToStatementDetail(statement);
      });
  }

  private navigateToStatementDetail(statement: TimetableHearingStatementV2) {
    this.router
      .navigate(['..', statement.id], { relativeTo: this.route })
      .then(() => {
        this.isInitializingComponent = false;
        this.statement = statement;
        this.ngOnInit();
      });
  }

  private handleError() {
    return () => {
      this.isLoading = false;
      this.form.enable();
      return EMPTY;
    };
  }

  private showConfirmationDialog() {
    this.confirmLeave().subscribe((confirmed) => {
      if (confirmed) {
        if (this.isNew) {
          this.backToOverview();
        } else {
          this.form.disable();
          this.ngOnInit();
        }
      }
    });
  }

  private confirmLeave(): Observable<boolean> {
    if (this.form.dirty) {
      return this.dialogService.confirm({
        title: 'DIALOG.DISCARD_CHANGES_TITLE',
        message: 'DIALOG.LEAVE_SITE',
      });
    }
    return of(true);
  }

  next() {
    this.timetableHearingStatementsService
      .getNextStatement(...this.getAlternationParams())
      .subscribe((next) => {
        this.tableService.pageIndex = next.pageable.pageNumber!;
        this.navigateToStatementDetail(next.timetableHearingStatement);
      });
  }

  previous() {
    this.timetableHearingStatementsService
      .getPreviousStatement(...this.getAlternationParams())
      .subscribe((next) => {
        this.tableService.pageIndex = next.pageable.pageNumber!;
        this.navigateToStatementDetail(next.timetableHearingStatement);
      });
  }

  private getAlternationParams(): [
    number,
    number | undefined,
    SwissCanton | undefined,
    Array<string> | undefined,
    Array<StatementStatus> | undefined,
    string | undefined,
    Array<number> | undefined,
    number | undefined,
    number | undefined,
    Array<string> | undefined,
  ] {
    const cantonFilter = Cantons.getSwissCantonFromShort(
      this.route.snapshot.params.canton
    );
    return [
      this.statement!.id!,
      this.statement!.timetableYear,
      cantonFilter,
      this.tableService.filterConfig?.filters.chipSearch.getActiveSearch(),
      this.tableService.filterConfig?.filters.multiSelectStatementStatus.getActiveSearch(),
      this.tableService.filterConfig?.filters.searchSelectTTFN.getActiveSearch()
        ?.ttfnid,
      (
        this.tableService.filterConfig?.filters.searchSelectTU.getActiveSearch() as TransportCompany[]
      )
        ?.map((tu) => tu.id)
        .filter(
          (numberOrUndefined): numberOrUndefined is number =>
            !!numberOrUndefined
        ),
      this.tableService.pageIndex,
      this.tableService.pageSize,
      addElementsToArrayWhenNotUndefined(
        this.tableService.sortString,
        'statementStatus,asc',
        'ttfnid,asc',
        'id,ASC'
      ),
    ];
  }
}
