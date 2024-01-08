import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import {
  ReadPlatformVersion,
  ReadServicePointVersion,
  ReadTrafficPointElementVersion,
} from '../../../api';
import { VersionsHandlingService } from '../../../core/versioning/versions-handling.service';
import { DateRange } from '../../../core/versioning/date-range';

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

  constructor(
    private route: ActivatedRoute,
    private router: Router,
  ) {}

  ngOnInit(): void {
    this.initServicePointDesignation();

    this.platform = this.route.snapshot.data.platform;
    if (this.platform.length === 0) {
      this.isNew = true;
    } else {
      this.isNew = false;
      this.maxValidity = VersionsHandlingService.getMaxValidity(this.platform);
      this.selectedVersion = VersionsHandlingService.determineDefaultVersionByValidity(
        this.platform,
      );
    }
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
