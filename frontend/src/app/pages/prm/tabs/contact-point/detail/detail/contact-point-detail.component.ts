import { Component, OnInit } from '@angular/core';
import {
  ContactPointVersion,
  PersonWithReducedMobilityService,
  ReadContactPointVersion,
  ReadServicePointVersion,
} from '../../../../../../api';
import { VersionsHandlingService } from '../../../../../../core/versioning/versions-handling.service';
import { ContactPointFormGroupBuilder } from '../form/contact-point-form-group';
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
import { ContactPointFormComponent } from '../form/contact-point-form/contact-point-form.component';
import { MatDivider } from '@angular/material/divider';
import { UserDetailInfoComponent } from '../../../../../../core/components/base-detail/user-edit-info/user-detail-info.component';
import { DetailFooterComponent } from '../../../../../../core/components/detail-footer/detail-footer.component';
import { AtlasButtonComponent } from '../../../../../../core/components/button/atlas-button.component';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
  selector: 'app-contact-point-detail',
  templateUrl: './contact-point-detail.component.html',
  providers: [ValidityService],
  imports: [
    DetailPageContentComponent,
    NgIf,
    SloidComponent,
    ReactiveFormsModule,
    SwitchVersionComponent,
    ContactPointFormComponent,
    MatDivider,
    UserDetailInfoComponent,
    DetailFooterComponent,
    AtlasButtonComponent,
    TranslatePipe,
  ],
})
export class ContactPointDetailComponent
  extends PrmTabDetailBaseComponent<ReadContactPointVersion>
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

    this.versions = this.route.snapshot.parent!.data.contactPoint;

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
    this.form = ContactPointFormGroupBuilder.buildFormGroup(
      this.selectedVersion
    );

    if (!this.isNew) {
      this.form.disable();
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

  protected saveProcess(): Observable<
    ReadContactPointVersion | ReadContactPointVersion[]
  > {
    this.form.markAllAsTouched();
    if (this.form.valid) {
      const contactPointVersion = ContactPointFormGroupBuilder.getWritableForm(
        this.form,
        this.servicePoint.sloid!
      );
      if (this.isNew) {
        return this.create(contactPointVersion);
      } else {
        this.validityService.updateValidity(this.form);
        return this.validityService.validate().pipe(
          switchMap((dialogRes) => {
            if (dialogRes) {
              this.form.disable();
              return this.update(contactPointVersion);
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

  private create(contactPointVersion: ContactPointVersion) {
    return this.personWithReducedMobilityService
      .createContactPoint(contactPointVersion)
      .pipe(
        switchMap((createdVersion) => {
          return this.notificateAndNavigate(
            'PRM.CONTACT_POINTS.NOTIFICATION.ADD_SUCCESS',
            createdVersion.sloid!
          ).pipe(map(() => createdVersion));
        })
      );
  }

  private update(contactPointVersion: ContactPointVersion) {
    return this.personWithReducedMobilityService
      .updateContactPoint(this.selectedVersion.id!, contactPointVersion)
      .pipe(
        switchMap((updatedVersions) => {
          return this.notificateAndNavigate(
            'PRM.CONTACT_POINTS.NOTIFICATION.EDIT_SUCCESS',
            this.selectedVersion.sloid!
          ).pipe(map(() => updatedVersions));
        })
      );
  }
}
