import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {DetailFormComponent} from "../../../../../../core/leave-guard/leave-dirty-form-guard.service";
import {
  PersonWithReducedMobilityService,
  PlatformVersion,
  ReadPlatformVersion,
  ReadServicePointVersion,
  ReadStopPointVersion,
  ReadTrafficPointElementVersion,
} from "../../../../../../api";
import {FormGroup} from "@angular/forms";
import {NotificationService} from "../../../../../../core/notification/notification.service";
import {PrmMeanOfTransportHelper} from "../../../../util/prm-mean-of-transport-helper";
import {VersionsHandlingService} from "../../../../../../core/versioning/versions-handling.service";
import {
  CompletePlatformFormGroup,
  PlatformFormGroupBuilder,
  ReducedPlatformFormGroup
} from "../form/platform-form-group";
import {DateRange} from "../../../../../../core/versioning/date-range";
import {DetailHelperService, DetailWithCancelEdit} from "../../../../../../core/detail/detail-helper.service";
import {ValidityService} from "../../../../../sepodi/validity/validity.service";
import {PermissionService} from "../../../../../../core/auth/permission/permission.service";
import {catchError, EMPTY} from "rxjs";
import {Pages} from "../../../../../pages";
import {NavigationToPage} from "../../../../../../core/navigation-sepodi-prm/navigation-sepodi-prm.component";

@Component({
  selector: 'app-platforms',
  templateUrl: './platform-detail.component.html',
  providers: [ValidityService]
})
export class PlatformDetailComponent implements OnInit, DetailFormComponent, DetailWithCancelEdit {
  protected readonly NavigationToPage = NavigationToPage;

  isNew = false;
  platform: ReadPlatformVersion[] = [];
  selectedVersion!: ReadPlatformVersion;

  servicePoint!: ReadServicePointVersion;
  trafficPoint!: ReadTrafficPointElementVersion;
  maxValidity!: DateRange;
  stopPoint!: ReadStopPointVersion[];
  businessOrganisations: string[] = [];

  reduced = false;
  form!: FormGroup<ReducedPlatformFormGroup> | FormGroup<CompletePlatformFormGroup>;
  showVersionSwitch = false;
  selectedVersionIndex!: number;
  mayCreate = true;

  get reducedForm(): FormGroup<ReducedPlatformFormGroup> {
    return this.form as FormGroup<ReducedPlatformFormGroup>;
  }

  get completeForm(): FormGroup<CompletePlatformFormGroup> {
    return this.form as FormGroup<CompletePlatformFormGroup>;
  }

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private personWithReducedMobilityService: PersonWithReducedMobilityService,
    private notificationService: NotificationService,
    private detailHelperService: DetailHelperService,
    private permissionService: PermissionService,
    private validityService: ValidityService
  ) {}

  ngOnInit(): void {
    this.initSePoDiData();
    this.stopPoint = this.route.snapshot.parent!.data.stopPoint;
    this.platform = this.route.snapshot.parent!.data.platform;

      this.reduced = PrmMeanOfTransportHelper.isReduced(this.stopPoint[0].meansOfTransport);
      this.isNew = this.platform.length === 0;

      if (this.isNew) {
        this.mayCreate = this.hasPermissionToCreateNewStopPoint();
      } else {
        VersionsHandlingService.addVersionNumbers(this.platform);
        this.showVersionSwitch = VersionsHandlingService.hasMultipleVersions(this.platform);
        this.maxValidity = VersionsHandlingService.getMaxValidity(this.platform);
        this.selectedVersion = VersionsHandlingService.determineDefaultVersionByValidity(
          this.platform,
        );
        this.selectedVersionIndex = this.platform.indexOf(this.selectedVersion);
      }

      this.initForm();
  }

  private initForm() {
    if (this.reduced) {
      this.form = PlatformFormGroupBuilder.buildReducedFormGroup(this.selectedVersion);
    } else {
      this.form = PlatformFormGroupBuilder.buildCompleteFormGroup(this.selectedVersion);
    }
    this.form.controls.sloid.setValue(this.trafficPoint.sloid);

    if (!this.isNew) {
      this.form.disable();
    }
  }

  private initSePoDiData() {
    const servicePointVersions: ReadServicePointVersion[] = this.route.snapshot.parent!.data.servicePoint;
    this.servicePoint =
      VersionsHandlingService.determineDefaultVersionByValidity(servicePointVersions);
    this.businessOrganisations = [
      ...new Set(servicePointVersions.map((value) => value.businessOrganisation)),
    ];
    this.trafficPoint = VersionsHandlingService.determineDefaultVersionByValidity(
      this.route.snapshot.parent!.data.trafficPoint,
    );
  }

  hasPermissionToCreateNewStopPoint(): boolean {
    const sboidsPermissions = this.businessOrganisations.map((bo) =>
      this.permissionService.hasPermissionsToWrite('PRM', bo),
    );
    return sboidsPermissions.includes(true);
  }

  switchVersion(newIndex: number) {
    this.selectedVersionIndex = newIndex;
    this.selectedVersion = this.platform[newIndex];
    this.initForm();
  }

  back() {
    this.router.navigate(['..'], {relativeTo: this.route.parent}).then();
  }

  toggleEdit() {
    if (this.form.enabled) {
      this.detailHelperService.showCancelEditDialog(this);
    } else {
      this.validityService.initValidity(this.form)
      this.form.enable();
    }
  }

  save() {
    this.form.markAllAsTouched();
    if (this.form.valid) {
      const platformVersion = PlatformFormGroupBuilder.getWritableForm(
        this.form,
        this.servicePoint.sloid!,
        this.reduced,
      );
      if (this.isNew) {
        this.create(platformVersion);
        this.form.disable();
      } else {
        this.validityService.updateValidity(this.form);
        this.validityService.validateAndDisableForm(() => this.update(platformVersion), this.form);
      }
    }
  }

  private create(platformVersion: PlatformVersion) {
    this.personWithReducedMobilityService.createPlatform(platformVersion)
      .pipe(catchError(() => {
        this.ngOnInit();
        return EMPTY;
      }))
      .subscribe(() => {
        this.notificationService.success('PRM.PLATFORMS.NOTIFICATION.ADD_SUCCESS');
        this.reloadPage();
      });
  }

  update(platformVersion: PlatformVersion) {
    this.personWithReducedMobilityService
      .updatePlatform(this.selectedVersion.id!, platformVersion)
      .pipe(catchError(() => {
        this.ngOnInit();
        return EMPTY;
      }))
      .subscribe(() => {
        this.notificationService.success('PRM.PLATFORMS.NOTIFICATION.EDIT_SUCCESS');
        this.reloadPage();
      });
  }

  reloadPage() {
    this.router
      .navigate(['..', this.trafficPoint.sloid], {
        relativeTo: this.route.parent,
      })
      .then(() => this.ngOnInit());
  }
}
