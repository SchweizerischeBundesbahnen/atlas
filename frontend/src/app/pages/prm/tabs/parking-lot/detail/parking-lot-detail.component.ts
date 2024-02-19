import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {VersionsHandlingService} from '../../../../../core/versioning/versions-handling.service';
import {ParkingLotFormGroup, ParkingLotFormGroupBuilder,} from './form/parking-lot-form-group';
import {Observable, of, take} from 'rxjs';
import {DetailFormComponent} from '../../../../../core/leave-guard/leave-dirty-form-guard.service';
import {DateRange} from '../../../../../core/versioning/date-range';
import {FormGroup} from '@angular/forms';
import {NotificationService} from '../../../../../core/notification/notification.service';
import {DialogService} from '../../../../../core/components/dialog/dialog.service';
import {
  ParkingLotVersion,
  PersonWithReducedMobilityService,
  ReadParkingLotVersion,
  ReadServicePointVersion,
} from '../../../../../api';

@Component({
  selector: 'app-parking-lot',
  templateUrl: './parking-lot-detail.component.html',
  styleUrls: ['./parking-lot-detail.component.scss'],
})
export class ParkingLotDetailComponent implements OnInit, DetailFormComponent {
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
    private dialogService: DialogService,
  ) {}

  ngOnInit(): void {
    this.initSePoDiData();

    this.parkingLot = this.route.snapshot.data.parkingLot;

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
    const servicePointVersions: ReadServicePointVersion[] = this.route.snapshot.data.servicePoint;
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
    this.router.navigate(['..'], { relativeTo: this.route }).then();
  }

  toggleEdit() {
    if (this.form.enabled) {
      this.showCancelEditDialog();
    } else {
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
        this.update(parkingLotVersion);
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
            relativeTo: this.route,
          })
          .then(() => this.ngOnInit());
      });
  }

  private update(parkingLotVersion: ParkingLotVersion) {
    this.personWithReducedMobilityService
      .updateParkingLot(this.selectedVersion.id!, parkingLotVersion)
      .subscribe(() => {
        this.notificationService.success('PRM.PARKING_LOTS.NOTIFICATION.EDIT_SUCCESS');
        this.router
          .navigate(['..', this.selectedVersion.sloid], {
            relativeTo: this.route,
          })
          .then(() => this.ngOnInit());
      });
  }

  private showCancelEditDialog() {
    this.confirmLeave()
      .pipe(take(1))
      .subscribe((confirmed) => {
        if (confirmed) {
          if (this.isNew) {
            this.form.reset();
            this.router.navigate(['..'], { relativeTo: this.route }).then();
          } else {
            this.form.disable();
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
    return this.form && this.form.dirty;
  }
}
