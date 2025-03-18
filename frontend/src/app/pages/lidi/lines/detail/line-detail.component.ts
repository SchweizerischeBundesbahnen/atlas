import { Component, OnDestroy, OnInit } from '@angular/core';
import {
  AffectedSublinesModel,
  ApplicationRole,
  ApplicationType,
  LinesService,
  LineType,
  LineVersionV2,
  LineVersionWorkflow,
  Status,
  UpdateLineVersionV2,
} from '../../../../api';
import { ActivatedRoute, Router } from '@angular/router';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { DialogService } from '../../../../core/components/dialog/dialog.service';
import { Pages } from '../../../pages';
import {
  LineDetailFormGroup,
  LineFormGroupBuilder,
} from './line-detail-form-group';
import { ValidityService } from '../../../sepodi/validity/validity.service';
import { PermissionService } from '../../../../core/auth/permission/permission.service';
import { catchError, EMPTY, Observable, Subject } from 'rxjs';
import { NotificationService } from '../../../../core/notification/notification.service';
import { DateRange } from '../../../../core/versioning/date-range';
import { VersionsHandlingService } from '../../../../core/versioning/versions-handling.service';
import { ValidationService } from '../../../../core/validation/validation.service';
import { DetailHelperService } from '../../../../core/detail/detail-helper.service';
import { MatDialog } from '@angular/material/dialog';
import { SublineShorteningDialogComponent } from '../../dialog/subline-shortening-dialog/subline-shortening-dialog.component';
import { filter, map, switchMap, takeUntil } from 'rxjs/operators';
import { DetailPageContainerComponent } from '../../../../core/components/detail-page-container/detail-page-container.component';
import { ScrollToTopDirective } from '../../../../core/scroll-to-top/scroll-to-top.directive';
import { DetailPageContentComponent } from '../../../../core/components/detail-page-content/detail-page-content.component';
import { DateRangeTextComponent } from '../../../../core/versioning/date-range-text/date-range-text.component';
import { NgIf } from '@angular/common';
import { SwitchVersionComponent } from '../../../../core/components/switch-version/switch-version.component';
import { SublineTableComponent } from './subline-table/subline-table.component';
import { WorkflowComponent } from '../../../../core/workflow/workflow.component';
import { LinkComponent } from '../../../../core/form-components/link/link.component';
import { LineDetailFormComponent } from './line-detail-form/line-detail-form.component';
import { UserDetailInfoComponent } from '../../../../core/components/base-detail/user-edit-info/user-detail-info.component';
import { DetailFooterComponent } from '../../../../core/components/detail-footer/detail-footer.component';
import { AtlasButtonComponent } from '../../../../core/components/button/atlas-button.component';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
    templateUrl: './line-detail.component.html',
    styleUrls: ['./line-detail.component.scss'],
    providers: [ValidityService],
    imports: [DetailPageContainerComponent, ScrollToTopDirective, DetailPageContentComponent, DateRangeTextComponent, NgIf, SwitchVersionComponent, SublineTableComponent, WorkflowComponent, LinkComponent, LineDetailFormComponent, UserDetailInfoComponent, DetailFooterComponent, AtlasButtonComponent, TranslatePipe]
})
export class LineDetailComponent implements OnInit, OnDestroy {
  private onDestroy$ = new Subject<boolean>();
  eventSubject = new Subject<boolean>();

  selectedVersionIndex!: number;
  selectedVersion!: LineVersionV2;
  versions!: Array<LineVersionV2>;

  form!: FormGroup<LineDetailFormGroup>;
  initForm!: FormGroup<LineDetailFormGroup>;

  isValidFromShortened!: boolean;
  isValidToShortened!: boolean;

  isNew = false;

  showVersionSwitch = false;
  isSwitchVersionDisabled = false;
  showWorkflow = false;

  maxValidity!: DateRange;
  boSboidRestriction: string[] = [];

  isShowLineSnapshotHistory = false;

  _lineType!: LineType;
  get lineType(): LineType {
    return this._lineType;
  }

  set lineType(lineType: LineType) {
    this._lineType = lineType;
  }

  _isLineConcessionTypeRequired = false;

  get isLineConcessionTypeRequired(): boolean {
    return this._isLineConcessionTypeRequired;
  }

  set isLineConcessionTypeRequired(isRequired) {
    this._isLineConcessionTypeRequired = isRequired;
  }

  constructor(
    private router: Router,
    private linesService: LinesService,
    private notificationService: NotificationService,
    private dialogService: DialogService,
    private permissionService: PermissionService,
    private activatedRoute: ActivatedRoute,
    private validityService: ValidityService,
    private detailHelperService: DetailHelperService,
    private dialog: MatDialog
  ) {}

  ngOnInit() {
    this.versions = this.activatedRoute.snapshot.data.lineDetail;
    if (this.versions.length == 0) {
      this.isNew = true;
      this.form = LineFormGroupBuilder.buildFormGroup();
      this.subscribeToConditionalValidation();
    } else {
      this.isNew = false;
      VersionsHandlingService.addVersionNumbers(this.versions);
      this.maxValidity = VersionsHandlingService.getMaxValidity(this.versions);
      this.selectedVersion =
        VersionsHandlingService.determineDefaultVersionByValidity(
          this.versions
        );
      this.selectedVersionIndex = this.versions.indexOf(this.selectedVersion);

      this.initSelectedVersion();
    }

    if (!this.isNew) {
      this.isShowLineSnapshotHistory = this.showSnapshotHistoryLink();

      this.lineType = this.form.value.lineType!;
      this.conditionalValidation();
    }
    this.initBoSboidRestriction();
  }

  ngOnDestroy(): void {
    this.onDestroy$.next(true);
    this.onDestroy$.complete();
  }

  private initSelectedVersion() {
    this.showVersionSwitch = VersionsHandlingService.hasMultipleVersions(
      this.versions
    );
    this.form = LineFormGroupBuilder.buildFormGroup(this.selectedVersion);
    if (!this.isNew) {
      this.form.disable();

      this.showWorkflow =
        this.selectedVersion.lineType === LineType.Orderly &&
        (this.selectedVersion.status === Status.Draft ||
          this.selectedVersion.status === Status.InReview);
    }
  }

  initBoSboidRestriction() {
    if (!this.isNew || this.permissionService.isAdmin) {
      this.boSboidRestriction = [];
    } else {
      const permission = this.permissionService.getApplicationUserPermission(
        ApplicationType.Lidi
      );
      if (permission.role === ApplicationRole.Writer) {
        this.boSboidRestriction =
          PermissionService.getSboidRestrictions(permission);
      } else {
        this.boSboidRestriction = [];
      }
    }
  }

  navigateToSnapshot() {
    this.router
      .navigate([Pages.LIDI.path, Pages.WORKFLOWS.path], {
        queryParams: {
          slnid: this.selectedVersion.slnid,
        },
      })
      .then();
  }

  showSnapshotHistoryLink(): boolean {
    const lineVersionWorkflows: LineVersionWorkflow[] = [];
    this.selectedVersion.lineVersionWorkflows?.forEach((lvw) =>
      lineVersionWorkflows.push(lvw)
    );
    return (
      lineVersionWorkflows.length > 0 ||
      (this.selectedVersion.lineType === LineType.Orderly &&
        this.selectedVersion.status === Status.Validated)
    );
  }

  reloadRecord() {
    this.router
      .navigate([Pages.LIDI.path, Pages.LINES.path, this.selectedVersion.slnid])
      .then(() => this.ngOnInit());
  }

  isEditButtonVisible() {
    return (
      this.selectedVersion.status !== 'IN_REVIEW' ||
      this.permissionService.isAtLeastSupervisor(ApplicationType.Lidi)
    );
  }

  private subscribeToConditionalValidation() {
    this.form.controls.lineType.valueChanges.subscribe(() => {
      this.conditionalValidation();
    });
  }

  private conditionalValidation() {
    if (this.form.controls.lineType.value !== LineType.Orderly) {
      this.isLineConcessionTypeRequired = false;
      this.form.controls.lineConcessionType.clearValidators();
      this.form.controls.lineConcessionType.updateValueAndValidity();
      this.form.controls.swissLineNumber.clearValidators();
      this.form.controls.swissLineNumber.updateValueAndValidity();
    } else {
      this.isLineConcessionTypeRequired = true;
      this.form.controls.lineConcessionType.setValidators([
        Validators.required,
      ]);
      this.form.controls.lineConcessionType.updateValueAndValidity();
      this.form.controls.swissLineNumber.setValidators([Validators.required]);
      this.form.controls.swissLineNumber.updateValueAndValidity();
    }
    this.form.updateValueAndValidity();
  }

  save() {
    this.conditionalValidation();
    ValidationService.validateForm(this.form);
    if (this.form.valid) {
      if (this.isNew) {
        this.form.disable();
        const lineVersion = this.form.getRawValue() as unknown as LineVersionV2;
        this.createLine(lineVersion);
      } else {
        this.validityService.updateValidity(this.form);
        this.validityService.validate().subscribe((confirmed) => {
          if (confirmed) {
            const lineVersion =
              this.form.getRawValue() as unknown as UpdateLineVersionV2;
            this.updateLine(this.selectedVersion.id!, lineVersion);
          }
        });
      }
    }
  }

  createLine(lineVersion: LineVersionV2): void {
    this.form.disable();
    this.linesService
      .createLineVersionV2(lineVersion)
      .pipe(takeUntil(this.onDestroy$), catchError(this.handleError()))
      .subscribe((version) => {
        this.notificationService.success('LIDI.LINE.NOTIFICATION.ADD_SUCCESS');
        this.router
          .navigate([Pages.LIDI.path, Pages.LINES.path, version.slnid])
          .then(() => this.ngOnInit());
      });
  }

  updateLine(id: number, lineVersion: UpdateLineVersionV2): void {
    const defaultSuccessMessage = 'LIDI.LINE.NOTIFICATION.EDIT_SUCCESS';
    this.eventSubject.next(false);
    if (!this.isOnlyValidityChangedToTruncation()) {
      this.updateLineVersion(id, lineVersion, defaultSuccessMessage);
      return;
    }

    this.linesService
      .checkAffectedSublines(id, lineVersion)
      .pipe(
        switchMap((affectedSublines) => {
          if (affectedSublines.affectedSublinesEmpty) {
            this.updateLineVersion(id, lineVersion, defaultSuccessMessage);
            return EMPTY;
          } else {
            const successMessage =
              this.buildSuccessMessageForShortening(affectedSublines);
            return this.openSublineShorteningDialog(
              affectedSublines,
              lineVersion
            ).pipe(
              map((confirmed) => {
                return { confirmed, successMessage };
              })
            );
          }
        }),
        filter(({ confirmed }) => confirmed),
        switchMap(({ successMessage }) => {
          this.updateLineVersion(id, lineVersion, successMessage);
          return EMPTY;
        }),
        takeUntil(this.onDestroy$)
      )
      .subscribe();
  }

  buildSuccessMessageForShortening(affectedSublines: AffectedSublinesModel) {
    if (
      affectedSublines.hasNotAllowedSublinesOnly &&
      !affectedSublines.hasAllowedSublinesOnly
    ) {
      return 'LIDI.LINE.NOTIFICATION.EDIT_SUCCESS';
    }
    return 'LIDI.SUBLINE_SHORTENING.ALLOWED.SUCCESS';
  }

  updateLineVersion(
    id: number,
    lineVersion: UpdateLineVersionV2,
    success: string
  ) {
    this.linesService
      .updateLineVersion(id, lineVersion)
      .pipe(takeUntil(this.onDestroy$), catchError(this.handleError()))
      .subscribe(() => {
        this.notificationService.success(success);
        this.router
          .navigate([
            Pages.LIDI.path,
            Pages.LINES.path,
            this.selectedVersion.slnid,
          ])
          .then(() => {
            this.ngOnInit();
            this.eventSubject.next(true);
          });
      });
  }

  openSublineShorteningDialog(
    affectedSublines: AffectedSublinesModel,
    lineVersion: UpdateLineVersionV2
  ): Observable<boolean> {
    return this.dialog
      .open(SublineShorteningDialogComponent, {
        data: {
          affectedSublines: affectedSublines,
          validFrom: lineVersion.validFrom,
          validTo: lineVersion.validTo,
          isValidFromShortened: this.isValidFromShortened,
          isValidToShortened: this.isValidToShortened,
        },
      })
      .afterClosed()
      .pipe(
        takeUntil(this.onDestroy$),
        map((value) => (value ? value : false))
      );
  }

  revoke(): void {
    this.dialogService
      .confirm({
        title: 'DIALOG.WARNING',
        message: 'DIALOG.REVOKE',
        cancelText: 'DIALOG.BACK',
        confirmText: 'DIALOG.CONFIRM_REVOKE',
      })
      .pipe(takeUntil(this.onDestroy$))
      .subscribe((confirmed) => {
        if (confirmed) {
          if (this.selectedVersion.slnid) {
            this.linesService
              .revokeLine(this.selectedVersion.slnid)
              .subscribe(() => {
                this.notificationService.success(
                  'LIDI.LINE.NOTIFICATION.REVOKE_SUCCESS'
                );
                this.router
                  .navigate([
                    Pages.LIDI.path,
                    Pages.LINES.path,
                    this.selectedVersion.slnid,
                  ])
                  .then(() => this.ngOnInit());
              });
          }
        }
      });
  }

  delete(): void {
    this.dialogService
      .confirm({
        title: 'DIALOG.WARNING',
        message: 'DIALOG.DELETE',
        cancelText: 'DIALOG.BACK',
        confirmText: 'DIALOG.CONFIRM_DELETE',
      })
      .pipe(takeUntil(this.onDestroy$))
      .subscribe((confirmed) => {
        if (confirmed) {
          if (this.selectedVersion.slnid) {
            this.linesService
              .deleteLines(this.selectedVersion.slnid)
              .subscribe(() => {
                this.notificationService.success(
                  'LIDI.LINE.NOTIFICATION.DELETE_SUCCESS'
                );
                this.back();
              });
          }
        }
      });
  }

  back() {
    this.router.navigate(['..'], { relativeTo: this.activatedRoute }).then();
  }

  toggleEdit() {
    if (this.form.enabled) {
      this.detailHelperService.showCancelEditDialog(this);
    } else {
      this.isSwitchVersionDisabled = true;
      this.validityService.initValidity(this.form);
      this.form.enable({ emitEvent: false });

      this.initializeForm(this.form);
      this.conditionalValidation();

      if (this.selectedVersion.status === Status.InReview) {
        this.form.controls.validFrom.disable();
        this.form.controls.validTo.disable();
        this.form.controls.lineType.disable();
      }
    }
  }

  initializeForm(form: FormGroup<LineDetailFormGroup>) {
    this.initForm = new FormGroup<LineDetailFormGroup>({
      swissLineNumber: new FormControl(form.value.swissLineNumber),
      lineType: new FormControl(form.value.lineType),
      offerCategory: new FormControl(form.value.offerCategory),
      businessOrganisation: new FormControl(form.value.businessOrganisation),
      number: new FormControl(form.value.number),
      shortNumber: new FormControl(form.value.shortNumber),
      lineConcessionType: new FormControl(form.value.lineConcessionType),
      longName: new FormControl(form.value.longName),
      description: new FormControl(form.value.description),
      comment: new FormControl(form.value.comment),
      validFrom: new FormControl(form.value.validFrom!),
      validTo: new FormControl(form.value.validTo!),
      etagVersion: new FormControl(form.value.etagVersion),
      creationDate: new FormControl(form.value.creationDate),
      editionDate: new FormControl(form.value.editionDate),
      creator: new FormControl(form.value.creator),
      editor: new FormControl(form.value.editor),
    });
  }

  isOnlyValidityChangedToTruncation() {
    const initForm = { ...this.initForm.value };
    const updatedForm = { ...this.form.value };

    const ignoreFields = ['validFrom', 'validTo'];

    const keysInitForm = Object.keys(initForm)
      .filter((key) => !ignoreFields.includes(key))
      .sort();
    const keysUpdatedForm = Object.keys(updatedForm)
      .filter((key) => !ignoreFields.includes(key))
      .sort();

    let formsEqual = false;

    keysInitForm.forEach((key) => {
      if (keysUpdatedForm.includes(key)) {
        formsEqual = true;
      } else {
        if (
          initForm[key as keyof typeof initForm] ===
          updatedForm[key as keyof typeof updatedForm]
        ) {
          formsEqual = true;
        }
      }
    });

    const validFromShortened = this.form.value.validFrom?.isAfter(
      this.initForm.value.validFrom
    );
    this.isValidFromShortened = validFromShortened!;
    const validToShortened = this.form.value.validTo?.isBefore(
      this.initForm.value.validTo
    );

    this.isValidToShortened = validToShortened!;
    return formsEqual && (validFromShortened || validToShortened);
  }

  switchVersion(newIndex: number) {
    this.selectedVersionIndex = newIndex;
    this.selectedVersion = this.versions[newIndex];
    this.initSelectedVersion();
  }

  private handleError() {
    return () => {
      this.form.enable();
      return EMPTY;
    };
  }

  getPageType() {
    return Pages.LINES;
  }
}
