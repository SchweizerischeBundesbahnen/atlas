import { Component, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { VersionsHandlingService } from '../../../../core/versioning/versions-handling.service';
import {
  ApplicationRole,
  ApplicationType,
  CreateServicePointVersion,
  ReadServicePointVersion,
  ServicePointsService,
  Status,
} from '../../../../api';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';
import {
  ServicePointDetailFormGroup,
  ServicePointFormGroupBuilder,
} from './service-point-detail-form-group';
import { MapService } from '../../map/map.service';
import {
  BehaviorSubject,
  catchError,
  EMPTY,
  Observable,
  of,
  Subject,
  take,
} from 'rxjs';
import { Pages } from '../../../pages';
import { DialogService } from '../../../../core/components/dialog/dialog.service';
import { ValidationService } from '../../../../core/validation/validation.service';
import { NotificationService } from '../../../../core/notification/notification.service';
import { DetailFormComponent } from '../../../../core/leave-guard/leave-dirty-form-guard.service';
import { ServicePointAbbreviationAllowList } from './service-point-abbreviation-allow-list';
import {
  GeographyFormGroup,
  GeographyFormGroupBuilder,
} from '../../geography/geography-form-group';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { ValidityService } from '../../validity/validity.service';
import { PermissionService } from '../../../../core/auth/permission/permission.service';
import { AddStopPointWorkflowDialogService } from '../../workflow/add-dialog/add-stop-point-workflow-dialog.service';
import { takeUntil } from 'rxjs/operators';
import { DetailPageContainerComponent } from '../../../../core/components/detail-page-container/detail-page-container.component';
import { AsyncPipe, NgIf } from '@angular/common';
import { SwitchVersionComponent } from '../../../../core/components/switch-version/switch-version.component';
import { NavigationSepodiPrmComponent } from '../../../../core/navigation-sepodi-prm/navigation-sepodi-prm.component';
import { ServicePointFormComponent } from './service-point-form/service-point-form.component';
import { TextFieldComponent } from '../../../../core/form-components/text-field/text-field.component';
import { GeographyComponent } from '../../geography/geography.component';
import { MatDivider } from '@angular/material/divider';
import { UserDetailInfoComponent } from '../../../../core/components/base-detail/user-edit-info/user-detail-info.component';
import { DetailFooterComponent } from '../../../../core/components/detail-footer/detail-footer.component';
import { AtlasButtonComponent } from '../../../../core/components/button/atlas-button.component';
import { TranslatePipe } from '@ngx-translate/core';
import { PrmRecordingObligationComponent } from '../../../../core/prm-recording-obligation/prm-recording-obligation.component';
import { StopPointTerminationDialogService } from './stop-point-termination-dialog/stop-point-termination-dialog.service';
import { StopPointTerminationInfoComponent } from './stop-point-termination-info/stop-point-termination-info.component';
import { TerminationService } from './termination.service';

@Component({
  selector: 'app-service-point',
  templateUrl: './service-point-detail.component.html',
  providers: [ValidityService],
  imports: [
    DetailPageContainerComponent,
    NgIf,
    SwitchVersionComponent,
    NavigationSepodiPrmComponent,
    ServicePointFormComponent,
    TextFieldComponent,
    ReactiveFormsModule,
    GeographyComponent,
    MatDivider,
    UserDetailInfoComponent,
    DetailFooterComponent,
    AtlasButtonComponent,
    AsyncPipe,
    TranslatePipe,
    PrmRecordingObligationComponent,
    StopPointTerminationInfoComponent,
  ],
})
export class ServicePointDetailComponent
  implements OnDestroy, DetailFormComponent
{
  readonly servicePointStatus = Status;

  private onDestroy$ = new Subject<boolean>();

  servicePointVersions!: ReadServicePointVersion[];
  selectedVersion?: ReadServicePointVersion;

  showVersionSwitch = false;
  selectedVersionIndex!: number;
  form?: FormGroup<ServicePointDetailFormGroup>;

  hasAbbreviation = false;
  isAbbreviationAllowed = false;

  isLatestVersionSelected = false;
  preferredId?: number;

  isSwitchVersionDisabled = false;

  _showRevokeButton = false;

  get showRevokeButton(): boolean {
    return this._showRevokeButton;
  }

  set showRevokeButton(show: boolean) {
    this._showRevokeButton = show;
  }

  private _terminationInProgress = false;

  get isTerminationInProgress(): boolean {
    return this._terminationInProgress;
  }

  set terminationInProgress(terminationInProgress: boolean) {
    this._terminationInProgress = terminationInProgress;
  }

  public isFormEnabled$ = new BehaviorSubject<boolean>(false);
  private readonly ZOOM_LEVEL_FOR_DETAIL = 14;
  private _savedGeographyForm?: FormGroup<GeographyFormGroup>;

  constructor(
    private router: Router,
    private route: ActivatedRoute,
    private dialogService: DialogService,
    private servicePointService: ServicePointsService,
    private notificationService: NotificationService,
    private mapService: MapService,
    private permissionService: PermissionService,
    private validityService: ValidityService,
    private addStopPointWorkflowDialogService: AddStopPointWorkflowDialogService,
    private terminationDialogService: StopPointTerminationDialogService,
    private terminationService: TerminationService,
    protected activatedRoute: ActivatedRoute
  ) {
    this.route.parent?.data.pipe(takeUntilDestroyed()).subscribe((next) => {
      this.servicePointVersions = next.servicePoint;
      this.initServicePoint();
      this.displayAndSelectServicePointOnMap();
    });
  }

  onGeographyEnabled() {
    if (this.form && !this.form.controls.servicePointGeolocation) {
      ServicePointFormGroupBuilder.addGroupToForm(
        this.form,
        'servicePointGeolocation',
        this._savedGeographyForm ?? GeographyFormGroupBuilder.buildFormGroup()
      );
      this.form.markAsDirty();
    }
  }

  onGeographyDisabled() {
    if (this.form?.controls.servicePointGeolocation) {
      this._savedGeographyForm = this.form.controls.servicePointGeolocation;
      ServicePointFormGroupBuilder.removeGroupFromForm(
        this.form,
        'servicePointGeolocation'
      );
      this.form.markAsDirty();
    }
  }

  ngOnDestroy() {
    this.mapService.deselectServicePoint();
    this.onDestroy$.next(true);
    this.onDestroy$.complete();
  }

  switchVersion(newIndex: number) {
    this.selectedVersionIndex = newIndex;
    this.initSelectedVersion(this.servicePointVersions[newIndex]);
  }

  closeSidePanel() {
    this.router.navigate([Pages.SEPODI.path]).then();
  }

  private initServicePoint() {
    const queryParamId = this.activatedRoute?.snapshot?.queryParams?.id;
    if (queryParamId) {
      this.preferredId = Number(queryParamId);
    }
    VersionsHandlingService.addVersionNumbers(this.servicePointVersions);
    this.showVersionSwitch = VersionsHandlingService.hasMultipleVersions(
      this.servicePointVersions
    );

    let selectedVersion: ReadServicePointVersion;
    if (this.preferredId) {
      selectedVersion =
        this.servicePointVersions.find((i) => i.id === this.preferredId) ??
        VersionsHandlingService.determineDefaultVersionByValidity(
          this.servicePointVersions
        );
      this.preferredId = undefined;
    } else {
      selectedVersion =
        VersionsHandlingService.determineDefaultVersionByValidity(
          this.servicePointVersions
        );
    }

    this.selectedVersionIndex =
      this.servicePointVersions.indexOf(selectedVersion);
    this.initSelectedVersion(selectedVersion);
  }

  public initSelectedVersion(version: ReadServicePointVersion) {
    this.terminationInProgress = version.terminationInProgress!;
    this.initShowRevokeButton(version);
    this.form = ServicePointFormGroupBuilder.buildFormGroup(version);
    this.disableForm();
    this.isSwitchVersionDisabled = false;
    this.selectedVersion = version;
    this.displayAndSelectServicePointOnMap();
    this.isSelectedVersionHighDate(this.servicePointVersions, version);
    this.checkIfAbbreviationIsAllowed();
    this.hasAbbreviation = !!this.form.controls.abbreviation.value;
    this.terminationService.initTermination(this.form);
  }

  initShowRevokeButton(version: ReadServicePointVersion) {
    this.showRevokeButton = !(
      this.servicePointVersions
        .map((value) => value.status)
        .includes('IN_REVIEW') || version.status === 'REVOKED'
    );
  }

  private displayAndSelectServicePointOnMap() {
    this.mapService.mapInitialized
      .pipe(takeUntil(this.onDestroy$))
      .subscribe((initialized) => {
        if (initialized) {
          if (this.mapService.map.getZoom() <= this.ZOOM_LEVEL_FOR_DETAIL) {
            this.mapService.map.setZoom(this.ZOOM_LEVEL_FOR_DETAIL);
          }
          this.mapService.centerOn(
            this.selectedVersion?.servicePointGeolocation?.wgs84
          );
          this.mapService.displayCurrentCoordinates(
            this.selectedVersion?.servicePointGeolocation?.wgs84
          );
        }
      });
  }

  toggleEdit() {
    if (this.form?.enabled) {
      this.showConfirmationDialog();
    } else {
      this.isSwitchVersionDisabled = true;
      this.enableForm();
      if (this.form?.controls.operatingPointRouteNetwork.value) {
        this.form.controls.operatingPointKilometer.disable();
      }
    }
  }

  showConfirmationDialog() {
    this.confirmLeave()
      .pipe(take(1))
      .subscribe((confirmed) => {
        if (confirmed) {
          this.initSelectedVersion({ ...this.selectedVersion! });
          this.disableForm();
          this.isSwitchVersionDisabled = false;
        }
      });
  }

  private disableForm(): void {
    this.form?.disable({ emitEvent: false });
    this.isFormEnabled$.next(false);
    this._savedGeographyForm = undefined;
  }

  private enableForm(): void {
    this.form?.enable({ emitEvent: false });
    this.isFormEnabled$.next(true);
    this.validityService.initValidity(this.form!);
  }

  confirmLeave(): Observable<boolean> {
    if (this.form?.dirty) {
      return this.dialogService.confirm({
        title: 'DIALOG.DISCARD_CHANGES_TITLE',
        message: 'DIALOG.LEAVE_SITE',
      });
    }
    return of(true);
  }

  private confirmBoTransfer(): Observable<boolean> {
    const currentlySelectedBo = this.form?.controls.businessOrganisation.value;
    const permission = this.permissionService.getApplicationUserPermission(
      ApplicationType.Sepodi
    );
    if (
      !this.permissionService.isAdmin &&
      permission.role == ApplicationRole.Writer &&
      currentlySelectedBo &&
      !PermissionService.getSboidRestrictions(permission).includes(
        currentlySelectedBo
      )
    ) {
      return this.dialogService.confirm({
        title: 'DIALOG.CONFIRM_BO_TRANSFER_TITLE',
        message: 'DIALOG.CONFIRM_BO_TRANSFER',
      });
    }
    return of(true);
  }

  save() {
    ValidationService.validateForm(this.form!);
    if (this.form?.valid) {
      this.validityService.updateValidity(this.form);
      if (this.isStartingTermination(this.form)) {
        this.startTermination();
      } else {
        this.validityService.validateAndDisableCustom(
          () => this.updateVersion(),
          () => this.disableForm()
        );
      }
    }
  }

  private startTermination() {
    this.terminationDialogService
      .openDialog(this.selectedVersion!, this.form!.controls.validTo.value!)
      .subscribe((saved) => {
        if (saved) {
          this.router
            .navigate(['..', this.selectedVersion!.number.number], {
              relativeTo: this.route,
            })
            .then();
        }
      });
  }

  private isStartingTermination(form: FormGroup<ServicePointDetailFormGroup>) {
    return (
      this.isLatestVersionSelected &&
      this.terminationService.isStartingTermination(form)
    );
  }

  update(id: number, servicePointVersion: CreateServicePointVersion) {
    this.confirmBoTransfer()
      .pipe(take(1))
      .subscribe((confirmed) => {
        if (confirmed) {
          this.preferredId = id;
          this.servicePointService
            .updateServicePoint(id, servicePointVersion)
            .pipe(catchError(this.handleError))
            .subscribe(() => {
              this.hasAbbreviation = !!this.form?.controls.abbreviation.value;
              this.notificationService.success(
                'SEPODI.SERVICE_POINTS.NOTIFICATION.EDIT_SUCCESS'
              );
              this.router
                .navigate(['..', this.selectedVersion!.number.number], {
                  relativeTo: this.route,
                })
                .then(() => this.mapService.refreshMap());
            });
        } else {
          this.enableForm();
        }
      });
  }

  private handleError = () => {
    this.enableForm();
    if (this.form?.controls.operatingPointRouteNetwork.value) {
      this.form.controls.operatingPointKilometer.disable();
    }
    return EMPTY;
  };

  checkIfAbbreviationIsAllowed() {
    this.isAbbreviationAllowed = ServicePointAbbreviationAllowList.SBOIDS.some(
      (element) => element.includes(this.selectedVersion!.businessOrganisation)
    );
  }

  isSelectedVersionHighDate(
    servicePointVersions: ReadServicePointVersion[],
    selectedVersion: ReadServicePointVersion
  ) {
    this.isLatestVersionSelected = !servicePointVersions.some(
      (obj) => obj.validTo > selectedVersion.validTo
    );
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
          this.servicePointService
            .revokeServicePoint(this.selectedVersion!.number.number)
            .pipe(catchError(this.handleError))
            .subscribe(() => {
              this.notificationService.success(
                'SEPODI.SERVICE_POINTS.NOTIFICATION.REVOKE_SUCCESS'
              );
              this.router
                .navigate(['..', this.selectedVersion!.number.number], {
                  relativeTo: this.route,
                })
                .then(() => this.mapService.refreshMap());
            });
        }
      });
  }

  validate() {
    this.dialogService
      .confirm({
        title: 'DIALOG.WARNING',
        message: 'DIALOG.VALIDATE',
        cancelText: 'DIALOG.BACK',
        confirmText: 'DIALOG.CONFIRM_VALIDATE',
      })
      .subscribe((confirmed) => {
        if (confirmed) {
          this.servicePointService
            .validateServicePoint(this.selectedVersion!.id!)
            .pipe(catchError(this.handleError))
            .subscribe(() => {
              this.notificationService.success(
                'SEPODI.SERVICE_POINTS.NOTIFICATION.VALIDATE_SUCCESS'
              );
              this.router.navigate(
                ['..', this.selectedVersion!.number.number],
                {
                  relativeTo: this.route,
                }
              );
            });
        }
      });
  }

  updateVersion() {
    const servicePointVersion =
      ServicePointFormGroupBuilder.getWritableServicePoint(this.form!);
    this.update(this.selectedVersion!.id!, servicePointVersion);
  }

  addWorkflow() {
    this.addStopPointWorkflowDialogService.openDialog(this.selectedVersion!);
  }
}
