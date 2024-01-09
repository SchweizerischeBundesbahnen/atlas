import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import {
  ReadPlatformVersion,
  ReadServicePointVersion,
  ReadStopPointVersion,
  ReadTrafficPointElementVersion,
} from '../../../api';
import { VersionsHandlingService } from '../../../core/versioning/versions-handling.service';
import { DateRange } from '../../../core/versioning/date-range';
import {
  CompletePlatformFormGroup,
  PlatformFormGroupBuilder,
  ReducedPlatformFormGroup,
} from './form/platform-form-group';
import { FormGroup } from '@angular/forms';

@Component({
  selector: 'app-platforms',
  templateUrl: './platform.component.html',
  styleUrls: ['./platform.component.scss'],
})
export class PlatformComponent implements OnInit {
  isNew = false;
  platform: ReadPlatformVersion[] = [];
  selectedVersion!: ReadPlatformVersion;

  servicePoint!: ReadServicePointVersion;
  trafficPoint!: ReadTrafficPointElementVersion;
  maxValidity!: DateRange;
  stopPoint!: ReadStopPointVersion[];

  reduced = false;
  form!: FormGroup<ReducedPlatformFormGroup> | FormGroup<CompletePlatformFormGroup>;

  get reducedForm(): FormGroup<ReducedPlatformFormGroup> {
    return this.form as FormGroup<ReducedPlatformFormGroup>;
  }

  get completeForm(): FormGroup<CompletePlatformFormGroup> {
    return this.form as FormGroup<CompletePlatformFormGroup>;
  }

  constructor(
    private route: ActivatedRoute,
    private router: Router,
  ) {}

  ngOnInit(): void {
    this.initServicePointDesignation();

    this.platform = this.route.snapshot.data.platform;
    this.stopPoint = this.route.snapshot.data.stopPoint;
    this.reduced = this.stopPoint[0].reduced!;

    if (this.platform.length === 0) {
      this.isNew = true;
    } else {
      this.isNew = false;
      this.maxValidity = VersionsHandlingService.getMaxValidity(this.platform);
      this.selectedVersion = VersionsHandlingService.determineDefaultVersionByValidity(
        this.platform,
      );
    }

    if (this.reduced) {
      this.form = PlatformFormGroupBuilder.buildReducedFormGroup(this.selectedVersion);
    } else {
      this.form = PlatformFormGroupBuilder.buildCompleteFormGroup(this.selectedVersion);
    }
    this.form.controls.sloid.setValue(this.trafficPoint.sloid);

    console.log('reduced: ', this.reduced);
  }

  initServicePointDesignation() {
    this.servicePoint = VersionsHandlingService.determineDefaultVersionByValidity(
      this.route.snapshot.data.servicePoint,
    );
    this.trafficPoint = VersionsHandlingService.determineDefaultVersionByValidity(
      this.route.snapshot.data.trafficPoint,
    );
  }

  back() {
    this.router.navigate(['..'], { relativeTo: this.route }).then();
  }

  save() {}
}
