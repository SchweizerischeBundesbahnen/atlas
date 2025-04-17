import { Component, OnInit } from '@angular/core';
import {
  ParkingLotVersion,
  PersonWithReducedMobilityService,
  ReadParkingLotVersion,
  ReadServicePointVersion,
} from '../../../../../../api';
import { VersionsHandlingService } from '../../../../../../core/versioning/versions-handling.service';
import { ParkingLotFormGroupBuilder } from '../form/parking-lot-form-group';
import { DateRange } from '../../../../../../core/versioning/date-range';
import { ValidityService } from '../../../../../sepodi/validity/validity.service';
import { EMPTY, Observable, switchMap } from 'rxjs';
import { map } from 'rxjs/operators';
import { PrmTabDetailBaseComponent } from '../../../../shared/prm-tab-detail-base.component';
import { DetailPageContentComponent } from '../../../../../../core/components/detail-page-content/detail-page-content.component';
import { NgIf } from '@angular/common';
import { SloidComponent } from '../../../../../../core/form-components/sloid/sloid.component';
import { ReactiveFormsModule } from '@angular/forms';
import { SwitchVersionComponent } from '../../../../../../core/components/switch-version/switch-version.component';
import { ParkingLotFormComponent } from '../form/parking-lot-form/parking-lot-form.component';
import { MatDivider } from '@angular/material/divider';
import { UserDetailInfoComponent } from '../../../../../../core/components/base-detail/user-edit-info/user-detail-info.component';
import { DetailFooterComponent } from '../../../../../../core/components/detail-footer/detail-footer.component';
import { AtlasButtonComponent } from '../../../../../../core/components/button/atlas-button.component';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
  selector: 'app-parking-lot-detail',
  templateUrl: './parking-lot-detail.component.html',
  providers: [ValidityService],
  imports: [
    DetailPageContentComponent,
    NgIf,
    SloidComponent,
    ReactiveFormsModule,
    SwitchVersionComponent,
    ParkingLotFormComponent,
    MatDivider,
    UserDetailInfoComponent,
    DetailFooterComponent,
    AtlasButtonComponent,
    TranslatePipe,
  ],
})
export class ParkingLotDetailComponent
  extends PrmTabDetailBaseComponent<ReadParkingLotVersion>
  implements OnInit
{
  servicePoint!: ReadServicePointVersion;
  maxValidity!: DateRange;
  showVersionSwitch = false;
  businessOrganisations: string[] = [];

  constructor(
    private readonly personWithReducedMobilityService: PersonWithReducedMobilityService
  ) {
    super();
  }

  ngOnInit(): void {
    this.initSePoDiData();

    this.versions = this.route.snapshot.parent!.data.parkingLot;

    this.isNew = this.versions.length === 0;

    if (!this.isNew) {
      VersionsHandlingService.addVersionNumbers(this.versions);
      this.showVersionSwitch = VersionsHandlingService.hasMultipleVersions(
        this.versions
      );
      this.maxValidity = VersionsHandlingService.getMaxValidity(this.versions);
      this.selectedVersion =
        VersionsHandlingService.determineDefaultVersionByValidity(
          this.versions
        );
      this.selectedVersionIndex = this.versions.indexOf(this.selectedVersion);
    }

    this.initForm();
  }

  protected initForm() {
    this.form = ParkingLotFormGroupBuilder.buildFormGroup(this.selectedVersion);

    if (!this.isNew) {
      this.form.disable();
    }
  }

  protected saveProcess(): Observable<
    ReadParkingLotVersion | ReadParkingLotVersion[]
  > {
    this.form.markAllAsTouched();
    if (this.form.valid) {
      const parkingLotVersion = ParkingLotFormGroupBuilder.getWritableForm(
        this.form,
        this.servicePoint.sloid!
      );
      if (this.isNew) {
        return this.create(parkingLotVersion);
      } else {
        this.validityService.updateValidity(this.form);
        return this.validityService.validate().pipe(
          switchMap((dialogRes) => {
            if (dialogRes) {
              this.form.disable();
              return this.update(parkingLotVersion);
            } else {
              return EMPTY;
            }
          })
        );
      }
    } else {
      return EMPTY;
    }
  }
  private initSePoDiData() {
    const servicePointVersions: ReadServicePointVersion[] =
      this.route.snapshot.parent!.data.servicePoint;
    this.servicePoint =
      VersionsHandlingService.determineDefaultVersionByValidity(
        servicePointVersions
      );
    this.businessOrganisations = [
      ...new Set(
        servicePointVersions.map((value) => value.businessOrganisation)
      ),
    ];
  }

  private create(parkingLotVersion: ParkingLotVersion) {
    return this.personWithReducedMobilityService
      .createParkingLot(parkingLotVersion)
      .pipe(
        switchMap((createdVersion) => {
          return this.notificateAndNavigate(
            'PRM.PARKING_LOTS.NOTIFICATION.ADD_SUCCESS',
            createdVersion.sloid!
          ).pipe(map(() => createdVersion));
        })
      );
  }

  private update(parkingLotVersion: ParkingLotVersion) {
    return this.personWithReducedMobilityService
      .updateParkingLot(this.selectedVersion.id!, parkingLotVersion)
      .pipe(
        switchMap((updatedVersions) => {
          return this.notificateAndNavigate(
            'PRM.PARKING_LOTS.NOTIFICATION.EDIT_SUCCESS',
            this.selectedVersion.sloid!
          ).pipe(map(() => updatedVersions));
        })
      );
  }
}
