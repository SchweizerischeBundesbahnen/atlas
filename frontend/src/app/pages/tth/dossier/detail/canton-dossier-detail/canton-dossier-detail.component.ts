import { Component, inject, OnInit } from '@angular/core';
import { DetailFormComponent } from '../../../../../core/leave-guard/leave-dirty-form-guard.service';
import { FormGroup, FormsModule, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { TthDossier } from '../../../../../api/model/tthDossier';
import { DetailPageContainerComponent } from '../../../../../core/components/detail-page-container/detail-page-container.component';
import { DetailPageContentComponent } from '../../../../../core/components/detail-page-content/detail-page-content.component';
import { ScrollToTopDirective } from '../../../../../core/scroll-to-top/scroll-to-top.directive';
import { DetailFooterComponent } from '../../../../../core/components/detail-footer/detail-footer.component';
import { DossierDetailFormGroup, DossierFormGroupBuilder } from '../dossier-detail-form-group';
import { AtlasLabelFieldComponent } from '@atlas/form';
import { TextFieldComponent } from '../../../../../core/form-components/text-field/text-field.component';
import { TranslatePipe } from '@ngx-translate/core';
import { AtlasSpacerComponent } from '../../../../../core/components/spacer/atlas-spacer.component';
import { AtlasButtonComponent } from '../../../../../core/components/button/atlas-button.component';
import { DateComponent } from '../../../../../core/form-components/date/date.component';
import { CommentComponent } from '../../../../../core/form-components/comment/comment.component';
import {
  DetailDialogHelperService,
  DetailWithCancelEdit,
} from '../../../../../core/detail/detail-dialog-helper.service';
import { ValidationService } from '../../../../../core/validation/validation.service';
import { DossierInternalService } from '../../../../../api/service/workflow/dossier-internal.service';
import { catchError, EMPTY, forkJoin, mergeMap, of } from 'rxjs';
import { NotificationService } from '../../../../../core/notification/notification.service';
import { toNumberArrayStrict } from '../../../../../core/util/arrays';
import { StatementSelectComponent } from '../../statement-select/statement-select.component';
import { SwissCanton } from '../../../../../api';
import { TimetableHearingStatementInternalService } from '../../../../../api/service/lidi/timetable-hearing-statement-internal.service';
import { AtlasFieldErrorComponent } from '../../../../../core/form-components/atlas-field-error/atlas-field-error.component';
import { UserSelectComponent } from '../../../../user-administration/user/user-select/user-select.component';
import { Cantons } from '../../../../../core/cantons/Cantons';
import { DossierStatus } from '../../../../../api/model/dossierStatus';
import { SelectComponent } from '../../../../../core/form-components/select/select.component';
import { DialogService } from '../../../../../core/components/dialog/dialog.service';
import { OpenCantonDossierInMailService } from './open-canton-dossier-in-mail.service';
import { StatementSelectDialogComponent } from '../../statement-select/dialog/statement-select-dialog.component';
import { StatementSelectData } from '../../statement-select-data';

export const DOSSIER_EDITABLE_STATES = [
  DossierStatus.Added,
  DossierStatus.DossierCantonCheck,
  DossierStatus.Accepted,
  DossierStatus.Rejected,
  DossierStatus.Moved,
];

@Component({
  selector: 'atlas-dossier-detail',
  imports: [
    DetailPageContainerComponent,
    DetailPageContentComponent,
    ScrollToTopDirective,
    DetailFooterComponent,
    AtlasLabelFieldComponent,
    TextFieldComponent,
    TranslatePipe,
    AtlasSpacerComponent,
    AtlasButtonComponent,
    FormsModule,
    ReactiveFormsModule,
    DateComponent,
    CommentComponent,
    StatementSelectComponent,
    AtlasFieldErrorComponent,
    UserSelectComponent,
    SelectComponent,
  ],
  providers: [OpenCantonDossierInMailService, TranslatePipe],
  templateUrl: './canton-dossier-detail.component.html',
  styleUrls: ['./canton-dossier-detail.component.scss'],
})
export class CantonDossierDetailComponent implements DetailFormComponent, DetailWithCancelEdit, OnInit {
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly detailHelperService = inject(DetailDialogHelperService);
  private readonly dossierInternalService = inject(DossierInternalService);
  private readonly notificationService = inject(NotificationService);
  private readonly timetableHearingStatementInternalService = inject(TimetableHearingStatementInternalService);
  private readonly dialogService = inject(DialogService);
  private readonly openDossierInMailService = inject(OpenCantonDossierInMailService);

  readonly editableStates = DOSSIER_EDITABLE_STATES;
  readonly cancelableStates = [DossierStatus.Added];
  readonly dissolvableStates = [DossierStatus.Accepted, DossierStatus.Rejected, DossierStatus.Moved];
  readonly formStatusOptions: DossierStatus[] = [DossierStatus.Accepted, DossierStatus.Rejected, DossierStatus.Moved];

  form!: FormGroup<DossierDetailFormGroup>;
  currentDossier?: TthDossier;
  isNew = false;
  swissCanton?: SwissCanton;

  get cantonShort() {
    return Cantons.fromSwissCanton(this.swissCanton)?.short;
  }
  timetableHearingYear!: number;

  private _selectedStatements!: number[];

  get selectedStatements(): number[] {
    return this._selectedStatements;
  }

  set selectedStatements(value: number[]) {
    this._selectedStatements = value;
    this.form.controls.statementIds.setValue(value);
    this.form.controls.statementIds.markAsDirty();
  }

  ngOnInit() {
    this.currentDossier = this.activatedRoute.snapshot.data.dossier;
    this.form = DossierFormGroupBuilder.buildFormGroup(this.currentDossier);
    if (this.currentDossier) {
      this.isNew = false;
      this.selectedStatements = this.currentDossier.statementIds;
      this.form.controls.statementIds.markAsPristine();
      this.form.disable();
    } else {
      this.isNew = true;

      this.selectedStatements = toNumberArrayStrict(this.activatedRoute.snapshot.queryParams?.statementIds);
    }

    this.loadCantonAndYear();
  }

  private loadCantonAndYear() {
    this.timetableHearingStatementInternalService.getStatement(this.selectedStatements[0]).subscribe((statement) => {
      this.swissCanton = statement.swissCanton;
      this.form.controls.swissCanton.setValue(this.swissCanton);
      this.timetableHearingYear = statement.timetableYear!;
    });
  }

  get isEditable(): boolean {
    return !!this.currentDossier && this.editableStates.includes(this.currentDossier.dossierStatus!);
  }

  toggleEdit() {
    if (this.form.enabled) {
      this.detailHelperService.showCancelEditDialog(this);
    } else {
      this.form.enable();
      if (this.form.controls.answerToCanton.value) {
        this.form.controls.question.disable();
        this.form.controls.boContactMail.disable();
        this.form.controls.boDeadlineToAnswer.disable();
      }
      this.form.controls.answerToCanton.disable();
    }
  }

  back() {
    this.router.navigate(['..'], { relativeTo: this.activatedRoute }).then();
  }

  save() {
    ValidationService.validateForm(this.form);
    if (this.form.valid) {
      const dossier = DossierFormGroupBuilder.getDossier(this.form);
      this.form.disable();
      if (this.isNew) {
        this.createDossier(dossier);
      } else {
        this.updateDossier(dossier);
      }
    }
  }

  private createDossier(dossier: TthDossier) {
    this.dossierInternalService
      .createDossier(dossier)
      .pipe(catchError(this.handleError()))
      .subscribe((dossier) => {
        this.notificationService.success('TTH.DOSSIER.NOTIFICATION.ADD_SUCCESS');
        this.router
          .navigate(['..', dossier.id], {
            relativeTo: this.activatedRoute,
            queryParams: {},
          })
          .then(() => this.ngOnInit());
      });
  }

  private updateDossier(dossier: TthDossier) {
    this.dossierInternalService
      .updateDossier(dossier)
      .pipe(catchError(this.handleError()))
      .subscribe((dossier) => {
        this.notificationService.success('TTH.DOSSIER.NOTIFICATION.EDIT_SUCCESS');
        this.goToDossier(dossier.id!);
      });
  }

  private goToDossier(dossierId: number) {
    this.router
      .navigate(['..', dossierId], {
        relativeTo: this.activatedRoute,
        queryParams: {},
      })
      .then(() => this.ngOnInit());
  }

  private handleError() {
    return () => {
      this.form.enable();
      return EMPTY;
    };
  }

  openAddStatementsDialog() {
    const dialogData: StatementSelectData = {
      title: 'TTH.DIALOG.STATUS_CHANGE',
      message: 'TTH.DIALOG.STATUS_CHANGE',
      cancelText: 'COMMON.CANCEL',
      confirmText: 'COMMON.APPLY',
      selectedStatements: this.selectedStatements,
      swissCanton: this.swissCanton!,
      timetableHearingYear: this.timetableHearingYear,
    };

    this.dialogService
      .openDialogDataWithCustomResult<StatementSelectData, number[]>(dialogData, StatementSelectDialogComponent)
      .subscribe((selected) => {
        if (selected) {
          this.selectedStatements = selected;
          this.form.controls.statementIds.markAsDirty();
        }
      });
  }

  get isSendableToBo(): boolean {
    return (
      this.currentDossier?.dossierStatus === DossierStatus.Added &&
      !!this.form.controls.boContactMail.value &&
      !!this.form.controls.boDeadlineToAnswer.value &&
      !!this.form.controls.question.value
    );
  }

  sendToBo() {
    this.performDataProtectionInfo().subscribe((confirmed) => {
      if (confirmed) {
        this.doSendToBo();
      }
    });
  }

  private performDataProtectionInfo() {
    return forkJoin(
      this.selectedStatements.map((id) => this.timetableHearingStatementInternalService.getStatement(id))
    ).pipe(
      mergeMap((statements) => {
        const allStatementsDataProtected = statements.every((statement) => statement.dataProtectionChecked!);
        if (allStatementsDataProtected) {
          return of(true);
        } else {
          return this.dialogService.openDialogDataWithConfirmationResult({
            title: 'TTH.STATEMENT.DATA_PROTECTION',
            message: 'TTH.DOSSIER.DATA_PROTECTION_NOT_COMPLETED',
          });
        }
      })
    );
  }

  private doSendToBo() {
    this.dossierInternalService.sendDossierToBo(this.currentDossier!.id!).subscribe(() => {
      this.notificationService.success('TTH.DOSSIER.NOTIFICATION.SENT_TO_BO');
      this.goToDossier(this.currentDossier!.id!);
    });
  }

  completeDossier(status: DossierStatus) {
    this.dialogService
      .openDialogDataWithConfirmationResult({
        title: 'TTH.DOSSIER.NOTIFICATION.COMPLETE_TITLE',
        message: 'TTH.DOSSIER.NOTIFICATION.COMPLETE_MESSAGE',
        confirmText: 'DIALOG.OK',
        cancelText: 'DIALOG.CANCEL',
      })
      .subscribe((confirmed) => {
        if (confirmed) {
          this.dossierInternalService.completeDossier(this.currentDossier!.id!, status).subscribe(() => {
            this.notificationService.success('TTH.DOSSIER.NOTIFICATION.EDIT_SUCCESS');
            this.goToDossier(this.currentDossier!.id!);
          });
        }
      });
  }

  openInternalFeedbackMail() {
    this.openDossierInMailService.openDossierInMailClient({
      topic: this.currentDossier!.topic,
      statementIds: this.selectedStatements,
      question: this.form.controls.question.value,
      answer: this.form.controls.answerToCanton.value,
      internalComment: this.form.controls.internalComment.value,
      publicComment: this.form.controls.publicComment.value,
    });
  }
}
