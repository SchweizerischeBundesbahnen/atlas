import {Component, OnInit} from '@angular/core';
import {DetailFormComponent} from "../../../../../core/leave-guard/leave-dirty-form-guard.service";
import {PersonWithReducedMobilityService, ReadServicePointVersion, ReadToiletVersion, ToiletVersion} from "../../../../../api";
import {DateRange} from "../../../../../core/versioning/date-range";
import {FormGroup} from "@angular/forms";
import {ActivatedRoute, Router} from "@angular/router";
import {NotificationService} from "../../../../../core/notification/notification.service";
import {DialogService} from "../../../../../core/components/dialog/dialog.service";
import {VersionsHandlingService} from "../../../../../core/versioning/versions-handling.service";
import {Observable, of, take} from "rxjs";
import {ToiletFormGroup, ToiletFormGroupBuilder} from "./form/toilet-form-group";

@Component({
  selector: 'app-toilet-detail',
  templateUrl: './toilet-detail.component.html',
})
export class ToiletDetailComponent implements OnInit, DetailFormComponent {

  isNew = false;
  toiletVersions: ReadToiletVersion[] = [];
  selectedVersion!: ReadToiletVersion;

  servicePoint!: ReadServicePointVersion;
  maxValidity!: DateRange;

  form!: FormGroup<ToiletFormGroup>;
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

    this.toiletVersions = this.route.snapshot.data.toilet;

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
    const servicePointVersions: ReadServicePointVersion[] = this.route.snapshot.data.servicePoint;
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
      const toiletLotVersion = ToiletFormGroupBuilder.getWritableForm(
        this.form,
        this.servicePoint.sloid!,
      );
      if (this.isNew) {
        this.create(toiletLotVersion);
      } else {
        this.update(toiletLotVersion);
      }
    }
  }

  private create(toiletLotVersion: ToiletVersion) {
    this.personWithReducedMobilityService
      .createToiletVersion(toiletLotVersion)
      .subscribe((createdVersion) => {
        this.notificationService.success('PRM.TOILETS.NOTIFICATION.ADD_SUCCESS');
        this.router
          .navigate(['..', createdVersion.sloid], {
            relativeTo: this.route,
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
