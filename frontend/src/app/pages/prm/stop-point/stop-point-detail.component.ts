import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import {
  CreateStopPointVersion,
  PersonWithReducedMobilityService,
  ReadServicePointVersion,
  ReadStopPointVersion,
} from '../../../api';
import { BehaviorSubject, Observable, of, Subject, Subscription, take } from 'rxjs';
import { FormGroup } from '@angular/forms';
import {
  StopPointDetailFormGroup,
  StopPointFormGroupBuilder,
} from './form/stop-point-detail-form-group';
import { VersionsHandlingService } from '../../../core/versioning/versions-handling.service';
import { takeUntil } from 'rxjs/operators';
import { Pages } from '../../pages';
import { NotificationService } from '../../../core/notification/notification.service';
import { DialogService } from '../../../core/components/dialog/dialog.service';
import { DetailFormComponent } from '../../../core/leave-guard/leave-dirty-form-guard.service';

@Component({
  selector: 'app-stop-point-detail',
  templateUrl: './stop-point-detail.component.html',
  styleUrls: ['./stop-point-detail.component.scss'],
})
export class StopPointDetailComponent implements OnInit, DetailFormComponent {
  isNew = false;
  stopPointVersions!: ReadStopPointVersion[];
  selectedVersionIndex!: number;
  selectedVersion!: ReadStopPointVersion;
  form!: FormGroup<StopPointDetailFormGroup>;
  isLatestVersionSelected = false;
  showVersionSwitch = false;
  isSwitchVersionDisabled = false;
  preferredId?: number;
  private ngUnsubscribe = new Subject<void>();
  public isFormEnabled$ = new BehaviorSubject<boolean>(false);
  isReduced!: boolean | undefined;

  constructor(
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly personWithReducedMobilityService: PersonWithReducedMobilityService,
    private notificationService: NotificationService,
    private dialogService: DialogService,
  ) {}

  private stopPointSubscription?: Subscription;

  ngOnInit(): void {
    this.stopPointSubscription = this.route.data
      .pipe(takeUntil(this.ngUnsubscribe))
      .subscribe((next) => {
        this.stopPointVersions = next.stopPoint;
        if (this.stopPointVersions.length > 0) {
          this.initExistingStopPoint();
        } else {
          this.initNotExistingStopPoint();
        }
      });
  }

  backToSearchPrm() {
    this.router.navigate([Pages.PRM.path]).then();
  }

  toggleEdit() {
    if (this.form.enabled) {
      this.showConfirmationDialog();
    } else {
      this.enableForm();
    }
  }

  private initNotExistingStopPoint() {
    this.isNew = true;
    this.route.parent?.data.subscribe((next) => {
      const servicePointVersion: ReadServicePointVersion =
        VersionsHandlingService.determineDefaultVersionByValidity(next.servicePoints);
      this.form = StopPointFormGroupBuilder.buildEmptyWithReducedValidationFormGroup();
      this.form.controls.number.setValue(servicePointVersion.number.number);
      this.form.controls.sloid.setValue(servicePointVersion.sloid);
      this.disableForm();
    });
  }

  switchVersion(newIndex: number) {
    this.selectedVersionIndex = newIndex;
    this.selectedVersion = this.stopPointVersions[newIndex];
    this.initSelectedVersion();
  }

  save() {
    this.form.markAllAsTouched();
    if (this.form.valid) {
      const writableStopPoint = StopPointFormGroupBuilder.getWritableStopPoint(this.form);
      if (!this.isNew) {
        this.updateStopPoint(writableStopPoint);
      } else {
        this.createStopPoint(writableStopPoint);
      }
    }
  }

  private initSelectedVersion() {
    this.form = StopPointFormGroupBuilder.buildFormGroup(this.selectedVersion);
    this.disableForm();
    this.isSelectedVersionHighDate(this.stopPointVersions, this.selectedVersion);
  }

  private disableForm(): void {
    this.form.disable({ emitEvent: false });
    this.isFormEnabled$.next(false);
  }

  private isSelectedVersionHighDate(
    stopPointVersions: ReadStopPointVersion[],
    selectedVersion: ReadStopPointVersion,
  ) {
    this.isLatestVersionSelected = !stopPointVersions.some(
      (obj) => obj.validTo > selectedVersion.validTo,
    );
  }

  private initExistingStopPoint() {
    VersionsHandlingService.addVersionNumbers(this.stopPointVersions);
    this.showVersionSwitch = VersionsHandlingService.hasMultipleVersions(this.stopPointVersions);
    if (this.preferredId) {
      this.selectedVersion =
        this.stopPointVersions.find((i) => i.id === this.preferredId) ??
        VersionsHandlingService.determineDefaultVersionByValidity(this.stopPointVersions);
      this.preferredId = undefined;
    } else {
      this.selectedVersion = VersionsHandlingService.determineDefaultVersionByValidity(
        this.stopPointVersions,
      );
    }
    this.isReduced = this.selectedVersion.reduced;
    this.selectedVersionIndex = this.stopPointVersions.indexOf(this.selectedVersion);
    this.initSelectedVersion();
    this.disableForm();
  }

  private enableForm() {
    this.form.enable({ emitEvent: false });
    this.isFormEnabled$.next(true);
  }

  private updateStopPoint(writableStopPoint: CreateStopPointVersion) {
    this.personWithReducedMobilityService
      .updateStopPoint(this.selectedVersion.id!, writableStopPoint)
      .pipe(takeUntil(this.ngUnsubscribe))
      .subscribe(() => {
        this.notificationService.success('PRM.STOP_POINTS.NOTIFICATION.EDIT_SUCCESS');
        this.reloadPage();
      });
  }

  private createStopPoint(writableStopPoint: CreateStopPointVersion) {
    this.personWithReducedMobilityService
      .createStopPoint(writableStopPoint)
      .pipe(takeUntil(this.ngUnsubscribe))
      .subscribe(() => {
        this.notificationService.success('PRM.STOP_POINTS.NOTIFICATION.ADD_SUCCESS');
        this.reloadPage();
      });
  }

  private reloadPage() {
    this.router
      .navigate([Pages.PRM.path, Pages.STOP_POINTS.path, this.form.controls.number], {
        relativeTo: this.route,
      })
      .then(() => (this.isNew = false));
  }

  private showConfirmationDialog() {
    this.confirmLeave()
      .pipe(take(1))
      .subscribe((confirmed) => {
        if (confirmed) {
          if (this.isNew) {
            this.backToSearchPrm();
          } else {
            this.initSelectedVersion();
            this.disableForm();
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

  //used in combination with canLeaveDirtyForm
  isFormDirty(): boolean {
    return this.form.dirty;
  }
}
