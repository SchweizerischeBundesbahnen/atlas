import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ReadStopPointVersion, StandardAttributeType } from '../../../api';
import { BehaviorSubject, Subject, Subscription } from 'rxjs';
import { FormGroup } from '@angular/forms';
import {
  StopPointDetailFormGroup,
  StopPointFormGroupBuilder,
} from './form/stop-point-detail-form-group';
import { VersionsHandlingService } from '../../../core/versioning/versions-handling.service';
import { takeUntil } from 'rxjs/operators';
import { TranslationSortingService } from '../../../core/translation/translation-sorting.service';
import { Pages } from '../../pages';

@Component({
  selector: 'app-stop-point-detail',
  templateUrl: './stop-point-detail.component.html',
  styleUrls: ['./stop-point-detail.component.scss'],
})
export class StopPointDetailComponent implements OnInit {
  isStopPointExisting = true;
  stopPointVersions!: ReadStopPointVersion[];
  selectedVersionIndex!: number;
  selectedVersion!: ReadStopPointVersion;
  form!: FormGroup<StopPointDetailFormGroup>;
  isNew = true;
  isLatestVersionSelected = false;
  showVersionSwitch = false;
  isSwitchVersionDisabled = false;
  preferredId?: number;
  private ngUnsubscribe = new Subject<void>();
  public isFormEnabled$ = new BehaviorSubject<boolean>(false);
  isReduced!: boolean | undefined;

  constructor(
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly translationSortingService: TranslationSortingService,
  ) {}

  private stopPointSubscription?: Subscription;
  standardAttributeTypes: string[] = [];

  ngOnInit(): void {
    this.stopPointSubscription = this.route.data
      .pipe(takeUntil(this.ngUnsubscribe))
      .subscribe((next) => {
        this.stopPointVersions = next.stopPoint;
        if (this.stopPointVersions.length > 0) {
          this.initExistingStopPoint();
        } else {
          this.isStopPointExisting = false;
          this.initNotExistingStopPoint();
        }
        this.setSortedOperatingPointTypes();
      });
  }

  private initNotExistingStopPoint() {}

  switchVersion(newIndex: number) {
    this.selectedVersionIndex = newIndex;
    this.selectedVersion = this.stopPointVersions[newIndex];
    this.initSelectedVersion();
  }

  public initSelectedVersion() {
    if (this.selectedVersion.id) {
      this.isNew = false;
    }

    this.form = StopPointFormGroupBuilder.buildFormGroup(this.selectedVersion);
    if (!this.isNew) {
      this.disableForm();
    }
    this.isSelectedVersionHighDate(this.stopPointVersions, this.selectedVersion);
  }

  private disableForm(): void {
    this.form.disable({ emitEvent: false });
  }

  isSelectedVersionHighDate(
    stopPointVersions: ReadStopPointVersion[],
    selectedVersion: ReadStopPointVersion,
  ) {
    this.isLatestVersionSelected = !stopPointVersions.some(
      (obj) => obj.validTo > selectedVersion.validTo,
    );
  }

  private initExistingStopPoint() {
    VersionsHandlingService.addVersionNumbers(this.stopPointVersions);
    this.showVersionSwitch = VersionsHandlingService.hasMultipleVersions(this.stopPointVersions);
    if (this.preferredId) {
      this.selectedVersion =
        this.stopPointVersions.find((i) => i.id === this.preferredId) ??
        VersionsHandlingService.determineDefaultVersionByValidity(this.stopPointVersions);
      this.preferredId = undefined;
    } else {
      this.selectedVersion = VersionsHandlingService.determineDefaultVersionByValidity(
        this.stopPointVersions,
      );
    }
    this.isReduced = this.selectedVersion.reduced;
    this.selectedVersionIndex = this.stopPointVersions.indexOf(this.selectedVersion);
    this.initSelectedVersion();
  }

  private setSortedOperatingPointTypes = (): void => {
    this.standardAttributeTypes = this.translationSortingService.sort(
      Object.values(StandardAttributeType),
      'PRM.STOP_POINTS.STANDARD_ATTRIBUTE_TYPES.',
    );
  };

  closeSidePanel() {
    this.router.navigate([Pages.PRM.path]).then();
  }

  toggleEdit() {}

  save() {}
}
