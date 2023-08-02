import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { VersionsHandlingService } from '../../../../core/versioning/versions-handling.service';
import {
  Category,
  OperatingPointTechnicalTimetableType,
  OperatingPointType,
  ReadServicePointVersion,
  StopPointType,
} from '../../../../api';
import { FormGroup } from '@angular/forms';
import {
  ServicePointDetailFormGroup,
  ServicePointFormGroupBuilder,
} from './service-point-detail-form-group';
import { ServicePointType } from './service-point-type';

@Component({
  selector: 'app-service-point',
  templateUrl: './service-point-detail.component.html',
  styleUrls: ['./service-point-detail.component.scss'],
})
export class ServicePointDetailComponent implements OnInit {
  servicePointVersions!: ReadServicePointVersion[];
  selectedVersion!: ReadServicePointVersion;
  showVersionSwitch = false;
  selectedVersionIndex!: number;
  form!: FormGroup<ServicePointDetailFormGroup>;
  isNew = true;

  types = Object.values(ServicePointType);
  selectedType: ServicePointType = ServicePointType.ServicePoint;
  operatingPointTypes = (Object.values(OperatingPointType) as string[]).concat(
    Object.values(OperatingPointTechnicalTimetableType)
  );

  stopPoint = false;
  freightServicePoint = false;

  stopPointTypes = Object.values(StopPointType);
  categories = Object.values(Category);

  constructor(private route: ActivatedRoute) {}

  ngOnInit() {
    this.route.parent?.data.subscribe((next) => {
      this.servicePointVersions = next.servicePoint;
      this.initServicePoint();
    });
  }

  private initServicePoint() {
    VersionsHandlingService.addVersionNumbers(this.servicePointVersions);
    this.showVersionSwitch = VersionsHandlingService.hasMultipleVersions(this.servicePointVersions);
    this.selectedVersion = VersionsHandlingService.determineDefaultVersionByValidity(
      this.servicePointVersions
    );
    if (this.selectedVersion.id) {
      this.isNew = false;
    }
    this.selectedVersionIndex = this.servicePointVersions.indexOf(this.selectedVersion);

    this.form = ServicePointFormGroupBuilder.buildFormGroup(this.selectedVersion);
    if (!this.isNew) {
      this.form.disable();
    }

    this.initType();
  }

  switchVersion(newIndex: number) {
    this.selectedVersion = this.servicePointVersions[newIndex];
  }

  private initType() {
    if (
      this.selectedVersion.operatingPointType ||
      this.selectedVersion.operatingPointTechnicalTimetableType
    ) {
      this.selectedType = ServicePointType.OperatingPoint;
    }
    if (this.selectedVersion.stopPoint || this.selectedVersion.freightServicePoint) {
      this.stopPoint = this.selectedVersion.stopPoint!;
      this.freightServicePoint = this.selectedVersion.freightServicePoint!;

      this.selectedType = ServicePointType.StopPoint;
    }
    if (this.selectedVersion.fareStop) {
      this.selectedType = ServicePointType.FareStop;
    }
  }
}
