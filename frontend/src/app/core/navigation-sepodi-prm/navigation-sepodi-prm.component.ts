import {Component, Input, OnChanges, OnInit, SimpleChanges} from '@angular/core';
import {Router} from "@angular/router";
import {PersonWithReducedMobilityService} from "../../api";

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

  @Input() sloid!: string;
  @Input() platform_sloid!: string;

  @Input() number!: number;
  @Input() targetPage!: NavigationToPage;

  targetUrl!: string;
  isTargetViewSepodi!: boolean;

  constructor(
    private router: Router,
    private readonly personWithReducedMobilityService: PersonWithReducedMobilityService
  ) {}

  ngOnInit(): void {
    console.log(this.sloid)
    const urlMapping = this.getUrlMappings(this.number, this.sloid, this.platform_sloid)
    this.isTargetViewSepodi = urlMapping[this.targetPage].icon === NavigationToPage.SEPODI;
    this.targetUrl = urlMapping[this.targetPage].url
  }

  ngOnChanges(changes: SimpleChanges): void {
    console.log("logggg")
  }



  navigate() {
    if(!this.isTargetViewSepodi) {
      this.checkStoppointExists(this.sloid);
    }
    this.router.navigateByUrl(this.targetUrl);
  }

  getUrlMappings(number?: number, sloid?: string, platform_sloid?: string) {
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
        url: `/prm-directory/stop-points/${sloid}/platforms/${platform_sloid}/detail`,
        icon: NavigationToPage.PRM
      }
    };
  }

  checkStoppointExists(sloid: string) {
    this.personWithReducedMobilityService.getStopPointVersions(sloid).subscribe((stoppoint) => {
      console.log("stop point ", stoppoint)
      if(stoppoint.length === 0) {
        this.router.navigateByUrl(`/prm-directory/stop-points/${sloid}/stop-point`)
      }
    })
  }
}
