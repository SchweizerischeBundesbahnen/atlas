import { Component, OnInit } from '@angular/core';
import {
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
import { FormGroup, Validators } from '@angular/forms';
import { DialogService } from '../../../../core/components/dialog/dialog.service';
import { Pages } from '../../../pages';
import {
  LineDetailFormGroup,
  LineFormGroupBuilder,
} from './line-detail-form-group';
import { ValidityService } from '../../../sepodi/validity/validity.service';
import { PermissionService } from '../../../../core/auth/permission/permission.service';
import { catchError, EMPTY } from 'rxjs';
import { NotificationService } from '../../../../core/notification/notification.service';
import { DateRange } from '../../../../core/versioning/date-range';
import { VersionsHandlingService } from '../../../../core/versioning/versions-handling.service';
import { ValidationService } from '../../../../core/validation/validation.service';
import { DetailHelperService } from '../../../../core/detail/detail-helper.service';

@Component({
  templateUrl: './line-detail.component.html',
  styleUrls: ['./line-detail.component.scss'],
  providers: [ValidityService],
})
export class LineDetailComponent implements OnInit {
  selectedVersionIndex!: number;
  selectedVersion!: LineVersionV2;
  versions!: Array<LineVersionV2>;

  form!: FormGroup<LineDetailFormGroup>;

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

  _isLineConcessionTypeRequired = true;
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
    private detailHelperService: DetailHelperService
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
      if (this.form.controls.lineType.value !== LineType.Orderly) {
        this.isLineConcessionTypeRequired = false;
      }
    }
    this.initBoSboidRestriction();
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
    ValidationService.validateForm(this.form);
    if (this.form.valid) {
      if (this.isNew) {
        this.form.disable();
        const lineVersion =
          this.form.getRawValue() as unknown as LineVersionV2;
        this.createLine(lineVersion);
      } else {
        this.validityService.updateValidity(this.form);
        this.validityService.validate().subscribe((confirmed) => {
          if (confirmed) {
            this.form.disable();
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
      .pipe(catchError(this.handleError()))
      .subscribe((version) => {
        this.notificationService.success('LIDI.LINE.NOTIFICATION.ADD_SUCCESS');
        this.router
          .navigate([Pages.LIDI.path, Pages.LINES.path, version.slnid])
          .then(() => this.ngOnInit());
      });
  }

  updateLine(id: number, lineVersion: UpdateLineVersionV2): void {
    this.linesService
      .updateLineVersion(id, lineVersion)
      .pipe(catchError(this.handleError()))
      .subscribe(() => {
        this.notificationService.success('LIDI.LINE.NOTIFICATION.EDIT_SUCCESS');
        this.router
          .navigate([
            Pages.LIDI.path,
            Pages.LINES.path,
            this.selectedVersion.slnid,
          ])
          .then(() => this.ngOnInit());
      });
  }

  revoke(): void {
    this.dialogService
      .confirm({
        title: 'DIALOG.WARNING',
        message: 'DIALOG.REVOKE',
        cancelText: 'DIALOG.BACK',
        confirmText: 'DIALOG.CONFIRM_REVOKE',
      })
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

      if (this.selectedVersion.status === Status.InReview) {
        this.form.controls.validFrom.disable();
        this.form.controls.validTo.disable();
        this.form.controls.lineType.disable();
      }
    }
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
