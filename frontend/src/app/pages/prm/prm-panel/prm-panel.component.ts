import {Component} from '@angular/core';
import {
  BusinessOrganisationsService,
  BusinessOrganisationVersion,
  ReadServicePointVersion,
  ReadStopPointVersion,
} from '../../../api';
import {DateRange} from '../../../core/versioning/date-range';
import {VersionsHandlingService} from '../../../core/versioning/versions-handling.service';
import { ActivatedRoute, RouterLinkActive, RouterLink, RouterOutlet } from '@angular/router';
import {map, switchMap, tap} from 'rxjs/operators';
import {
  BusinessOrganisationLanguageService
} from '../../../core/form-components/bo-select/business-organisation-language.service';
import {PRM_TABS} from './prm-tabs';
import {takeUntilDestroyed} from '@angular/core/rxjs-interop';
import {PrmTabsService} from './prm-tabs.service';
import { DateRangeTextComponent } from '../../../core/versioning/date-range-text/date-range-text.component';
import { MatTabNav, MatTabLink, MatTabNavPanel } from '@angular/material/tabs';
import { NgFor } from '@angular/common';
import { SplitServicePointNumberPipe } from '../../../core/search-service-point/split-service-point-number.pipe';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
    selector: 'app-prm-panel',
    templateUrl: './prm-panel.component.html',
    styleUrls: ['./prm-panel.component.scss'],
    imports: [DateRangeTextComponent, MatTabNav, NgFor, MatTabLink, RouterLinkActive, RouterLink, MatTabNavPanel, RouterOutlet, SplitServicePointNumberPipe, TranslatePipe]
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
    private prmTabsService: PrmTabsService,
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
    this.prmTabsService.tabs.subscribe((tabs) => (this.tabs = tabs));
    this.prmTabsService.disableTabNavigation.subscribe(
      (disableTabNavigation) => (this.disableTabNavigation = disableTabNavigation),
    );
    this.prmTabsService.initTabs(stopPointVersions);
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
