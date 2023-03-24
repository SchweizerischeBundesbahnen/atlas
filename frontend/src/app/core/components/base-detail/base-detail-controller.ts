import { Directive, OnInit } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { Record } from './record';
import { DialogService } from '../dialog/dialog.service';
import { EMPTY, Observable, of, Subject } from 'rxjs';
import moment from 'moment';
import { Page } from '../../model/page';
import { NotificationService } from '../../notification/notification.service';
import { DateService } from '../../date/date.service';
import { ApplicationRole, ApplicationType, Status } from '../../../api';
import { MatDialogRef } from '@angular/material/dialog';
import { AuthService } from '../../auth/auth.service';
import { ValidationService } from '../../validation/validation.service';
import { ActivatedRoute } from '@angular/router';

@Directive()
export abstract class BaseDetailController<TYPE extends Record> implements OnInit {
  record!: TYPE;
  selectedRecordChange = new Subject<Record>();
  records!: Array<TYPE>;
  form!: FormGroup;
  switchedIndex!: number | undefined;
  showSwitch: boolean | undefined;
  switchVersionEvent = new Subject<Record>();

  protected constructor(
    protected dialogRef: MatDialogRef<any>,
    protected dialogService: DialogService,
    protected notificationService: NotificationService,
    protected authService: AuthService,
    protected activatedRoute: ActivatedRoute
  ) {}

  get versionNumberOfCurrentRecord(): number {
    return this.record.versionNumber!;
  }

  get statusOfCurrentRecord(): Status {
    return this.record.status!;
  }

  ngOnInit(): void {
    this.init();
    this.showSwitch = !!Array.isArray(this.records);
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

  getStartDate() {
    this.sortRecords();
    return DateService.getDateFormatted(this.records[0].validFrom);
  }

  getEndDate() {
    this.sortRecords();
    return DateService.getDateFormatted(this.records[this.records.length - 1].validTo);
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
    if (records.length == 1) {
      return records[0];
    }
    const now = moment();
    const matchedRecord = this.findRecordByTodayDate(records, now);
    if (matchedRecord.length == 1) {
      return matchedRecord[0];
    } else if (matchedRecord.length > 1) {
      throw new Error('Something went wrong. Found more than one Record.');
    } else if (matchedRecord.length == 0 && records.length > 1) {
      const foundRecordBetweenGap = this.findRecordBetweenGap(records, now);
      if (foundRecordBetweenGap != null) {
        return foundRecordBetweenGap;
      }
      //get next in future
      const firstIndexValidFrom = moment(records[0].validFrom);
      if (now.isBefore(firstIndexValidFrom)) {
        return records[0];
      }
      //get last in passt
      const lastIndexValidTo = moment(records[records.length - 1].validTo);
      if (now.isAfter(lastIndexValidTo)) {
        return records[records.length - 1];
      }
    }
    return records[0];
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
    new Error('You have to override me');
  }

  getDecriptionForWorkflow(): string {
    throw new Error('You have to override me');
  }

  abstract getApplicationType(): ApplicationType;

  backToOverview(): void {
    this.dialogRef.close();
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
      this.records.forEach((item, index) => (item.versionNumber = index + 1));
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

  private findRecordByTodayDate(records: Array<TYPE>, now: moment.Moment) {
    return records.filter((record) => {
      const currentValidFrom = moment(record.validFrom);
      const currentValidTo = moment(record.validTo);
      if (currentValidFrom.isSame(currentValidTo, 'day') && now.isSame(currentValidFrom, 'day')) {
        return true;
      }
      return now.isBetween(currentValidFrom, currentValidTo);
    });
  }

  private findRecordBetweenGap(records: Array<TYPE>, now: moment.Moment) {
    const startRecordsDateRange = records[0].validFrom;
    const endRecordsDateRange = records[records.length - 1].validTo;
    if (now.isBetween(startRecordsDateRange, endRecordsDateRange)) {
      for (let i = 1; i < records.length; i++) {
        const currentValidTo = moment(records[i - 1].validTo);
        const nextValidFrom = moment(records[i].validFrom);
        if (now.isBetween(currentValidTo, nextValidFrom)) {
          return records[i];
        }
      }
    }
    return null;
  }

  private sortRecords() {
    this.records.sort((x, y) => +new Date(x.validFrom!) - +new Date(y.validFrom!));
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
}
