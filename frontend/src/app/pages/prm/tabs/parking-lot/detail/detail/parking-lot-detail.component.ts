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
import {ValidityService} from "../../../../../sepodi/validity/validity.service";
import {catchError, EMPTY} from "rxjs";

@Component({
  selector: 'app-parking-lot-detail',
  templateUrl: './parking-lot-detail.component.html',
  providers: [ValidityService]
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

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private personWithReducedMobilityService: PersonWithReducedMobilityService,
    private notificationService: NotificationService,
    private detailHelperService: DetailHelperService,
    private validityService: ValidityService
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
      this.validityService.initValidity(this.form);
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
        this.form.disable();
      } else {
        this.validityService.updateValidity(this.form);
        this.validityService.validateAndDisableForm(() => this.update(parkingLotVersion), this.form);
      }
    }
  }

  private create(parkingLotVersion: ParkingLotVersion) {
    this.personWithReducedMobilityService
      .createParkingLot(parkingLotVersion)
      .pipe(catchError(() => {
        this.ngOnInit();
        return EMPTY;
      }))
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
      .pipe(catchError(() => {
        this.ngOnInit();
        return EMPTY;
      }))
      .subscribe(() => {
        this.notificationService.success('PRM.PARKING_LOTS.NOTIFICATION.EDIT_SUCCESS');
        this.router
          .navigate(['..', this.selectedVersion.sloid], {
            relativeTo: this.route.parent,
          })
          .then(() => this.ngOnInit());
      });
  }
}
