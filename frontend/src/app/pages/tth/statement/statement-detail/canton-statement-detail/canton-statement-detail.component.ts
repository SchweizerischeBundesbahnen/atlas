import { ChangeDetectionStrategy, Component, inject, OnInit, ViewChild } from '@angular/core';
import {
  ApplicationRole,
  ApplicationType,
  HearingStatus,
  StatementStatus,
  SwissCanton,
  TimetableFieldNumber,
  TimetableHearingStatementDocument,
  TimetableHearingStatementV2,
  TransportCompany,
} from '../../../../../api';
import { DialogService } from '../../../../../core/components/dialog/dialog.service';
import { Cantons } from '../../../../../core/cantons/Cantons';
import { ReactiveFormsModule } from '@angular/forms';
import { Canton } from '../../../../../core/cantons/Canton';
import { map, takeUntil } from 'rxjs/operators';
import { catchError, EMPTY, Observable, of, Subject } from 'rxjs';
import { NotificationService } from '../../../../../core/notification/notification.service';
import { ValidationService } from '../../../../../core/validation/validation.service';
import { TthUtils } from '../../../util/tth-utils';
import { OpenStatementInMailService } from '../../open-statement-in-mail.service';
import { StatementShareService } from '../../../overview-detail/statement-share-service';
import { DetailFormComponent } from '../../../../../core/leave-guard/leave-dirty-form-guard.service';
import { TableService } from '../../../../../core/components/table/table.service';
import { addElementsToArrayWhenNotUndefined } from '../../../../../core/util/arrays';
import { PermissionService } from '../../../../../core/auth/permission/permission.service';
import { TimetableYearChangeInternalService } from '../../../../../api/service/lidi/timetable-year-change-internal.service';
import { LoadingSpinnerService } from '../../../../../core/components/loading-spinner/loading-spinner.service';
import { ScrollToTopDirective } from '../../../../../core/scroll-to-top/scroll-to-top.directive';
import { DetailPageContainerComponent } from '../../../../../core/components/detail-page-container/detail-page-container.component';
import { DetailPageContentComponent } from '../../../../../core/components/detail-page-content/detail-page-content.component';
import { AsyncPipe, NgOptimizedImage } from '@angular/common';
import { UserDetailInfoComponent } from '../../../../../core/components/user-edit-info/user-detail-info.component';
import { SelectComponent } from '../../../../../core/form-components/select/select.component';
import { AtlasSpacerComponent } from '../../../../../core/components/spacer/atlas-spacer.component';
import { CommentComponent } from '../../../../../core/form-components/comment/comment.component';
import { AtlasButtonComponent } from '../../../../../core/components/button/atlas-button.component';
import { DetailFooterComponent } from '../../../../../core/components/detail-footer/detail-footer.component';
import { DisplayCantonPipe } from '../../../../../core/cantons/display-canton.pipe';
import { TranslatePipe } from '@ngx-translate/core';
import { StatementTextComponent } from '../../statement-text/statement-text.component';
import { StatementPersonalInformationComponent } from '../../statement-personal-information/statement-personal-information.component';
import { StatementDataComponent } from '../../statement-data/statement-data.component';
import { AtlasLabelFieldComponent } from '@atlas/form';
import { FileComponent } from '../../../../../core/components/file-upload/file/file.component';
import { FileUploadComponent } from '../../../../../core/components/file-upload/file-upload.component';
import { StatementDetailBaseComponent } from '../statement-detail-base.component';
import { TimetableHearingYearInternalService } from '../../../../../api/service/lidi/timetable-hearing-year-internal.service';
import { DialogData } from '../../../../../core/components/dialog/dialog.data';
import { StatementDialogComponent } from '../../statement-dialog/statement.dialog.component';
import { StatementDataProtectionCheckDialogComponent } from './data-protection-check/statement-data-protection-check.dialog.component';
import { Pages } from '../../../../pages';

@Component({
  selector: 'atlas-statement-detail',
  templateUrl: './canton-statement-detail.component.html',
  changeDetection: ChangeDetectionStrategy.Eager,
  styleUrls: ['./canton-statement-detail.component.scss'],
  imports: [
    ScrollToTopDirective,
    DetailPageContainerComponent,
    DetailPageContentComponent,
    ReactiveFormsModule,
    UserDetailInfoComponent,
    SelectComponent,
    AtlasSpacerComponent,
    CommentComponent,
    AtlasButtonComponent,
    DetailFooterComponent,
    DisplayCantonPipe,
    AsyncPipe,
    TranslatePipe,
    NgOptimizedImage,
    StatementTextComponent,
    StatementPersonalInformationComponent,
    StatementDataComponent,
    AtlasLabelFieldComponent,
    FileComponent,
    FileUploadComponent,
  ],
  providers: [OpenStatementInMailService, TranslatePipe],
})
export class CantonStatementDetailComponent
  extends StatementDetailBaseComponent
  implements OnInit, DetailFormComponent
{
  @ViewChild(StatementTextComponent) statementText!: StatementTextComponent;
  YEAR_OPTIONS: number[] = [];
  CANTON_OPTIONS: Canton[] = [];
  STATUS_OPTIONS: StatementStatus[] = [];
  initialValueForCanton: SwissCanton | null | undefined;
  hearingStatus!: HearingStatus;
  isNew!: boolean;
  isStatementEditable: Observable<boolean | undefined> = of(true);
  isDuplicating = false;
  isInitializingComponent = true;

  private readonly ngUnsubscribe = new Subject<void>();

  private readonly loadingSpinnerService = inject(LoadingSpinnerService);
  private readonly dialogService = inject(DialogService);
  private readonly notificationService = inject(NotificationService);
  private readonly permissionService = inject(PermissionService);
  private readonly timetableYearChangeService = inject(TimetableYearChangeInternalService);
  private readonly timetableHearingYearsService = inject(TimetableHearingYearInternalService);
  private readonly openStatementInMailService = inject(OpenStatementInMailService);
  private readonly statementShareService = inject(StatementShareService);
  private readonly tableService = inject(TableService);

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
      this.isStatementEditable = this.timetableHearingYearsService.getHearingYears([HearingStatus.Active]).pipe(
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
  }

  cantonSelectionChanged() {
    this.form.controls.editor.setValue(this.statement?.editor);
    this.form.controls.oldSwissCanton.setValue(this.initialValueForCanton);

    this.dialogService
      .openCustomDataWithConfirmationResult(this.form, StatementDialogComponent)
      .subscribe((confirmed) => {
        if (confirmed) {
          const hearingStatement = this.form.value as TimetableHearingStatementV2;
          this.navigateToStatementDetail(hearingStatement);
        } else {
          this.form.controls.cantonTransferComment.setValue(this.statement?.cantonTransferComment);
        }
      });
  }

  save() {
    if (!this.isNew && this.initialValueForCanton != this.form.value.swissCanton) {
      this.cantonSelectionChanged();
    } else {
      ValidationService.validateForm(this.form);
      if (this.form.valid) {
        const hearingStatement = this.form.value as TimetableHearingStatementV2;
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

  removeDocument(fileName: string) {
    const documents = this.form.value.documents as { fileName: string }[];
    const indexOfFile = documents.findIndex((document) => document.fileName === fileName);
    this.form.controls.documents.removeAt(indexOfFile);
    this.form.markAsDirty();
  }

  openAsMail() {
    this.openStatementInMailService.openAsMail(this.statement!, this.ttfnValidOn);
  }

  downloadLocalFile(id: number, documents: Array<TimetableHearingStatementDocument> | undefined) {
    if (documents!.length > 0) {
      for (let i = 0; i < documents!.length!; i++) {
        this.timetableHearingStatementsService
          .getStatementDocument(id, documents![i].fileName)
          .pipe(takeUntil(this.ngUnsubscribe))
          .subscribe((response) => {
            this.uploadedFiles.push(new File([response], documents![i].fileName));
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
      const tthPermissions = this.permissionService.getApplicationUserPermission(ApplicationType.TimetableHearing);
      if (tthPermissions.role === ApplicationRole.Supervisor || this.permissionService.isAdmin) {
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
    if (this.isDuplicating) {
      this.form.markAsDirty();
    }
    if (!this.isNew) {
      this.initialValueForCanton = this.form.value.swissCanton;
    }
    if (!this.isNew || this.isHearingStatusArchived) {
      this.form.disable();
    }
    this.statementText?.resetForm(this.form);
  }

  duplicateStatement() {
    if (this.statementShareService.statement) {
      this.isDuplicating = true;
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

  private createStatement(statement: TimetableHearingStatementV2) {
    this.loadingSpinnerService.loading.next(true);
    this.timetableHearingStatementsService
      .createStatement(statement, this.uploadedFiles)
      .pipe(takeUntil(this.ngUnsubscribe), catchError(this.handleError()))
      .subscribe((statement) => {
        this.loadingSpinnerService.loading.next(false);
        this.isDuplicating = false;
        this.notificationService.success('TTH.STATEMENT.NOTIFICATION.ADD_SUCCESS');
        this.navigateToStatementDetail(statement);
      });
  }

  private updateStatement(id: number, statement: TimetableHearingStatementV2) {
    this.loadingSpinnerService.loading.next(true);
    this.timetableHearingStatementsService
      .updateHearingStatement(id, statement, this.uploadedFiles)
      .pipe(takeUntil(this.ngUnsubscribe), catchError(this.handleError()))
      .subscribe((statement) => {
        this.statementText.ngOnInit();
        this.loadingSpinnerService.loading.next(false);
        this.notificationService.success('TTH.STATEMENT.NOTIFICATION.EDIT_SUCCESS');
        this.navigateToStatementDetail(statement);
      });
  }

  private navigateToStatementDetail(statement: TimetableHearingStatementV2) {
    this.router.navigate(['..', statement.id], { relativeTo: this.route }).then(() => {
      this.isInitializingComponent = false;
      this.statement = statement;
      this.ngOnInit();
    });
  }

  private handleError() {
    return () => {
      this.loadingSpinnerService.loading.next(false);
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
      return this.dialogService.openDialogDataWithConfirmationResult({
        title: 'DIALOG.DISCARD_CHANGES_TITLE',
        message: 'DIALOG.LEAVE_SITE',
      } satisfies DialogData);
    }
    return of(true);
  }

  next() {
    this.timetableHearingStatementsService.getNextStatement(...this.getAlternationParams()).subscribe((next) => {
      this.tableService.pageIndex = next.pageable.pageNumber!;
      this.navigateToStatementDetail(next.timetableHearingStatement);
    });
  }

  previous() {
    this.timetableHearingStatementsService.getPreviousStatement(...this.getAlternationParams()).subscribe((next) => {
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
    const cantonFilter = Cantons.getSwissCantonFromShort(this.route.snapshot.params.canton);
    return [
      this.statement!.id!,
      this.statement!.timetableYear,
      cantonFilter,
      this.tableService.filterConfig?.filters.chipSearch.getActiveSearch(),
      this.tableService.filterConfig?.filters.multiSelectStatementStatus.getActiveSearch(),
      this.tableService.filterConfig?.filters.searchSelectTTFN.getActiveSearch()?.ttfnid,
      (this.tableService.filterConfig?.filters.searchSelectTU.getActiveSearch() as TransportCompany[])
        ?.map((tu) => tu.id)
        .filter((numberOrUndefined): numberOrUndefined is number => !!numberOrUndefined),
      this.tableService.pageIndex,
      this.tableService.pageSize,
      addElementsToArrayWhenNotUndefined(this.tableService.sortString, 'statementStatus,asc', 'ttfnid,asc', 'id,ASC'),
    ];
  }

  ttfnSelectionChanged(newTtfn?: TimetableFieldNumber) {
    if (newTtfn) {
      this.timetableHearingStatementsService
        .getResponsibleTransportCompanies(newTtfn.ttfnid!, this.form.value.timetableYear! - 1)
        .subscribe((result) => {
          this.form.controls.responsibleTransportCompanies.setValue(result);
        });
    }
  }

  openDataProtectionCheck() {
    this.dialogService
      .openCustomDataWithConfirmationResult(this.statement!, StatementDataProtectionCheckDialogComponent)
      .subscribe((result) => {
        if (result) {
          this.isInitializingComponent = true;
          this.router.navigate(['..', this.statement!.id], { relativeTo: this.route }).then(() => this.ngOnInit());
        }
      });
  }

  goToDossier() {
    this.router
      .navigate(['../..', Pages.TTH_DOSSIERS.path, this.statement!.dossierId], {
        relativeTo: this.route,
      })
      .then();
  }
}
