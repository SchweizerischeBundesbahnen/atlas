import { Directive, OnInit } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { Record } from './record';
import { DialogService } from '../dialog/dialog.service';
import { EMPTY, Observable, of, Subject } from 'rxjs';
import { Page } from '../../model/page';
import { NotificationService } from '../../notification/notification.service';
import { ApplicationRole, ApplicationType, Status } from '../../../api';
import { AuthService } from '../../auth/auth.service';
import { ValidationService } from '../../validation/validation.service';
import { ActivatedRoute, Router } from '@angular/router';
import { DetailFormComponent } from '../../leave-guard/leave-dirty-form-guard.service';
import { VersionsHandlingService } from '../../versioning/versions-handling.service';
import { DateRange } from '../../versioning/date-range';

@Directive()
export abstract class BaseDetailController<TYPE extends Record>
  implements OnInit, DetailFormComponent
{
  record!: TYPE;
  selectedRecordChange = new Subject<Record>();
  records!: Array<TYPE>;
  form!: FormGroup;
  switchedIndex!: number | undefined;
  showSwitch: boolean | undefined;
  switchVersionEvent = new Subject<Record>();
  maxValidity!: DateRange;

  protected constructor(
    protected router: Router,
    protected dialogService: DialogService,
    protected notificationService: NotificationService,
    protected authService: AuthService,
    protected activatedRoute: ActivatedRoute,
  ) {}

  get versionNumberOfCurrentRecord(): number {
    return this.record.versionNumber!;
  }

  get statusOfCurrentRecord(): Status {
    return this.record.status!;
  }

  ngOnInit(): void {
    this.init();
    this.showSwitch = VersionsHandlingService.hasMultipleVersions(this.records);
  }

  evaluateSelectedRecord(records: Array<TYPE>) {
    const preferredSelectionId = Number(this.activatedRoute.snapshot.queryParams.id);
    if (this.isVersionSwitched() && this.switchedIndex !== undefined) {
      return records[this.switchedIndex];
    } else if (preferredSelectionId) {
      const preferredRecord = records.filter((record) => record.id === preferredSelectionId);
      if (preferredRecord.length == 1) {
        return preferredRecord[0];
      } else {
        return this.getActualRecord(records);
      }
    } else {
      return this.getActualRecord(records);
    }
  }

  //override me in the child component
  isWorkflowable(): boolean {
    return false;
  }

  getSelectedRecord(): TYPE {
    return this.record;
  }

  setSelectedRecord(record: TYPE) {
    this.record = record;
    this.selectedRecordChange.next(record);
  }

  getId(): number {
    return this.record.id!;
  }

  isNewRecord() {
    return !this.getId();
  }

  isExistingRecord() {
    return !this.isNewRecord();
  }

  switchVersion(index: number) {
    this.switchedIndex = index;
    if (this.form.enabled) {
      this.showConfirmationDialog();
    } else {
      this.ngOnInit();
    }
  }

  toggleEdit() {
    if (this.form.enabled) {
      this.showConfirmationDialog();
    } else {
      this.form.enable();
      this.disableUneditableFormFields();
    }
  }

  save() {
    ValidationService.validateForm(this.form);
    this.switchedIndex = undefined;
    if (this.form.valid) {
      this.form.disable();
      if (this.getId()) {
        this.confirmBoTransfer().subscribe((confirmed) => {
          if (confirmed) {
            this.updateRecord();
          } else {
            this.form.enable();
          }
        });
      } else {
        this.createRecord();
      }
    }
  }

  revoke() {
    this.dialogService
      .confirm({
        title: 'DIALOG.WARNING',
        message: 'DIALOG.REVOKE',
        cancelText: 'DIALOG.BACK',
        confirmText: 'DIALOG.CONFIRM_REVOKE',
      })
      .subscribe((confirmed) => {
        if (confirmed) {
          this.revokeRecord();
        }
      });
  }

  delete() {
    this.dialogService
      .confirm({
        title: 'DIALOG.WARNING',
        message: 'DIALOG.DELETE',
        cancelText: 'DIALOG.BACK',
        confirmText: 'DIALOG.CONFIRM_DELETE',
      })
      .subscribe((confirmed) => {
        if (confirmed) {
          this.deleteRecord();
        }
      });
  }

  getActualRecord(records: Array<TYPE>): TYPE {
    return VersionsHandlingService.determineDefaultVersionByValidity(records);
  }

  abstract getDetailHeading(record: TYPE): string;

  abstract getDetailSubheading(record: TYPE): string;

  abstract readRecord(): TYPE;

  abstract getFormGroup(record: TYPE): FormGroup;

  abstract updateRecord(): void;

  abstract createRecord(): void;

  abstract revokeRecord(): void;

  abstract deleteRecord(): void;

  abstract getPageType(): Page;

  reloadRecord(): void {
    throw new Error('You have to override me');
  }

  getDescriptionForWorkflow(): string {
    throw new Error('You have to override me');
  }

  abstract getApplicationType(): ApplicationType;

  backToOverview(): void {
    this.form.reset();
    this.router.navigate(['..'], { relativeTo: this.activatedRoute }).then();
  }

  closeConfirmDialog(): void {
    this.dialogService.closeConfirmDialog();
  }

  getBoSboidRestriction() {
    if (this.isExistingRecord() || this.authService.isAdmin) {
      return [];
    }
    const permission = this.authService.getApplicationUserPermission(this.getApplicationType());
    if (permission.role === ApplicationRole.Writer) {
      return AuthService.getSboidRestrictions(permission);
    }
    return [];
  }

  protected handleError = () => {
    this.form.enable();
    return EMPTY;
  };

  protected getFormControlsToDisable(): string[] {
    return [];
  }

  private init() {
    this.getRecord();
    if (this.records) {
      VersionsHandlingService.addVersionNumbers(this.records);
      this.maxValidity = VersionsHandlingService.getMaxValidity(this.records);
    }
    this.form = this.getFormGroup(this.record);
    this.switchVersionEvent.next(this.record);
    if (this.isExistingRecord()) {
      this.form.disable();
    } else {
      this.form.enable();
    }
  }

  private getRecord() {
    const records = this.readRecord();

    //if is a version/s already persist get switched or actual version and fill the Form
    if (Array.isArray(records) && records.length > 0) {
      this.records = records;
      this.sortRecords();
      this.setSelectedRecord(this.evaluateSelectedRecord(this.records));
    } else {
      //is creating a new version, prepare empty Form
      this.setSelectedRecord(records);
    }
  }

  private isVersionSwitched() {
    return this.switchedIndex !== undefined && this.switchedIndex >= 0;
  }

  private showConfirmationDialog() {
    this.confirmLeave().subscribe((confirmed) => {
      if (confirmed) {
        if (this.isNewRecord()) {
          this.backToOverview();
        } else {
          this.form.disable();
          this.ngOnInit();
        }
      }
    });
  }

  private sortRecords() {
    VersionsHandlingService.sortByValidFrom(this.records);
  }

  private disableUneditableFormFields(): void {
    const formControlsToDisable = this.getFormControlsToDisable();
    formControlsToDisable.forEach((ctrl) => this.form.get(ctrl)?.disable());
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

  private confirmBoTransfer(): Observable<boolean> {
    const currentlySelectedBo = this.form.value.businessOrganisation;
    const permission = this.authService.getApplicationUserPermission(this.getApplicationType());
    if (
      !this.authService.isAdmin &&
      permission.role == ApplicationRole.Writer &&
      currentlySelectedBo &&
      !AuthService.getSboidRestrictions(permission).includes(currentlySelectedBo)
    ) {
      return this.dialogService.confirm({
        title: 'DIALOG.CONFIRM_BO_TRANSFER_TITLE',
        message: 'DIALOG.CONFIRM_BO_TRANSFER',
      });
    }
    return of(true);
  }

  isFormDirty() {
    return this.form.dirty;
  }
}
