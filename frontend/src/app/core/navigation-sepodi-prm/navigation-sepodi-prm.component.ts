import { Component, Input, OnChanges, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import {
  Country,
  PersonWithReducedMobilityService,
  ServicePointsService,
} from '../../api';
import { Countries } from '../country/Countries';
import { NgIf } from '@angular/common';
import { TranslatePipe } from '@ngx-translate/core';

export type TargetPageType =
  | 'stop-point'
  | 'service-point'
  | 'traffic-point-table'
  | 'traffic-point-detail'
  | 'platform-table'
  | 'platform-detail';

@Component({
  selector: 'app-navigation-sepodi-prm',
  templateUrl: './navigation-sepodi-prm.component.html',
  imports: [NgIf, TranslatePipe],
})
export class NavigationSepodiPrmComponent implements OnInit, OnChanges {
  @Input() targetPage!: TargetPageType;

  @Input() sloid?: string;
  @Input() number?: number;
  @Input() parentSloid?: string;

  isTargetViewSepodi!: boolean;
  isStopPoint!: boolean;
  isSwissServicePoint!: boolean;

  constructor(
    private router: Router,
    private readonly personWithReducedMobilityService: PersonWithReducedMobilityService,
    private readonly servicePointsService: ServicePointsService
  ) {}

  ngOnInit(): void {
    this.init();
  }

  ngOnChanges(): void {
    this.init();
  }

  init() {
    const sepodiPages = [
      'service-point',
      'traffic-point-table',
      'traffic-point-detail',
    ];
    this.isTargetViewSepodi = sepodiPages.includes(this.targetPage);
    if (!this.isTargetViewSepodi) {
      this.checkServicePointIsLocatedInSwitzerland(this.number!);
    }
  }

  navigate() {
    if (!this.isTargetViewSepodi) {
      const sloid = this.parentSloid || this.sloid!;
      this.checkStopPointExists(sloid);
    }

    switch (this.targetPage) {
      case 'stop-point':
        this.router.navigateByUrl(
          `/prm-directory/stop-points/${this.sloid}/stop-point`
        );
        break;
      case 'service-point':
        this.router.navigateByUrl(
          `/service-point-directory/service-points/${this.number}/service-point`
        );
        break;
      case 'traffic-point-table':
        this.router.navigateByUrl(
          `/service-point-directory/service-points/${this.number}/traffic-point-elements`
        );
        break;
      case 'traffic-point-detail':
        this.router.navigateByUrl(
          `/service-point-directory/traffic-point-elements/${this.sloid}`
        );
        break;
      case 'platform-table':
        this.router.navigateByUrl(
          `/prm-directory/stop-points/${this.sloid}/platforms`
        );
        break;
      case 'platform-detail':
        this.router.navigateByUrl(
          `/prm-directory/stop-points/${this.parentSloid}/platforms/${this.sloid}/detail`
        );
        break;
      default:
        this.router.navigateByUrl('');
    }
  }

  checkStopPointExists(sloid: string) {
    this.personWithReducedMobilityService
      .getStopPointVersions(sloid)
      .subscribe((stoppoint) => {
        if (stoppoint.length === 0) {
          this.router.navigateByUrl(
            `/prm-directory/stop-points/${sloid}/stop-point`
          );
        }
      });
  }

  checkServicePointIsLocatedInSwitzerland(number: number) {
    this.servicePointsService
      .getServicePointVersions(number)
      .subscribe((servicePointVersion) => {
        const servicePoint =
          servicePointVersion[servicePointVersion.length - 1];
        this.isSwissServicePoint =
          Countries.fromUicCode(servicePoint.number.uicCountryCode)
            .enumCountry === Country.Switzerland;
        this.isStopPoint =
          servicePointVersion.filter((sp) => sp.stopPoint).length > 0;
      });
  }
}
