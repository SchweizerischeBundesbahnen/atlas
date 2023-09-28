import { Component, Input, OnDestroy, OnInit } from '@angular/core';
import { AbstractControl, FormGroup } from '@angular/forms';
import { ServicePointDetailFormGroup } from '../service-point-side-panel/service-point/service-point-detail-form-group';
import { ServicePointType } from '../service-point-side-panel/service-point/service-point-type';
import { TranslationSortingService } from '../../../core/translation/translation-sorting.service';
import { Subscription } from 'rxjs';
import {
  Category,
  OperatingPointTechnicalTimetableType,
  OperatingPointType,
  ReadServicePointVersion,
  SpatialReference,
  StopPointType,
} from '../../../api';

@Component({
  selector: 'service-point-form',
  templateUrl: './service-point-form.component.html',
  styleUrls: ['./service-point-form.component.scss'],
})
export class ServicePointFormComponent implements OnInit, OnDestroy {
  @Input() form?: FormGroup<ServicePointDetailFormGroup>;
  @Input() currentVersion?: ReadServicePointVersion;

  public servicePointTypes = Object.values(ServicePointType);
  public operatingPointTypes: string[] = [];
  public stopPointTypes = Object.values(StopPointType);
  public categories = Object.values(Category);

  private langChangeSubscription?: Subscription;

  constructor(private readonly translationSortingService: TranslationSortingService) {}

  ngOnInit(): void {
    this.initSortedOperatingPointTypes();
  }

  ngOnDestroy() {
    this.langChangeSubscription?.unsubscribe();
  }

  private initSortedOperatingPointTypes(): void {
    this.setSortedOperatingPointTypes();
    this.langChangeSubscription =
      this.translationSortingService.translateService.onLangChange.subscribe(
        this.setSortedOperatingPointTypes,
      );
  }

  private setSortedOperatingPointTypes = (): void => {
    this.operatingPointTypes = this.translationSortingService.sort(
      [
        ...Object.values(OperatingPointType),
        ...Object.values(OperatingPointTechnicalTimetableType),
      ],
      'SEPODI.SERVICE_POINTS.OPERATING_POINT_TYPES.',
    );
  };

  onGeolocationToggleChange(hasGeolocation: boolean): void {
    const spatialRefCtrl = this.spatialRefCtrl;
    if (!spatialRefCtrl) return;
    if (hasGeolocation) {
      spatialRefCtrl.setValue(SpatialReference.Lv95);
    } else {
      spatialRefCtrl.setValue(null);
    }
    this.form?.markAsDirty();
  }

  get spatialRefCtrl(): AbstractControl | undefined {
    return this.form?.controls.servicePointGeolocation.controls.spatialReference;
  }

  setOperatingPointRouteNetwork(isSelected: boolean) {
    if (!this.form) return;
    if (isSelected) {
      this.form.controls.operatingPointRouteNetwork.setValue(true);
      this.form.controls.operatingPointKilometer.setValue(true);
      this.form.controls.operatingPointKilometer.disable();
      this.form.controls.operatingPointKilometerMaster.setValue(this.currentVersion?.number.number);
      this.form.controls.operatingPointKilometerMaster.disable();
    } else {
      this.form.controls.operatingPointRouteNetwork.setValue(false);
      this.form.controls.operatingPointKilometer.setValue(false);
      this.form.controls.operatingPointKilometer.enable();
      this.form.controls.operatingPointKilometerMaster.reset();
      this.form.controls.operatingPointKilometerMaster.enable();
    }
  }

  setOperatingPointKilometer(isSelected: boolean) {
    if (!this.form) return;
    if (isSelected) {
      this.form.controls.operatingPointKilometer.setValue(true);
    } else {
      this.form.controls.operatingPointKilometer.setValue(false);
    }
  }
}
