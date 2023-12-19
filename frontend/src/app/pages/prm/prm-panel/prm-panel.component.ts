import { Component } from '@angular/core';
import {
  BusinessOrganisationsService,
  BusinessOrganisationVersion,
  ReadServicePointVersion,
  ReadStopPointVersion,
} from '../../../api';
import { DateRange } from '../../../core/versioning/date-range';
import { VersionsHandlingService } from '../../../core/versioning/versions-handling.service';
import { ActivatedRoute } from '@angular/router';
import { map, switchMap, tap } from 'rxjs/operators';
import { BusinessOrganisationLanguageService } from '../../../core/form-components/bo-select/business-organisation-language.service';
import { PrmMeanOfTransportHelper } from '../util/prm-mean-of-transport-helper';
import { PRM_REDUCED_TABS, PRM_TABS, PrmTab } from './prm-tab';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';

@Component({
  selector: 'app-prm-panel',
  templateUrl: './prm-panel.component.html',
  styleUrls: ['./prm-panel.component.scss'],
})
export class PrmPanelComponent {
  selectedServicePointVersion!: ReadServicePointVersion;
  selectedBusinessOrganisation?: BusinessOrganisationVersion;
  selectedVersion!: ReadStopPointVersion;
  maxValidity!: DateRange;
  boDescription!: string;
  isNew!: boolean;
  disableTabNavigation = false;

  tabs = PRM_TABS;

  constructor(
    private route: ActivatedRoute,
    private businessOrganisationsService: BusinessOrganisationsService,
    private businessOrganisationLanguageService: BusinessOrganisationLanguageService,
  ) {
    this.businessOrganisationLanguageService
      .languageChanged()
      .pipe(takeUntilDestroyed())
      .subscribe(() => this.translateBoDescription());

    this.route.data
      .pipe(
        takeUntilDestroyed(),
        map((data) => {
          this.initTabs(data.stopPoints);
          this.initServicePointVersioning(data.servicePoints);
        }),
        switchMap(() => this.initBusinessOrganisationHeaderPanel()),
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
        this.tabs = PRM_REDUCED_TABS;
      }
    }
  }

  private initBusinessOrganisationHeaderPanel() {
    return this.businessOrganisationsService
      .getVersions(this.selectedServicePointVersion.businessOrganisation)
      .pipe(tap((bo) => this.initSelectedBusinessOrganisationVersion(bo)));
  }

  private initServicePointVersioning(servicePointVersions: ReadServicePointVersion[]) {
    this.maxValidity = VersionsHandlingService.getMaxValidity(servicePointVersions);
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
