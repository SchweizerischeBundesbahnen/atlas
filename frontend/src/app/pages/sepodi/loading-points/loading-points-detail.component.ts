import { Component, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import {
  CreateLoadingPointVersion,
  LoadingPointsService,
  ReadLoadingPointVersion,
  ReadServicePointVersion,
  ServicePointsService,
} from '../../../api';
import { VersionsHandlingService } from '../../../core/versioning/versions-handling.service';
import { DateRange } from '../../../core/versioning/date-range';
import { catchError, EMPTY, Observable, of, Subject } from 'rxjs';
import { Pages } from '../../pages';
import { FormGroup } from '@angular/forms';
import {
  LoadingPointDetailFormGroup,
  LoadingPointFormGroupBuilder,
} from './loading-point-detail-form-group';
import { DialogService } from '../../../core/components/dialog/dialog.service';
import { ValidationService } from '../../../core/validation/validation.service';
import { takeUntil } from 'rxjs/operators';
import { NotificationService } from '../../../core/notification/notification.service';
import { ValidityConfirmationService } from '../validity/validity-confirmation.service';
import { DetailFormComponent } from '../../../core/leave-guard/leave-dirty-form-guard.service';

@Component({
  selector: 'app-loading-points',
  templateUrl: './loading-points-detail.component.html',
  styleUrls: ['./loading-points-detail.component.scss'],
})
export class LoadingPointsDetailComponent implements OnInit, OnDestroy, DetailFormComponent {
  loadingPointVersions!: ReadLoadingPointVersion[];
  selectedVersion!: ReadLoadingPointVersion;

  maxValidity!: DateRange;
  servicePointName!: string;

  showVersionSwitch = false;
  selectedVersionIndex!: number;

  form!: FormGroup<LoadingPointDetailFormGroup>;
  isNew = false;
  servicePointNumber!: number;
  servicePoint: ReadServicePointVersion[] = [];
  servicePointBusinessOrganisations: string[] = [];

  private ngUnsubscribe = new Subject<void>();

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private servicePointService: ServicePointsService,
    private loadingPointsService: LoadingPointsService,
    private dialogService: DialogService,
    private validityConfirmationService: ValidityConfirmationService,
    private notificationService: NotificationService,
  ) {}

  ngOnInit() {
    this.route.data.pipe(takeUntil(this.ngUnsubscribe)).subscribe((next) => {
      this.loadingPointVersions = next.loadingPoint;
      this.initLoadingPoint();
    });
  }

  ngOnDestroy() {
    this.ngUnsubscribe.next();
    this.ngUnsubscribe.unsubscribe();
  }

  private initLoadingPoint() {
    if (this.loadingPointVersions.length == 0) {
      this.isNew = true;
      this.form = LoadingPointFormGroupBuilder.buildFormGroup();
    } else {
      this.isNew = false;
      VersionsHandlingService.addVersionNumbers(this.loadingPointVersions);
      this.maxValidity = VersionsHandlingService.getMaxValidity(this.loadingPointVersions);
      this.selectedVersion = VersionsHandlingService.determineDefaultVersionByValidity(
        this.loadingPointVersions,
      );
      this.selectedVersionIndex = this.loadingPointVersions.indexOf(this.selectedVersion);

      this.initSelectedVersion();
    }
    this.initServicePointInformation();
  }

  private initServicePointInformation() {
    this.servicePointNumber = this.route.snapshot.params['servicePointNumber'];

    if (!this.servicePointNumber) {
      this.router.navigate([Pages.SEPODI.path]).then();
    } else {
      this.servicePointService
        .getServicePointVersions(this.servicePointNumber)
        .pipe(takeUntil(this.ngUnsubscribe))
        .subscribe((servicePoint) => {
          this.servicePoint = servicePoint;
          this.servicePointName =
            VersionsHandlingService.determineDefaultVersionByValidity(
              servicePoint,
            ).designationOfficial;
          this.servicePointBusinessOrganisations = this.servicePoint.map((i) => {
            return i.businessOrganisation;
          });
        });
    }
  }

  backToServicePoint() {
    this.router
      .navigate([
        Pages.SEPODI.path,
        Pages.SERVICE_POINTS.path,
        this.servicePointNumber,
        'loading-points',
      ])
      .then();
  }

  switchVersion(newIndex: number) {
    this.selectedVersionIndex = newIndex;
    this.selectedVersion = this.loadingPointVersions[newIndex];
    this.initSelectedVersion();
  }

  private initSelectedVersion() {
    this.showVersionSwitch = VersionsHandlingService.hasMultipleVersions(this.loadingPointVersions);
    this.form = LoadingPointFormGroupBuilder.buildFormGroup(this.selectedVersion);
    if (!this.isNew) {
      this.form.disable();
    }
  }

  toggleEdit() {
    if (this.form.enabled) {
      this.showConfirmationDialog();
    } else {
      this.form.enable();
    }
  }

  private showConfirmationDialog() {
    this.confirmLeave().subscribe((confirmed) => {
      if (confirmed) {
        if (this.isNew) {
          this.backToServicePoint();
        } else {
          this.initSelectedVersion();
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

  save() {
    ValidationService.validateForm(this.form);
    if (this.form.valid) {
      this.validityConfirmationService
        .confirmValidityOverServicePoint(
          this.servicePoint,
          this.form.controls.validFrom.value!,
          this.form.controls.validTo.value!,
        )
        .subscribe((confirmed) => {
          if (confirmed) {
            const loadingPointVersion = this.form.value as unknown as CreateLoadingPointVersion;
            loadingPointVersion.servicePointNumber = this.servicePointNumber;
            this.form.disable();
            if (this.isNew) {
              this.create(loadingPointVersion);
            } else {
              this.update(this.selectedVersion.id!, loadingPointVersion);
            }
          }
        });
    }
  }

  private create(loadingPointVersion: CreateLoadingPointVersion) {
    this.loadingPointsService
      .createLoadingPoint(loadingPointVersion)
      .pipe(takeUntil(this.ngUnsubscribe), catchError(this.handleError()))
      .subscribe((loadingPointVersion) => {
        this.notificationService.success('SEPODI.LOADING_POINTS.NOTIFICATION.ADD_SUCCESS');
        this.router
          .navigate([
            Pages.SEPODI.path,
            Pages.LOADING_POINTS.path,
            loadingPointVersion.servicePointNumber.number,
            loadingPointVersion.number,
          ])
          .then();
      });
  }

  private update(id: number, loadingPointVersion: CreateLoadingPointVersion) {
    this.loadingPointsService
      .updateLoadingPoint(id, loadingPointVersion)
      .pipe(takeUntil(this.ngUnsubscribe), catchError(this.handleError()))
      .subscribe(() => {
        this.notificationService.success('SEPODI.LOADING_POINTS.NOTIFICATION.EDIT_SUCCESS');
        this.router
          .navigate([
            Pages.SEPODI.path,
            Pages.LOADING_POINTS.path,
            this.selectedVersion.servicePointNumber.number,
            this.selectedVersion.number,
          ])
          .then();
      });
  }

  private handleError() {
    return () => {
      this.form.enable();
      return EMPTY;
    };
  }

  isFormDirty(): boolean {
    return this.form.dirty;
  }
}
