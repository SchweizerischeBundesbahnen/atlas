import { Component, OnDestroy, OnInit } from '@angular/core';
import {
  BusinessOrganisationsService,
  BusinessOrganisationVersion,
  ReadServicePointVersion,
  ReadStopPointVersion,
  ServicePointsService,
} from '../../../api';
import { DateRange } from '../../../core/versioning/date-range';
import { Subject } from 'rxjs';
import { VersionsHandlingService } from '../../../core/versioning/versions-handling.service';
import { ActivatedRoute } from '@angular/router';
import { map, switchMap, takeUntil, tap } from 'rxjs/operators';
import { BusinessOrganisationLanguageService } from '../../../core/form-components/bo-select/business-organisation-language.service';
import { PrmMeanOfTransportHelper } from '../prm-mean-of-transport-helper';
import { PrmTab } from './prm-tab';

@Component({
  selector: 'app-prm-panel',
  templateUrl: './prm-panel.component.html',
  styleUrls: ['./prm-panel.component.scss'],
})
export class PrmPanelComponent implements OnDestroy, OnInit {
  selectedServicePointVersion!: ReadServicePointVersion;
  selectedBusinessOrganisation?: BusinessOrganisationVersion;
  servicePointVersions!: ReadServicePointVersion[];
  selectedVersion!: ReadStopPointVersion;
  maxValidity!: DateRange;
  boDescription!: string;
  isNew!: boolean;
  disableTabNavigation = false;
  stopPointVersions!: ReadStopPointVersion[];
  private ngUnsubscribe = new Subject<void>();

  tabs = PrmTab.tabs;

  constructor(
    private route: ActivatedRoute,
    private servicePointsService: ServicePointsService,
    private businessOrganisationsService: BusinessOrganisationsService,
    private businessOrganisationLanguageService: BusinessOrganisationLanguageService,
  ) {
    this.businessOrganisationLanguageService
      .languageChanged()
      .pipe(takeUntil(this.ngUnsubscribe))
      .subscribe(() => this.translateBoDescription());
  }

  ngOnInit() {
    this.route.data
      .pipe(
        takeUntil(this.ngUnsubscribe),
        map((next) => {
          this.servicePointVersions = next.servicePoints;
          this.stopPointVersions = next.stopPoints;
          this.initTabs(this.stopPointVersions);
          this.initServicePointVersioning(this.servicePointVersions);
        }),
        switchMap(() =>
          this.businessOrganisationsService
            .getVersions(this.selectedServicePointVersion.businessOrganisation)
            .pipe(
              takeUntil(this.ngUnsubscribe),
              tap((bo) => this.initSelectedBusinessOrganisationVersion(bo)),
            ),
        ),
      )
      .subscribe();
  }

  initTabs(stopPointVersions: ReadStopPointVersion[]) {
    if (stopPointVersions.length === 0) {
      this.disableTabNavigation = true;
      this.tabs = [PrmTab.STOP_POINT];
    } else {
      const isReduced = PrmMeanOfTransportHelper.isReduced(stopPointVersions[0].meansOfTransport);
      if (isReduced) {
        this.tabs = PrmTab.reducedTabs;
      }
    }
  }

  ngOnDestroy() {
    this.ngUnsubscribe?.unsubscribe();
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
