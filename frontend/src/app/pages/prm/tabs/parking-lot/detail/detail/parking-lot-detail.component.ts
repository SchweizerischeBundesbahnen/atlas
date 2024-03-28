import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {DetailFormComponent} from "../../../../../../core/leave-guard/leave-dirty-form-guard.service";
import {
  ParkingLotVersion,
  PersonWithReducedMobilityService,
  ReadParkingLotVersion,
  ReadServicePointVersion,
} from "../../../../../../api";
import {FormGroup} from "@angular/forms";
import {NotificationService} from "../../../../../../core/notification/notification.service";
import {VersionsHandlingService} from "../../../../../../core/versioning/versions-handling.service";
import {ParkingLotFormGroup, ParkingLotFormGroupBuilder} from "../form/parking-lot-form-group";
import {DateRange} from "../../../../../../core/versioning/date-range";
import {DetailHelperService, DetailWithCancelEdit} from "../../../../../../core/detail/detail-helper.service";
import {take} from "rxjs";
import {Moment} from "moment/moment";
import {ValidityConfirmationService} from "../../../../../sepodi/validity/validity-confirmation.service";

@Component({
  selector: 'app-parking-lot-detail',
  templateUrl: './parking-lot-detail.component.html',
})
export class ParkingLotDetailComponent implements OnInit, DetailFormComponent, DetailWithCancelEdit {
  isNew = false;
  parkingLot: ReadParkingLotVersion[] = [];
  selectedVersion!: ReadParkingLotVersion;

  servicePoint!: ReadServicePointVersion;
  maxValidity!: DateRange;

  form!: FormGroup<ParkingLotFormGroup>;
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

    this.parkingLot = this.route.snapshot.parent!.data.parkingLot;

    this.isNew = this.parkingLot.length === 0;

    if (!this.isNew) {
      VersionsHandlingService.addVersionNumbers(this.parkingLot);
      this.showVersionSwitch = VersionsHandlingService.hasMultipleVersions(this.parkingLot);
      this.maxValidity = VersionsHandlingService.getMaxValidity(this.parkingLot);
      this.selectedVersion = VersionsHandlingService.determineDefaultVersionByValidity(
        this.parkingLot,
      );
      this.selectedVersionIndex = this.parkingLot.indexOf(this.selectedVersion);
    }

    this.initForm();
  }

  private initForm() {
    this.form = ParkingLotFormGroupBuilder.buildFormGroup(this.selectedVersion);

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
    this.selectedVersion = this.parkingLot[newIndex];
    this.initForm();
  }

  back() {
    this.router.navigate(['..'], { relativeTo: this.route.parent }).then();
  }

  toggleEdit() {
    if (this.form.enabled) {
      this.detailHelperService.showCancelEditDialog(this);
    } else {
      this.initValidity();
      this.form.enable();
    }
  }

  save() {
    this.form.markAllAsTouched();
    if (this.form.valid) {
      const parkingLotVersion = ParkingLotFormGroupBuilder.getWritableForm(
        this.form,
        this.servicePoint.sloid!,
      );
      if (this.isNew) {
        this.create(parkingLotVersion);
      } else {
        this.confirmValidity(parkingLotVersion);
      }
    }
  }

  private create(parkingLotVersion: ParkingLotVersion) {
    this.personWithReducedMobilityService
      .createParkingLot(parkingLotVersion)
      .subscribe((createdVersion) => {
        this.notificationService.success('PRM.PARKING_LOTS.NOTIFICATION.ADD_SUCCESS');
        this.router
          .navigate(['..', createdVersion.sloid], {
            relativeTo: this.route.parent,
          })
          .then(() => this.ngOnInit());
      });
  }

  update(parkingLotVersion: ParkingLotVersion) {
    this.personWithReducedMobilityService
      .updateParkingLot(this.selectedVersion.id!, parkingLotVersion)
      .subscribe(() => {
        this.notificationService.success('PRM.PARKING_LOTS.NOTIFICATION.EDIT_SUCCESS');
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

  confirmValidity(parkingLotVersion: ParkingLotVersion){
    this.validityConfirmationService.confirmValidity(
      this.form.controls.validTo.value,
      this.form.controls.validFrom.value,
      this.initValidTo,
      this.initValidFrom
    ).pipe(take(1))
      .subscribe((confirmed) => {
        if (confirmed) {
          this.update(parkingLotVersion);
          this.form.disable();
        }
      });
  }
}
