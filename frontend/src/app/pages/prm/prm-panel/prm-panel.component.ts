import { Component } from '@angular/core';
import {
  BusinessOrganisationsService,
  BusinessOrganisationVersion,
  ReadServicePointVersion,
  ReadStopPointVersion,
  ServicePointsService,
} from '../../../api';
import { DateRange } from '../../../core/versioning/date-range';
import { Subscription } from 'rxjs';
import { VersionsHandlingService } from '../../../core/versioning/versions-handling.service';
import { ActivatedRoute } from '@angular/router';
import { map, switchMap, tap } from 'rxjs/operators';
import { BusinessOrganisationLanguageService } from '../../../core/form-components/bo-select/business-organisation-language.service';
import { StopPointExistsDataShareService } from '../stop-point/service/stop-point-exists-data-share.service';
import { PrmMeanOfTransportHelper } from '../prm-mean-of-transport-helper';

export const COMPLETE_TABS = [
  {
    link: 'reference-point',
    title: 'PRM.REFERENCE_POINT',
  },
  {
    link: 'connection',
    title: 'PRM.CONNECTION',
  },
];
export const REDUCED_TABS = [
  {
    link: 'stop-point',
    title: 'PRM.STOP_POINT',
  },
  {
    link: 'platform',
    title: 'PRM.PLATFORM',
  },
  {
    link: 'ticket-counter',
    title: 'PRM.TICKET_COUNTER',
  },
  {
    link: 'information-desk',
    title: 'PRM.INFORMATION_DESK',
  },
  {
    link: 'toilette',
    title: 'PRM.TOILETTE',
  },
  {
    link: 'parking-lot',
    title: 'PRM.PARKING_LOT',
  },
];

@Component({
  selector: 'app-prm-panel',
  templateUrl: './prm-panel.component.html',
  styleUrls: ['./prm-panel.component.scss'],
})
export class PrmPanelComponent {
  selectedServicePointVersion!: ReadServicePointVersion;
  selectedBusinessOrganisation?: BusinessOrganisationVersion;
  servicePointVersions!: ReadServicePointVersion[];
  selectedVersion!: ReadStopPointVersion;
  maxValidity!: DateRange;
  boDescription!: string;
  isNew!: boolean;
  disableTabNavigation = false;
  stopPointVersions!: ReadStopPointVersion[];

  tabs = REDUCED_TABS.concat(...COMPLETE_TABS);
  private stopPointSubscription?: Subscription;

  constructor(
    private route: ActivatedRoute,
    private servicePointsService: ServicePointsService,
    private businessOrganisationsService: BusinessOrganisationsService,
    private businessOrganisationLanguageService: BusinessOrganisationLanguageService,
    private stopPointExistsDataShareService: StopPointExistsDataShareService,
  ) {
    this.businessOrganisationLanguageService
      .languageChanged()
      .subscribe(() => this.translateBoDescription());
  }

  ngOnInit() {
    this.route.data
      .pipe(
        map((next) => {
          this.servicePointVersions = next.servicePoints;
          this.stopPointVersions = next.stopPoints;
          this.checkStopPointExists();
          this.initServicePointVersioning(this.servicePointVersions);
        }),
        switchMap(() =>
          this.businessOrganisationsService
            .getVersions(this.selectedServicePointVersion!.businessOrganisation)
            .pipe(tap((bo) => this.initSelectedBusinessOrganisationVersion(bo))),
        ),
      )
      .subscribe();
  }

  private checkStopPointExists() {
    if (this.stopPointVersions.length === 0) {
      this.disableTabNavigation = true;
      this.tabs = [
        {
          link: 'stop-point',
          title: 'PRM.STOP_POINT',
        },
      ];
    } else {
      const isReduced = PrmMeanOfTransportHelper.isReduced(
        this.stopPointVersions[0].meansOfTransport,
      );
      if (isReduced) {
        this.tabs = REDUCED_TABS;
      }
    }
  }

  ngOnDestroy() {
    this.stopPointSubscription?.unsubscribe();
  }

  private initServicePointVersioning(servicePointVersions: ReadServicePointVersion[]) {
    this.maxValidity = VersionsHandlingService.getMaxValidity(this.servicePointVersions);
    this.selectedServicePointVersion =
      VersionsHandlingService.determineDefaultVersionByValidity(servicePointVersions);
  }

  private initSelectedBusinessOrganisationVersion(bos: BusinessOrganisationVersion[]) {
    this.selectedBusinessOrganisation =
      VersionsHandlingService.determineDefaultVersionByValidity(bos);

    this.translateBoDescription();
  }

  private translateBoDescription() {
    this.boDescription =
      this.selectedBusinessOrganisation![
        this.businessOrganisationLanguageService.getCurrentLanguageDescription()
      ];
  }
}
