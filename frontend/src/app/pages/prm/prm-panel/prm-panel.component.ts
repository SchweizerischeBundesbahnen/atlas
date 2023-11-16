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

export const TABS = [
  {
    link: 'stop-point',
    title: 'PRM.STOP_POINT',
  },
  {
    link: 'reference-point',
    title: 'PRM.REFERENCE_POINT',
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
  {
    link: 'connection',
    title: 'PRM.CONNECTION',
  },
];

@Component({
  selector: 'app-prm-panel',
  templateUrl: './prm-panel.component.html',
  styleUrls: ['./prm-panel.component.scss'],
})
export class PrmPanelComponent {
  selectedServicePointVersion?: ReadServicePointVersion;
  selectedBusinessOrganisation?: BusinessOrganisationVersion;
  stopPointVersions!: ReadStopPointVersion[];
  selectedVersion!: ReadStopPointVersion;
  maxValidity!: DateRange;
  boDescription!: string;

  tabs = TABS;
  private stopPointSubscription?: Subscription;

  constructor(
    private route: ActivatedRoute,
    private servicePointsService: ServicePointsService,
    private businessOrganisationsService: BusinessOrganisationsService,
    private businessOrganisationLanguageService: BusinessOrganisationLanguageService,
  ) {
    this.businessOrganisationLanguageService
      .languageChanged()
      .subscribe(() => this.translateBoDescription());
  }

  ngOnInit() {
    this.route.data
      .pipe(
        map((next) => {
          this.stopPointVersions = next.stopPoint;
          this.initStopPointVersioning();
        }),
        switchMap((asd) =>
          this.servicePointsService
            .getServicePointVersions(this.selectedVersion.number.number)
            .pipe(
              tap((servicePointVersions) => this.initServicePointVersioning(servicePointVersions)),
            ),
        ),
        switchMap((asd) =>
          this.businessOrganisationsService
            .getVersions(this.selectedServicePointVersion!.businessOrganisation)
            .pipe(tap((bo) => this.initSelectedBusinessOrganisationVersion(bo))),
        ),
      )
      .subscribe();
  }

  ngOnDestroy() {
    this.stopPointSubscription?.unsubscribe();
  }

  private initStopPointVersioning() {
    this.maxValidity = VersionsHandlingService.getMaxValidity(this.stopPointVersions);
    this.selectedVersion = VersionsHandlingService.determineDefaultVersionByValidity(
      this.stopPointVersions,
    );
  }

  private initServicePointVersioning(servicePointVersions: ReadServicePointVersion[]) {
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
