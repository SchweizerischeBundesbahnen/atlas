import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Data, Router } from '@angular/router';
import {
  BehaviorSubject,
  catchError,
  EMPTY,
  finalize,
  from,
  Observable,
  of,
  switchMap,
  take,
} from 'rxjs';
import { FormGroup } from '@angular/forms';
import { VersionsHandlingService } from '../../../../../core/versioning/versions-handling.service';
import { Pages } from '../../../../pages';
import { NotificationService } from '../../../../../core/notification/notification.service';
import { DialogService } from '../../../../../core/components/dialog/dialog.service';
import { DetailFormComponent } from '../../../../../core/leave-guard/leave-dirty-form-guard.service';
import {
  StopPointDetailFormGroup,
  StopPointFormGroupBuilder,
} from '../form/stop-point-detail-form-group';
import { PrmTabsService } from '../../../prm-panel/prm-tabs.service';
import {
  PersonWithReducedMobilityService,
  ReadServicePointVersion,
  ReadStopPointVersion,
  StopPointVersion,
} from '../../../../../api';
import { PrmMeanOfTransportHelper } from '../../../util/prm-mean-of-transport-helper';
import { ValidityService } from '../../../../sepodi/validity/validity.service';
import { ReferencePointCreationHintService } from './reference-point-creation-hint/reference-point-creation-hint.service';
import { PermissionService } from '../../../../../core/auth/permission/permission.service';
import { filter, map } from 'rxjs/operators';

@Component({
  selector: 'app-stop-point-detail',
  templateUrl: './stop-point-detail.component.html',
  providers: [ValidityService],
})
export class StopPointDetailComponent implements OnInit, DetailFormComponent {
  isNew = false;
  isAuthorizedToCreateStopPoint = true;
  stopPointVersions!: ReadStopPointVersion[];
  servicePointVersion!: ReadServicePointVersion;
  businessOrganisations: string[] = [];
  selectedVersionIndex!: number;
  selectedVersion!: ReadStopPointVersion;
  form!: FormGroup<StopPointDetailFormGroup>;
  showVersionSwitch = false;
  isSwitchVersionDisabled = false;
  preferredId?: number;
  public isFormEnabled$ = new BehaviorSubject<boolean>(false);
  isReduced!: boolean | undefined;
  saving = false;

  constructor(
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly personWithReducedMobilityService: PersonWithReducedMobilityService,
    private readonly notificationService: NotificationService,
    private readonly dialogService: DialogService,
    private readonly permissionService: PermissionService,
    private readonly prmTabsService: PrmTabsService,
    private readonly referencePointCreationHintService: ReferencePointCreationHintService,
    private readonly validityService: ValidityService,
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
    if (this.form && this.form.dirty) {
      this.showConfirmationDialog();
    } else {
      this.navigateToPrmHomeSearch();
    }
  }

  toggleEdit() {
    if (this.form.enabled) {
      this.showConfirmationDialog();
    } else {
      this.validityService.initValidity(this.form);
      this.enableForm();
    }
  }

  switchVersion(newIndex: number) {
    this.selectedVersionIndex = newIndex;
    this.selectedVersion = this.stopPointVersions[newIndex];
    this.initSelectedVersion();
  }

  save(): void {
    this.saving = true;
    this.saveProcess()
      .pipe(
        take(1),
        catchError(() => {
          this.ngOnInit();
          return EMPTY;
        }),
        finalize(() => (this.saving = false)),
      )
      .subscribe();
  }

  private saveProcess(): Observable<ReadStopPointVersion | ReadStopPointVersion[]> {
    this.form.markAllAsTouched();
    if (this.form.valid) {
      const writableStopPoint = StopPointFormGroupBuilder.getWritableStopPoint(this.form);
      if (this.isNew) {
        this.disableForm();
        return this.createStopPoint(writableStopPoint);
      } else {
        this.validityService.updateValidity(this.form);
        return this.validityService.validate().pipe(
          switchMap((dialogRes) => {
            if (dialogRes) {
              this.disableForm();
              return this.updateStopPoint(writableStopPoint);
            } else {
              return EMPTY;
            }
          }),
        );
      }
    } else {
      return EMPTY;
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
      this.permissionService.hasPermissionsToWrite('PRM', bo),
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
  }

  disableForm(): void {
    this.form.disable({ emitEvent: false });
    this.isFormEnabled$.next(false);
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

  updateStopPoint(writableStopPoint: StopPointVersion) {
    const isEditedReduced = PrmMeanOfTransportHelper.isReduced(writableStopPoint.meansOfTransport);
    const isCurrentReduced = this.selectedVersion.reduced;
    if (isEditedReduced !== isCurrentReduced) {
      return this.showPrmChangeVariantConfirmationDialog().pipe(
        filter((res) => res),
        switchMap(() => this.doUpdateStopPoint(writableStopPoint)),
      );
    } else {
      return this.doUpdateStopPoint(writableStopPoint);
    }
  }

  showPrmChangeVariantConfirmationDialog() {
    return this.confirmPrmVariantChange().pipe(take(1));
  }

  doUpdateStopPoint(writableStopPoint: StopPointVersion) {
    return this.personWithReducedMobilityService
      .updateStopPoint(this.selectedVersion.id!, writableStopPoint)
      .pipe(
        switchMap((updatedVersions) => {
          this.notificationService.success('PRM.STOP_POINTS.NOTIFICATION.EDIT_SUCCESS');
          return this.reloadPage().pipe(map(() => updatedVersions));
        }),
      );
  }

  private createStopPoint(writableStopPoint: StopPointVersion) {
    return this.personWithReducedMobilityService.createStopPoint(writableStopPoint).pipe(
      switchMap((stopPoint) => {
        this.notificationService.success('PRM.STOP_POINTS.NOTIFICATION.ADD_SUCCESS');
        this.prmTabsService.initTabs([stopPoint]);
        if (!stopPoint.reduced) {
          this.referencePointCreationHintService.showHint();
        }
        return this.reloadPage().pipe(map(() => stopPoint));
      }),
    );
  }

  private reloadPage() {
    return from(
      this.router.navigate([Pages.PRM.path, Pages.STOP_POINTS.path, this.form.controls.number], {
        relativeTo: this.route,
      }),
    );
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

  private confirmPrmVariantChange(): Observable<boolean> {
    return this.dialogService.confirm({
      title: 'PRM.DIALOG.PRM_VARIANT_CHANGES_TITLE',
      message: 'PRM.DIALOG.PRM_VARIANT_CHANGES_MSG',
    });
  }
}
