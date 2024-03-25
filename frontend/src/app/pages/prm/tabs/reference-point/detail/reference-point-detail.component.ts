import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {VersionsHandlingService} from '../../../../../core/versioning/versions-handling.service';
import {CompleteReferencePointFormGroup, ReferencePointFormGroupBuilder,} from './form/reference-point-form-group';
import {DetailFormComponent} from '../../../../../core/leave-guard/leave-dirty-form-guard.service';
import {DateRange} from '../../../../../core/versioning/date-range';
import {FormGroup} from '@angular/forms';
import {NotificationService} from '../../../../../core/notification/notification.service';
import {
  PersonWithReducedMobilityService,
  ReadReferencePointVersion,
  ReadServicePointVersion,
  ReferencePointVersion, StopPointVersion,
} from '../../../../../api';
import {DetailHelperService, DetailWithCancelEdit} from "../../../../../core/detail/detail-helper.service";
import {take} from "rxjs";
import {Moment} from "moment/moment";
import {ValidityConfirmationService} from "../../../../sepodi/validity/validity-confirmation.service";

@Component({
  selector: 'app-reference-point',
  templateUrl: './reference-point-detail.component.html',
})
export class ReferencePointDetailComponent implements OnInit, DetailFormComponent, DetailWithCancelEdit {
  isNew = false;
  referencePoint: ReadReferencePointVersion[] = [];
  selectedVersion!: ReadReferencePointVersion;

  servicePoint!: ReadServicePointVersion;
  maxValidity!: DateRange;

  form!: FormGroup<CompleteReferencePointFormGroup>;
  showVersionSwitch = false;
  selectedVersionIndex!: number;

  businessOrganisations: string[] = [];

  initValidFrom!: Moment | null | undefined;
  initValidTo!: Moment | null | undefined;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private personWithReducedMobilityService: PersonWithReducedMobilityService,
    private notificationService: NotificationService,
    private detailHelperService: DetailHelperService,
    private validityConfirmationService: ValidityConfirmationService
  ) {}

  ngOnInit(): void {
    this.initSePoDiData();

    this.referencePoint = this.route.snapshot.data.referencePoint;

    this.isNew = this.referencePoint.length === 0;

    if (!this.isNew) {
      VersionsHandlingService.addVersionNumbers(this.referencePoint);
      this.showVersionSwitch = VersionsHandlingService.hasMultipleVersions(this.referencePoint);
      this.maxValidity = VersionsHandlingService.getMaxValidity(this.referencePoint);
      this.selectedVersion = VersionsHandlingService.determineDefaultVersionByValidity(
        this.referencePoint,
      );
      this.selectedVersionIndex = this.referencePoint.indexOf(this.selectedVersion);
    }

    this.initForm();
  }

  private initForm() {
    this.form = ReferencePointFormGroupBuilder.buildCompleteFormGroup(this.selectedVersion);

    if (!this.isNew) {
      this.form.disable();
    }
  }

  private initSePoDiData() {
    const servicePointVersions: ReadServicePointVersion[] = this.route.snapshot.data.servicePoint;
    this.servicePoint =
      VersionsHandlingService.determineDefaultVersionByValidity(servicePointVersions);
    this.businessOrganisations = [
      ...new Set(servicePointVersions.map((value) => value.businessOrganisation)),
    ];
  }

  switchVersion(newIndex: number) {
    this.selectedVersionIndex = newIndex;
    this.selectedVersion = this.referencePoint[newIndex];
    this.initForm();
  }

  back() {
    this.router.navigate(['..'], { relativeTo: this.route }).then();
  }

  toggleEdit() {
    if (this.form.enabled) {
      this.detailHelperService.showCancelEditDialog(this);
    } else {
      this.initValidity()
      this.form.enable();
    }
  }

  save() {
    this.form.markAllAsTouched();
    if (this.form.valid) {
      const referencePointVersion = ReferencePointFormGroupBuilder.getWritableForm(
        this.form,
        this.servicePoint.sloid!,
      );
      if (this.isNew) {
        this.create(referencePointVersion);
      } else {
       this.confirmValidity(referencePointVersion)
      }
    }
  }

  private create(referencePointVersion: ReferencePointVersion) {
    this.personWithReducedMobilityService
      .createReferencePoint(referencePointVersion)
      .subscribe((createdVersion) => {
        this.notificationService.success('PRM.REFERENCE_POINTS.NOTIFICATION.ADD_SUCCESS');
        this.router
          .navigate(['..', createdVersion.sloid], {
            relativeTo: this.route,
          })
          .then(() => this.ngOnInit());
      });
  }

  private update(referencePointVersion: ReferencePointVersion) {
    this.personWithReducedMobilityService
      .updateReferencePoint(this.selectedVersion.id!, referencePointVersion)
      .subscribe(() => {
        this.notificationService.success('PRM.REFERENCE_POINTS.NOTIFICATION.EDIT_SUCCESS');
        this.router
          .navigate(['..', this.selectedVersion.sloid], {
            relativeTo: this.route,
          })
          .then(() => this.ngOnInit());
      });
  }

  initValidity(){
    this.initValidTo = this.form?.value.validTo;
    this.initValidFrom = this.form?.value.validFrom;
  }

  confirmValidity(referencePointVersion: ReferencePointVersion){
    this.validityConfirmationService.confirmValidity(
      this.form.controls.validTo.value,
      this.form.controls.validFrom.value,
      this.initValidTo,
      this.initValidFrom
    ).pipe(take(1))
      .subscribe((confirmed) => {
        if (confirmed) {
          this.update(referencePointVersion);
          this.form.disable();
        }
      });
  }
}
