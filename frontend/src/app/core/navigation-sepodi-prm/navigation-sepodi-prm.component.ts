import {Component, Input, OnChanges, OnInit} from '@angular/core';
import {Router} from "@angular/router";
import {
  Country,
  PersonWithReducedMobilityService, ReadPlatformVersion,
  ReadServicePointVersion, ReadStopPointVersion, ReadTrafficPointElementVersion,
  ServicePointsService
} from "../../api";
import {Countries} from "../country/Countries";

export enum NavigationToPage {
  PRM = 'prm',
  SEPODI = 'sepodi',
  TRAFFIC_POINT_TABLE = 'traffic_point_table',
  PLATFORM_TABLE = 'platform_table',
  TRAFFIC_POINT_DETAIL = 'traffic_point_detail',
  PLATFORM_DETAIL = 'platform_detail'
}

@Component({
  selector: 'app-navigation-sepodi-prm',
  templateUrl: './navigation-sepodi-prm.component.html'
})
export class NavigationSepodiPrmComponent implements OnInit, OnChanges {

  @Input() targetPage!: NavigationToPage;
  @Input() currentElement?: ReadServicePointVersion | ReadStopPointVersion | ReadTrafficPointElementVersion | ReadPlatformVersion;

  targetUrl!: string;
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
    const urlMapping = this.getUrlMappings(this.getElementNumber(), this.currentElement?.sloid, this.getElementParentSloid());
    this.isTargetViewSepodi = urlMapping[this.targetPage].icon === NavigationToPage.SEPODI;
    this.targetUrl = urlMapping[this.targetPage].url;
    if(!this.isTargetViewSepodi){
      this.checkServicePointIsLocatedInSwitzerland(this.getElementNumber()!);
    }
  }

  navigate() {
    if(!this.isTargetViewSepodi) {
      this.checkStopPointExists(this.getElementSloid()!);
    }
    this.router.navigateByUrl(this.targetUrl);
  }

  getUrlMappings(number?: number, sloid?: string, parentSloid?: string) {
    return {
      sepodi: {
        url: `/service-point-directory/service-points/${number}/service-point`,
        icon: NavigationToPage.SEPODI
      },
      prm: {
        url: `/prm-directory/stop-points/${sloid}/stop-point`,
        icon: NavigationToPage.PRM
      },
      traffic_point_table: {
        url: `/service-point-directory/service-points/${number}/traffic-point-elements`,
        icon: NavigationToPage.SEPODI
      },
      platform_table: {
        url: `/prm-directory/stop-points/${sloid}/platforms`,
        icon: NavigationToPage.PRM
      },
      traffic_point_detail: {
        url: `/service-point-directory/traffic-point-elements/${sloid}`,
        icon: NavigationToPage.SEPODI
      },
      platform_detail: {
        url: `/prm-directory/stop-points/${parentSloid}/platforms/${sloid}/detail`,
        icon: NavigationToPage.PRM
      }
    };
  }

  checkStopPointExists(sloid: string) {
    this.personWithReducedMobilityService.getStopPointVersions(sloid).subscribe((stoppoint) => {
      if(stoppoint.length === 0) {
        this.router.navigateByUrl(`/prm-directory/stop-points/${sloid}/stop-point`);
      }
    })
  }

  checkServicePointIsLocatedInSwitzerland(number: number){
      this.servicePointsService.getServicePointVersions(number).subscribe((servicePointVersion) => {
        const servicePoint = servicePointVersion[servicePointVersion.length - 1]
        this.isSwissServicePoint = Countries.fromUicCode(servicePoint.number.uicCountryCode).enumCountry === Country.Switzerland;
        this.isStopPoint = servicePointVersion.filter((sp) => sp.stopPoint).length > 0;
      });
  }


  private getElementNumber(): number | undefined {
    if(!this.currentElement) {
      return undefined;
    }
    if('number' in this.currentElement) {
      return this.currentElement.number.number;
    }

    if ('servicePointNumber' in this.currentElement) {
      return this.currentElement.servicePointNumber.number;
    }
    return undefined;
  }

  private getElementParentSloid(): string | undefined {
    if(!this.currentElement) {
      return undefined;
    }
    if('servicePointSloid' in this.currentElement) {
      return this.currentElement.servicePointSloid;
    }

    return undefined;
  }

  private getElementSloid(): string | undefined {
    if(!this.currentElement) {
      return undefined;
    }
    if('servicePointSloid' in this.currentElement) {
      return this.currentElement.servicePointSloid;
    }
    else
    {
      return this.currentElement.sloid
    }
  }
}
