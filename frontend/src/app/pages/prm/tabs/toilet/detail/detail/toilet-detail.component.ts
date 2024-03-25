import {Component, OnInit} from '@angular/core';
import {VersionsHandlingService} from "../../../../../../core/versioning/versions-handling.service";
import {ToiletFormGroup, ToiletFormGroupBuilder} from "../form/toilet-form-group";
import {
  PersonWithReducedMobilityService,
  ReadServicePointVersion,
  ReadToiletVersion,
  StopPointVersion,
  ToiletVersion
} from "../../../../../../api";
import {DetailFormComponent} from "../../../../../../core/leave-guard/leave-dirty-form-guard.service";
import {DateRange} from "../../../../../../core/versioning/date-range";
import {FormGroup} from "@angular/forms";
import {ActivatedRoute, Router} from "@angular/router";
import {NotificationService} from "../../../../../../core/notification/notification.service";
import {DetailHelperService, DetailWithCancelEdit} from "../../../../../../core/detail/detail-helper.service";
import {take} from "rxjs";
import {Moment} from "moment/moment";
import {ValidityConfirmationService} from "../../../../../sepodi/validity/validity-confirmation.service";

@Component({
  selector: 'app-toilet-detail',
  templateUrl: './toilet-detail.component.html',
})
export class ToiletDetailComponent implements OnInit, DetailFormComponent, DetailWithCancelEdit {

  isNew = false;
  toiletVersions: ReadToiletVersion[] = [];
  selectedVersion!: ReadToiletVersion;

  servicePoint!: ReadServicePointVersion;
  maxValidity!: DateRange;

  form!: FormGroup<ToiletFormGroup>;
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

    this.toiletVersions = this.route.snapshot.parent!.data.toilet;

    this.isNew = this.toiletVersions.length === 0;

    if (!this.isNew) {
      VersionsHandlingService.addVersionNumbers(this.toiletVersions);
      this.showVersionSwitch = VersionsHandlingService.hasMultipleVersions(this.toiletVersions);
      this.maxValidity = VersionsHandlingService.getMaxValidity(this.toiletVersions);
      this.selectedVersion = VersionsHandlingService.determineDefaultVersionByValidity(
        this.toiletVersions,
      );
      this.selectedVersionIndex = this.toiletVersions.indexOf(this.selectedVersion);
    }

    this.initForm();
  }

  private initForm() {
    this.form = ToiletFormGroupBuilder.buildFormGroup(this.selectedVersion);

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
  }

  switchVersion(newIndex: number) {
    this.selectedVersionIndex = newIndex;
    this.selectedVersion = this.toiletVersions[newIndex];
    this.initForm();
  }

  back() {
    this.router.navigate(['..'], { relativeTo: this.route.parent }).then();
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
      const toiletVersion = ToiletFormGroupBuilder.getWritableForm(
        this.form,
        this.servicePoint.sloid!,
      );
      if (this.isNew) {
        this.create(toiletVersion);
      } else {
        this.confirmValidity(toiletVersion)
      }
    }
  }

  private create(toiletVersion: ToiletVersion) {
    this.personWithReducedMobilityService
      .createToiletVersion(toiletVersion)
      .subscribe((createdVersion) => {
        this.notificationService.success('PRM.TOILETS.NOTIFICATION.ADD_SUCCESS');
        this.router
          .navigate(['..', createdVersion.sloid], {
            relativeTo: this.route.parent,
          })
          .then(() => this.ngOnInit());
      });
  }

  private update(toiletVersion: ToiletVersion) {
    this.personWithReducedMobilityService
      .updateToiletVersion(this.selectedVersion.id!, toiletVersion)
      .subscribe(() => {
        this.notificationService.success('PRM.TOILETS.NOTIFICATION.EDIT_SUCCESS');
        this.router
          .navigate(['..', this.selectedVersion.sloid], {
            relativeTo: this.route.parent,
          })
          .then(() => this.ngOnInit());
      });
  }

  initValidity(){
    this.initValidTo = this.form?.value.validTo;
    this.initValidFrom = this.form?.value.validFrom;
  }

  confirmValidity(toiletVersion: ToiletVersion){
    this.validityConfirmationService.confirmValidity(
      this.form.controls.validTo.value,
      this.form.controls.validFrom.value,
      this.initValidTo,
      this.initValidFrom
    ).pipe(take(1))
      .subscribe((confirmed) => {
        if (confirmed) {
          this.update(toiletVersion);
          this.form.disable();
        }
      });
  }
}
