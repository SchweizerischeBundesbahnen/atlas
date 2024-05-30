import {Component, Input, OnInit} from '@angular/core';
import {BusinessOrganisationsService, BusinessOrganisationVersion, ReadServicePointVersion} from "../../../../api";
import {VersionsHandlingService} from "../../../../core/versioning/versions-handling.service";
import {tap} from "rxjs/operators";
import {
  BusinessOrganisationLanguageService
} from "../../../../core/form-components/bo-select/business-organisation-language.service";

@Component({
  selector: 'stop-point-workflow-detail-form',
  templateUrl: './stop-point-workflow-detail-form.component.html',
})
export class StopPointWorkflowDetailFormComponent implements OnInit {

  @Input() stopPoint!: ReadServicePointVersion;
  stopPointBusinessOrganisation!: BusinessOrganisationVersion;
  boDescription!: string;

  constructor(
    private businessOrganisationsService: BusinessOrganisationsService,
    private businessOrganisationLanguageService: BusinessOrganisationLanguageService,
  ) {
  }

  ngOnInit() {
    this.initBusinessOrganisationHeaderPanel();
  }

  private initBusinessOrganisationHeaderPanel() {
    return this.businessOrganisationsService
      .getVersions(this.stopPoint.businessOrganisation)
      .pipe(tap((bo) => this.initSelectedBusinessOrganisationVersion(bo)));
  }

  private initSelectedBusinessOrganisationVersion(bos: BusinessOrganisationVersion[]) {
    this.stopPointBusinessOrganisation = VersionsHandlingService.determineDefaultVersionByValidity(bos);

    this.translateBoDescription();
  }

  private translateBoDescription() {
    this.boDescription =
      this.stopPointBusinessOrganisation![
        this.businessOrganisationLanguageService.getCurrentLanguageDescription()
        ];
  }

}
