import {
  Component,
  ContentChild,
  EventEmitter,
  Input,
  OnDestroy,
  OnInit,
  Output,
} from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { ServicePointDetailFormGroup } from '../service-point-detail-form-group';
import { ServicePointType } from '../service-point-type';
import { TranslationSortingService } from '../../../../../core/translation/translation-sorting.service';
import { Observable, of, Subject, Subscription, take } from 'rxjs';
import {
  ApplicationRole,
  ApplicationType,
  Category,
  GeoDataService,
  OperatingPointTechnicalTimetableType,
  OperatingPointType,
  ReadServicePointVersion,
  StopPointType,
} from '../../../../../api';
import { LocationInformation } from '../location-information';
import { map, takeUntil } from 'rxjs/operators';
import { DialogService } from '../../../../../core/components/dialog/dialog.service';
import { GeographyComponent } from '../../../geography/geography.component';
import { Countries } from '../../../../../core/country/Countries';
import { PermissionService } from '../../../../../core/auth/permission/permission.service';
import { AtLeastOneValidator } from '../../../../../core/validation/boolean-cross-validator/at-least-one-validator';
import { NgIf, NgFor, NgTemplateOutlet, AsyncPipe } from '@angular/common';
import { TextFieldComponent } from '../../../../../core/form-components/text-field/text-field.component';
import { DateRangeComponent } from '../../../../../core/form-components/date-range/date-range.component';
import { BusinessOrganisationSelectComponent } from '../../../../../core/form-components/bo-select/business-organisation-select.component';
import { MatLabel } from '@angular/material/form-field';
import { MatRadioGroup, MatRadioButton } from '@angular/material/radio';
import { AtlasFieldErrorComponent } from '../../../../../core/form-components/atlas-field-error/atlas-field-error.component';
import { SelectComponent } from '../../../../../core/form-components/select/select.component';
import { MatCheckbox } from '@angular/material/checkbox';
import { MeansOfTransportPickerComponent } from '../../../means-of-transport-picker/means-of-transport-picker.component';
import { KilometerMasterSearchComponent } from '../search/kilometer-master-search.component';
import { DisplayCantonPipe } from '../../../../../core/cantons/display-canton.pipe';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
  selector: 'service-point-form',
  templateUrl: './service-point-form.component.html',
  styleUrls: ['./service-point-form.component.scss'],
  imports: [
    NgIf,
    TextFieldComponent,
    ReactiveFormsModule,
    DateRangeComponent,
    BusinessOrganisationSelectComponent,
    MatLabel,
    MatRadioGroup,
    NgFor,
    MatRadioButton,
    AtlasFieldErrorComponent,
    SelectComponent,
    MatCheckbox,
    MeansOfTransportPickerComponent,
    NgTemplateOutlet,
    KilometerMasterSearchComponent,
    DisplayCantonPipe,
    AsyncPipe,
    TranslatePipe,
  ],
})
export class ServicePointFormComponent implements OnInit, OnDestroy {
  @ContentChild(GeographyComponent, { static: true })
  geographyComponent?: GeographyComponent;

  locationInformation$?: Observable<LocationInformation>;
  servicePointTypes = Object.values(ServicePointType);
  operatingPointTypes: string[] = [];
  stopPointTypes = Object.values(StopPointType);
  categories = Object.values(Category);
  isNew = false;

  @Output()
  selectedServicePointTypeChange: EventEmitter<
    ServicePointType | null | undefined
  > = new EventEmitter<ServicePointType | null | undefined>();

  @Input() set form(form: FormGroup<ServicePointDetailFormGroup>) {
    this._form = form;
    this._currentSelectedServicePointType = form.controls.selectedType.value;
    this.formDestroy$.next();
    this.initTypeChangeInformationDialog(form.controls.selectedType);
  }

  get form(): FormGroup<ServicePointDetailFormGroup> | undefined {
    return this._form;
  }

  @Input() set currentVersion(version: ReadServicePointVersion | undefined) {
    this._currentVersion = version;
    this.setStopPointValidator();
    this.locationInformation$ = of({
      isoCountryCode: version?.servicePointGeolocation?.isoCountryCode,
      canton: version?.servicePointGeolocation?.swissLocation?.canton,
      municipalityName:
        version?.servicePointGeolocation?.swissLocation?.localityMunicipality
          ?.municipalityName,
      localityName:
        version?.servicePointGeolocation?.swissLocation?.localityMunicipality
          ?.localityName,
      swissDistrictName:
        version?.servicePointGeolocation?.swissLocation?.district?.districtName,
    });
  }

  get currentVersion(): ReadServicePointVersion | undefined {
    return this._currentVersion;
  }

  private _currentVersion?: ReadServicePointVersion;
  private _form?: FormGroup<ServicePointDetailFormGroup>;
  private _currentSelectedServicePointType: ServicePointType | null | undefined;
  private langChangeSubscription?: Subscription;
  private geographyChangedEventSubscription?: Subscription;
  private formDestroy$ = new Subject<void>();

  boSboidRestriction: string[] = [];

  constructor(
    private readonly translationSortingService: TranslationSortingService,
    private readonly dialogService: DialogService,
    private readonly geoDataService: GeoDataService,
    private readonly permissionService: PermissionService
  ) {}

  ngOnInit(): void {
    this.isNew = !this.currentVersion?.id;
    this.initSortedOperatingPointTypes();
    this.initBoSboidRestriction();
    if (!this.isNew) {
      this.geographyComponent?.coordinatesChanged.subscribe(
        (coordinatePair) => {
          if (coordinatePair.north && coordinatePair.east) {
            this.locationInformation$ = this.geoDataService
              .getLocationInformation(coordinatePair)
              .pipe(
                map((geoReference) => ({
                  isoCountryCode: Countries.fromCountry(geoReference.country)
                    ?.short,
                  canton: geoReference.swissCanton,
                  municipalityName: geoReference.swissMunicipalityName,
                  localityName: geoReference.swissLocalityName,
                  height: geoReference.height,
                  swissDistrictName: geoReference.swissDistrictName,
                }))
              );
          }
        }
      );
    }
  }

  ngOnDestroy() {
    this.langChangeSubscription?.unsubscribe();
    this.geographyChangedEventSubscription?.unsubscribe();
    this.formDestroy$.next();
    this.formDestroy$.unsubscribe();
  }

  private initSortedOperatingPointTypes(): void {
    this.setSortedOperatingPointTypes();
    this.langChangeSubscription =
      this.translationSortingService.translateService.onLangChange.subscribe(
        this.setSortedOperatingPointTypes
      );
  }

  private setSortedOperatingPointTypes = (): void => {
    this.operatingPointTypes = this.translationSortingService.sort(
      [
        ...Object.values(OperatingPointType),
        ...Object.values(OperatingPointTechnicalTimetableType),
      ],
      'SEPODI.SERVICE_POINTS.OPERATING_POINT_TYPES.'
    );
  };

  private initTypeChangeInformationDialog(
    selectedTypeCtrl: FormControl<ServicePointType | null | undefined>
  ) {
    selectedTypeCtrl.valueChanges
      .pipe(takeUntil(this.formDestroy$))
      .subscribe((newType) => {
        if (this.isNew) {
          this._currentSelectedServicePointType = newType;
          this.selectedServicePointTypeChange.emit(
            this._currentSelectedServicePointType
          );
        }
        if (!this.isNew && this._currentSelectedServicePointType != newType) {
          if (
            this._currentSelectedServicePointType !=
            ServicePointType.ServicePoint
          ) {
            this.dialogService
              .confirm({
                title: 'SEPODI.SERVICE_POINTS.TYPE_CHANGE_DIALOG.TITLE',
                message: 'SEPODI.SERVICE_POINTS.TYPE_CHANGE_DIALOG.MESSAGE',
              })
              .pipe(take(1))
              .subscribe((result) => {
                if (result) {
                  this._currentSelectedServicePointType = newType;
                  this.selectedServicePointTypeChange.emit(
                    this._currentSelectedServicePointType
                  );
                } else {
                  selectedTypeCtrl.setValue(
                    this._currentSelectedServicePointType
                  );
                }
              });
          } else {
            this._currentSelectedServicePointType = newType;
            this.selectedServicePointTypeChange.emit(
              this._currentSelectedServicePointType
            );
          }
        }
      });
  }

  initBoSboidRestriction() {
    if (!this.isNew || this.permissionService.isAdmin) {
      this.boSboidRestriction = [];
    } else {
      const permission = this.permissionService.getApplicationUserPermission(
        ApplicationType.Sepodi
      );
      if (permission.role === ApplicationRole.Writer) {
        this.boSboidRestriction =
          PermissionService.getSboidRestrictions(permission);
      } else {
        this.boSboidRestriction = [];
      }
    }
  }

  setOperatingPointRouteNetwork(isSelected: boolean) {
    if (!this.form) return;
    if (isSelected) {
      this.form.controls.operatingPointRouteNetwork.setValue(true);
      this.form.controls.operatingPointKilometer.setValue(true);
      this.form.controls.operatingPointKilometer.disable();
      this.form.controls.operatingPointKilometerMaster.setValue(
        this.currentVersion?.number.number
      );
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
      this.form.controls.operatingPointKilometerMaster.reset();
    }
  }

  setStopPointValidator() {
    if (this.form?.controls.selectedType.value === ServicePointType.StopPoint) {
      this.form!.addValidators(
        AtLeastOneValidator.of('stopPoint', 'freightServicePoint')
      );
    }
  }
}
