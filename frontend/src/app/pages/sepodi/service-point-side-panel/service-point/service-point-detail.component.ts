import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { VersionsHandlingService } from '../../../../core/versioning/VersionsHandling.service';
import { ReadServicePointVersion } from '../../../../api';

@Component({
  selector: 'app-service-point',
  templateUrl: './service-point-detail.component.html',
  styleUrls: ['./service-point-detail.component.scss'],
})
export class ServicePointDetailComponent implements OnInit {
  servicePointVersions!: ReadServicePointVersion[];
  selectedVersion!: ReadServicePointVersion;
  showVersionSwitch = false;

  constructor(private route: ActivatedRoute) {}

  ngOnInit() {
    this.servicePointVersions = this.route.parent?.snapshot.data.servicePoint;

    this.showVersionSwitch = VersionsHandlingService.hasMultipleVersions(this.servicePointVersions);
    this.selectedVersion = VersionsHandlingService.determineDefaultVersionByValidity(
      this.servicePointVersions
    );
  }

  switchVersion(newIndex: number) {
    this.selectedVersion = this.servicePointVersions[newIndex];
  }
}
