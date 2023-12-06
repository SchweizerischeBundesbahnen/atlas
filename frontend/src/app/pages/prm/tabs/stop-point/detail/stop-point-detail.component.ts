import { Component, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute, Data, Router } from '@angular/router';
import {
  CreateStopPointVersion,
  PersonWithReducedMobilityService,
  ReadServicePointVersion,
  ReadStopPointVersion,
} from '../../../../../api';
import { BehaviorSubject, Observable, of, Subject, take } from 'rxjs';
import { FormGroup } from '@angular/forms';
import { VersionsHandlingService } from '../../../../../core/versioning/versions-handling.service';
import { takeUntil } from 'rxjs/operators';
import { Pages } from '../../../../pages';
import { NotificationService } from '../../../../../core/notification/notification.service';
import { DialogService } from '../../../../../core/components/dialog/dialog.service';
import { DetailFormComponent } from '../../../../../core/leave-guard/leave-dirty-form-guard.service';
import { AuthService } from '../../../../../core/auth/auth.service';
import {
  StopPointDetailFormGroup,
  StopPointFormGroupBuilder,
} from '../form/stop-point-detail-form-group';

@Component({
  selector: 'app-stop-point-detail',
  templateUrl: './stop-point-detail.component.html',
})
export class StopPointDetailComponent implements OnInit, OnDestroy, DetailFormComponent {
  isNew = false;
  isAuthorizedToCreateStopPoint = true;
  stopPointVersions!: ReadStopPointVersion[];
  servicePointVersion!: ReadServicePointVersion;
  businessOrganisations: string[] = [];
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
    private authService: AuthService,
  ) {}

  ngOnInit(): void {
    this.route.parent?.data.subscribe((data) => {
      this.initServicePointsData(data);
      this.stopPointVersions = data.stopPoints;
      this.initStopPoint();
    });
  }

  initStopPoint() {
    if (this.stopPointVersions.length > 0) {
      this.initExistingStopPoint();
    } else {
      this.initNotExistingStopPoint();
    }
  }

  backToSearchPrm() {
    if (this.form.dirty) {
      this.showConfirmationDialog();
    } else {
      this.navigateToPrmHomeSearch();
    }
  }

  toggleEdit() {
    if (this.form.enabled) {
      this.showConfirmationDialog();
    } else {
      this.enableForm();
    }
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

  initServicePointsData(next: Data) {
    const servicePoints: ReadServicePointVersion[] = next.servicePoints;
    this.businessOrganisations = [
      ...new Set(servicePoints.map((value) => value.businessOrganisation)),
    ];
    this.servicePointVersion =
      VersionsHandlingService.determineDefaultVersionByValidity(servicePoints);
  }

  initNotExistingStopPoint() {
    this.isNew = true;
    if (this.hasPermissionToCreateNewStopPoint()) {
      this.initEmptyForm();
    } else {
      this.isAuthorizedToCreateStopPoint = false;
    }
  }

  hasPermissionToCreateNewStopPoint(): boolean {
    const sboidsPermissions = this.businessOrganisations.map((bo) =>
      this.authService.hasPermissionsToWrite('PRM', bo),
    );
    return sboidsPermissions.includes(true);
  }

  initEmptyForm() {
    this.form = StopPointFormGroupBuilder.buildEmptyWithReducedValidationFormGroup();
    this.form.controls.number.setValue(this.servicePointVersion.number.number);
    this.form.controls.sloid.setValue(this.servicePointVersion.sloid);
    this.disableForm();
  }

  initSelectedVersion() {
    this.form = StopPointFormGroupBuilder.buildFormGroup(this.selectedVersion);
    this.disableForm();
    this.isSelectedVersionHighDate(this.stopPointVersions, this.selectedVersion);
  }

  disableForm(): void {
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

  initExistingStopPoint() {
    this.isNew = false;
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

  enableForm() {
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

  reloadPage() {
    this.router
      .navigate([Pages.PRM.path, Pages.STOP_POINTS.path, this.form.controls.number], {
        relativeTo: this.route,
      })
      .then(() => (this.isNew = false));
  }

  showConfirmationDialog() {
    this.confirmLeave()
      .pipe(take(1))
      .subscribe((confirmed) => {
        if (confirmed) {
          if (this.isNew) {
            this.form.reset();
            this.navigateToPrmHomeSearch();
          } else {
            this.initSelectedVersion();
            this.disableForm();
          }
        }
      });
  }

  navigateToPrmHomeSearch() {
    this.router.navigate([Pages.PRM.path]).then();
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
    return this.form && this.form.dirty;
  }

  ngOnDestroy(): void {
    this.ngUnsubscribe.next();
    this.ngUnsubscribe.complete();
  }
}
