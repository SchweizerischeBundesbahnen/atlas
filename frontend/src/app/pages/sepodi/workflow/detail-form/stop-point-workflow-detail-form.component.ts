import {Component, Input, OnInit} from '@angular/core';
import {BusinessOrganisationsService, BusinessOrganisationVersion, ReadServicePointVersion} from "../../../../api";
import {VersionsHandlingService} from "../../../../core/versioning/versions-handling.service";
import {
  BusinessOrganisationLanguageService
} from "../../../../core/form-components/bo-select/business-organisation-language.service";
import {take} from "rxjs";
import {ControlContainer, FormGroup, NgForm} from "@angular/forms";
import {AtlasCharsetsValidator} from "../../../../core/validation/charsets/atlas-charsets-validator";
import {AtlasFieldLengthValidator} from "../../../../core/validation/field-lengths/atlas-field-length-validator";
import {StopPointWorkflowDetailFormGroup} from "./stop-point-workflow-detail-form-group";

@Component({
  selector: 'stop-point-workflow-detail-form',
  templateUrl: './stop-point-workflow-detail-form.component.html',
  viewProviders: [{provide: ControlContainer, useExisting: NgForm}],
})
export class StopPointWorkflowDetailFormComponent implements OnInit {

  @Input() stopPoint!: ReadServicePointVersion;

  stopPointBusinessOrganisation?: BusinessOrganisationVersion;
  boDescription!: string;

  @Input() form!: FormGroup<StopPointWorkflowDetailFormGroup>;

  readonly emailValidator = [AtlasCharsetsValidator.email, AtlasFieldLengthValidator.length_100];

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
      .pipe(take(1))
      .subscribe((businessOrganisation) => this.initSelectedBusinessOrganisationVersion(businessOrganisation));
  }

  private initSelectedBusinessOrganisationVersion(businessOrganisation: BusinessOrganisationVersion[]) {
    this.stopPointBusinessOrganisation = VersionsHandlingService.determineDefaultVersionByValidity(businessOrganisation);
    this.translateBoDescription();
  }

  private translateBoDescription() {
    this.boDescription = this.stopPointBusinessOrganisation![this.businessOrganisationLanguageService.getCurrentLanguageDescription()];
  }

}
