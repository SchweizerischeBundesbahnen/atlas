import {Component, Input, OnChanges, OnInit} from '@angular/core';
import {Router} from "@angular/router";
import {
  PersonWithReducedMobilityService, ReadPlatformVersion,
  ReadServicePointVersion, ReadStopPointVersion, ReadTrafficPointElementVersion,
  ServicePointNumber,
  ServicePointsService
} from "../../api";

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
  @Input() currentElement?: ReadServicePointVersion | ReadStopPointVersion | ReadTrafficPointElementVersion | ReadPlatformVersion

  targetUrl!: string;
  isTargetViewSepodi!: boolean;

  constructor(
    private router: Router,
    private readonly personWithReducedMobilityService: PersonWithReducedMobilityService,
    private readonly servicePointsService: ServicePointsService
  ) {}

  ngOnInit(): void {
    //const test = this.currentElement as any ['number'] ?? this.currentElement as any ['servicePointNumber'];
    console.log(this.currentElement)
    this.init();
  }

  ngOnChanges(): void {
    this.init();
  }

  init() {
    //const urlMapping = this.getUrlMappings(this.number, this.sloid, this.platformSloid);
    //this.isTargetViewSepodi = urlMapping[this.targetPage].icon === NavigationToPage.SEPODI;
    //this.targetUrl = urlMapping[this.targetPage].url;
  }

  navigate() {
    if(!this.isTargetViewSepodi) {
      //this.checkStopPointExists(this.sloid!);
    }
    this.router.navigateByUrl(this.targetUrl);
  }

  getUrlMappings(number?: number, sloid?: string, platformSloid?: string) {
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
        url: `/prm-directory/stop-points/${sloid}/platforms/${platformSloid}/detail`,
        icon: NavigationToPage.PRM
      }
    };
  }

  checkStopPointExists(sloid: string) {
    this.personWithReducedMobilityService.getStopPointVersions(sloid).subscribe((stoppoint) => {
      console.log("stop point ", stoppoint)
      //is swiss
      if(stoppoint.length === 0) {
        this.router.navigateByUrl(`/prm-directory/stop-points/${sloid}/stop-point`)
      }
    })
  }

  checkServicePointIsLocatedInSwitzerland(number: number){
    this.servicePointsService.getServicePointVersion(number).subscribe((servicePoint) => {
      console.log("service point ", servicePoint)
      //is swiss?
      //is haltestelle
    })
  }
}
