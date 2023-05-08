import { Component, OnInit } from '@angular/core';
import {
  ApplicationRole,
  ApplicationType,
  HearingStatus,
  StatementStatus,
  SwissCanton,
  TimetableHearingService,
  TimetableHearingStatement,
  TimetableHearingStatementDocument,
  TimetableYearChangeService,
} from '../../../api';
import { ActivatedRoute, Router } from '@angular/router';
import { DialogService } from '../../../core/components/dialog/dialog.service';
import { Cantons } from '../overview/canton/Cantons';
import { FormArray, FormControl, FormGroup, Validators } from '@angular/forms';
import { AtlasCharsetsValidator } from '../../../core/validation/charsets/atlas-charsets-validator';
import { AtlasFieldLengthValidator } from '../../../core/validation/field-lengths/atlas-field-length-validator';
import { WhitespaceValidator } from '../../../core/validation/whitespace/whitespace-validator';
import { StatementDetailFormGroup, StatementSenderFormGroup } from './statement-detail-form-group';
import { Canton } from '../overview/canton/Canton';
import { map, takeUntil } from 'rxjs/operators';
import { catchError, EMPTY, Observable, of, Subject } from 'rxjs';
import { NotificationService } from '../../../core/notification/notification.service';
import { ValidationService } from '../../../core/validation/validation.service';
import { AuthService } from '../../../core/auth/auth.service';
import { TthUtils } from '../util/tth-utils';
import { StatementDialogService } from './statement-dialog/service/statement.dialog.service';
import { FileDownloadService } from '../../../core/components/file-upload/file/file-download.service';
import { OpenStatementInMailService } from './open-statement-in-mail.service';
import { StatementShareService } from '../overview-detail/statement-share-service';

@Component({
  selector: 'app-statement-detail',
  templateUrl: './statement-detail.component.html',
  styleUrls: ['./statement-detail.component.scss'],
})
export class StatementDetailComponent implements OnInit {
  YEAR_OPTIONS: number[] = [];
  CANTON_OPTIONS: Canton[] = [];
  STATUS_OPTIONS: StatementStatus[] = [];
  ttfnValidOn: Date | undefined = undefined;
  statement: TimetableHearingStatement | undefined;
  initialValueForCanton: SwissCanton | null | undefined;
  hearingStatus!: HearingStatus;
  isNew!: boolean;
  form!: FormGroup<StatementDetailFormGroup>;
  isStatementEditable: Observable<boolean | undefined> = of(true);
  uploadedFiles: File[] = [];
  isLoading = false;

  private ngUnsubscribe = new Subject<void>();

  constructor(
    private router: Router,
    private route: ActivatedRoute,
    private dialogService: DialogService,
    private timetableHearingService: TimetableHearingService,
    private notificationService: NotificationService,
    private authService: AuthService,
    private timetableYearChangeService: TimetableYearChangeService,
    private readonly statementDialogService: StatementDialogService,
    private readonly openStatementInMailService: OpenStatementInMailService,
    private readonly statementShareService: StatementShareService
  ) {}

  get isHearingStatusArchived() {
    return TthUtils.isHearingStatusArchived(this.hearingStatus);
  }

  get cantonShort() {
    return Cantons.fromSwissCanton(this.form.value.swissCanton!)!.short;
  }

  get alreadySavedDocuments() {
    const documents = this.form.value.documents as { fileName: string }[];
    return documents.map((doc) => doc.fileName);
  }

  readonly extractEnumCanton = (option: Canton) => option.enumCanton;

  readonly extractShort = (option: Canton) => option.short;

  ngOnInit() {
    this.statement = this.route.snapshot.data.statement;
    this.hearingStatus = this.route.snapshot.data.hearingStatus;
    this.isNew = !this.statement;
    this.uploadedFiles = [];

    if (this.hearingStatus === HearingStatus.Active) {
      this.isStatementEditable = this.timetableHearingService
        .getHearingYears([HearingStatus.Active])
        .pipe(
          map((tthYearContainer) => {
            const containerObjects = tthYearContainer.objects ?? [];
            if (containerObjects.length > 0) {
              return containerObjects[0].statementEditable;
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
    if (!this.isNew && this.initialValueForCanton != this.form.value.swissCanton) {
      this.cantonSelectionChanged();
    } else {
      ValidationService.validateForm(this.form);
      if (this.form.valid) {
        this.form.disable();
        const hearingStatement = this.form.value as TimetableHearingStatement;
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
      this.form.enable({ emitEvent: false });
    }
  }

  backToOverview() {
    this.router.navigate(['..'], { relativeTo: this.route }).then();
  }

  getFormGroup(statement: TimetableHearingStatement | undefined): FormGroup {
    return new FormGroup<StatementDetailFormGroup>({
      id: new FormControl(statement?.id),
      timetableYear: new FormControl(statement?.timetableYear, [Validators.required]),
      statementStatus: new FormControl(statement?.statementStatus, [Validators.required]),
      ttfnid: new FormControl(statement?.ttfnid),
      responsibleTransportCompanies: new FormControl(statement?.responsibleTransportCompanies),
      swissCanton: new FormControl(statement?.swissCanton, [Validators.required]),
      stopPlace: new FormControl(statement?.stopPlace, [
        AtlasFieldLengthValidator.length_50,
        WhitespaceValidator.blankOrEmptySpaceSurrounding,
        AtlasCharsetsValidator.iso88591,
      ]),
      statementSender: new FormGroup<StatementSenderFormGroup>({
        firstName: new FormControl(statement?.statementSender?.firstName, [
          AtlasFieldLengthValidator.length_100,
          AtlasCharsetsValidator.iso88591,
        ]),
        lastName: new FormControl(statement?.statementSender?.lastName, [
          AtlasFieldLengthValidator.length_100,
          AtlasCharsetsValidator.iso88591,
        ]),
        organisation: new FormControl(statement?.statementSender?.organisation, [
          AtlasFieldLengthValidator.length_100,
          AtlasCharsetsValidator.iso88591,
        ]),
        zip: new FormControl(statement?.statementSender?.zip, [
          AtlasCharsetsValidator.numeric,
          Validators.min(1000),
          Validators.max(99999),
        ]),
        city: new FormControl(statement?.statementSender?.city, [
          AtlasFieldLengthValidator.length_50,
          AtlasCharsetsValidator.iso88591,
        ]),
        street: new FormControl(statement?.statementSender?.street, [
          AtlasFieldLengthValidator.length_100,
          AtlasCharsetsValidator.iso88591,
        ]),
        email: new FormControl(statement?.statementSender?.email, [
          Validators.required,
          AtlasFieldLengthValidator.length_100,
          AtlasCharsetsValidator.email,
        ]),
      }),
      statement: new FormControl(statement?.statement, [
        Validators.required,
        AtlasFieldLengthValidator.statement,
        WhitespaceValidator.blankOrEmptySpaceSurrounding,
        AtlasCharsetsValidator.iso88591,
      ]),
      justification: new FormControl(statement?.justification, [
        AtlasFieldLengthValidator.statement,
        WhitespaceValidator.blankOrEmptySpaceSurrounding,
        AtlasCharsetsValidator.iso88591,
      ]),
      comment: new FormControl(statement?.comment, [
        AtlasFieldLengthValidator.length_280,
        WhitespaceValidator.blankOrEmptySpaceSurrounding,
        AtlasCharsetsValidator.iso88591,
      ]),
      documents: new FormArray(
        statement?.documents?.map((document) => new FormControl(document)) ?? []
      ),
      etagVersion: new FormControl(statement?.etagVersion),
    });
  }

  saveButtonDisabled() {
    return !(this.form.dirty || this.uploadedFiles.length > 0);
  }

  removeDocument(fileName: string) {
    const documents = this.form.value.documents as { fileName: string }[];
    const indexOfFile = documents.findIndex((document) => document.fileName === fileName);
    this.form.controls.documents.removeAt(indexOfFile);
    this.form.markAsDirty();
  }

  downloadFile(fileName: string) {
    this.timetableHearingService
      .getStatementDocument(this.statement!.id!, fileName)
      .subscribe((response) => FileDownloadService.downloadFile(fileName, response));
  }

  openAsMail() {
    this.openStatementInMailService.openAsMail(this.statement!, this.ttfnValidOn);
  }

  private downloadLocalFile(
    id: number,
    documents: Array<TimetableHearingStatementDocument> | undefined
  ) {
    if (documents!.length > 0) {
      this.isLoading = true;
      for (let i = 0; i < documents!.length!; i++) {
        this.timetableHearingService
          .getStatementDocument(id, documents![i].fileName)
          .pipe(takeUntil(this.ngUnsubscribe))
          .subscribe((response) => {
            this.uploadedFiles.push(new File([response], documents![i].fileName));
            if (i === documents!.length! - 1) {
              this.isLoading = false;
            }
          });
      }
    }
  }

  private initYearOptions() {
    this.timetableHearingService
      .getHearingYears([HearingStatus.Active, HearingStatus.Planned])
      .subscribe((yearContainer) => {
        let years = yearContainer.objects!.map((year) => year.timetableYear);
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
      const tthPermissions = this.authService.getApplicationUserPermission(
        ApplicationType.TimetableHearing
      );
      if (tthPermissions.role === ApplicationRole.Supervisor || this.authService.isAdmin) {
        this.CANTON_OPTIONS = Cantons.cantons;
      } else if (tthPermissions.role === ApplicationRole.Writer) {
        this.CANTON_OPTIONS = tthPermissions.permissionRestrictions
          .map((restriction) => Cantons.fromSwissCanton(restriction.valueAsString as SwissCanton))
          .filter((element) => element !== undefined)
          .map((e) => e!)
          .sort((n1, n2) => (n1.enumCanton! > n2.enumCanton! ? 1 : -1));
      }
      const defaultCanton = Cantons.getSwissCantonEnum(this.route.snapshot.params.canton);
      if (this.CANTON_OPTIONS.includes(Cantons.fromSwissCanton(defaultCanton)!)) {
        this.form.controls.swissCanton.setValue(defaultCanton);
      }
    } else {
      this.CANTON_OPTIONS = Cantons.cantons;
    }
  }

  private initForm() {
    this.duplicateStatement();
    this.form = this.getFormGroup(this.statement);
    if (!this.isNew) {
      this.initialValueForCanton = this.form.value.swissCanton;
    }
    if (!this.isNew || this.isHearingStatusArchived) {
      this.form.disable();
    }
  }

  private duplicateStatement() {
    if (this.statementShareService.statement) {
      const localCopyStatement = this.statementShareService.statement;
      this.statement = this.statementShareService.getCloneStatement();
      this.downloadLocalFile(localCopyStatement.id!, localCopyStatement.documents);
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
        this.timetableYearChangeService.getTimetableYearChange(year - 1).subscribe((result) => {
          this.ttfnValidOn = result;
        });
      }
    });
  }

  private initResponsibleTransportCompanyPrefill() {
    this.form.controls.ttfnid.valueChanges.subscribe((ttfnid) => {
      if (ttfnid) {
        this.timetableHearingService
          .getResponsibleTransportCompanies(ttfnid, this.form.value.timetableYear! - 1)
          .subscribe((result) => {
            this.form.controls.responsibleTransportCompanies.setValue(result);
          });
      }
    });
  }

  private createStatement(statement: TimetableHearingStatement) {
    this.isLoading = true;
    this.timetableHearingService
      .createStatement(statement, this.uploadedFiles)
      .pipe(takeUntil(this.ngUnsubscribe), catchError(this.handleError()))
      .subscribe((statement) => {
        this.isLoading = false;
        this.notificationService.success('TTH.STATEMENT.NOTIFICATION.ADD_SUCCESS');
        this.navigateToStatementDetail(statement);
      });
  }

  private updateStatement(id: number, statement: TimetableHearingStatement) {
    this.isLoading = true;
    this.timetableHearingService
      .updateHearingStatement(id, statement, this.uploadedFiles)
      .pipe(takeUntil(this.ngUnsubscribe), catchError(this.handleError()))
      .subscribe((statement) => {
        this.isLoading = false;
        this.notificationService.success('TTH.STATEMENT.NOTIFICATION.EDIT_SUCCESS');
        this.navigateToStatementDetail(statement);
      });
  }

  private navigateToStatementDetail(statement: TimetableHearingStatement) {
    this.router
      .navigate(['..', statement.id], { relativeTo: this.route })
      .then(() => this.ngOnInit());
  }

  private handleError() {
    return () => {
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
          this.form.disable({ emitEvent: false });
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
}
