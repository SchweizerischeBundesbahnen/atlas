import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from "@angular/router";
import {ReadStopPointVersion} from "../../../api";
import {Subject, Subscription} from "rxjs";
import {FormGroup} from "@angular/forms";
import {StopPointDetailFormGroup} from "./stop-point/stop-point-detail-form-group";
import {VersionsHandlingService} from "../../../core/versioning/versions-handling.service";
import {takeUntil} from "rxjs/operators";

@Component({
  selector: 'app-prm-detail-panel',
  templateUrl: './prm-detail-panel.component.html',
  styleUrls: ['./prm-detail-panel.component.scss']
})
export class PrmDetailPanelComponent implements OnInit {

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

  constructor(
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {
  }

  private stopPointSubscription?: Subscription;
  private StopPointFormGroupBuilder: any;

  ngOnInit(): void {
    this.stopPointSubscription = this.route.parent?.data
      .pipe(takeUntil(this.ngUnsubscribe))
      .subscribe((next) => {
        console.log(next)
        this.stopPointVersions = next.stopPoint;
        this.initStopPoint();

      });
  }

  switchVersion(newIndex: number) {
    this.selectedVersionIndex = newIndex;
    this.selectedVersion = this.stopPointVersions[newIndex];
    this.initSelectedVersion();
  }

  public initSelectedVersion() {
    if (this.selectedVersion.id) {
      this.isNew = false;
    }

    this.form = this.StopPointFormGroupBuilder.buildFormGroup(this.selectedVersion);
    if (!this.isNew) {
      this.disableForm();
    }
    this.isSelectedVersionHighDate(this.stopPointVersions, this.selectedVersion);
  }

  private disableForm(): void {
    this.form.disable({emitEvent: false});
  }

  isSelectedVersionHighDate(
    stopPointVersions: ReadStopPointVersion[],
    selectedVersion: ReadStopPointVersion,
  ) {
    this.isLatestVersionSelected = !stopPointVersions.some(
      (obj) => obj.validTo > selectedVersion.validTo,
    );
  }

  private initStopPoint() {
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
    this.selectedVersionIndex = this.stopPointVersions.indexOf(this.selectedVersion);
    this.initSelectedVersion();
  }
}
