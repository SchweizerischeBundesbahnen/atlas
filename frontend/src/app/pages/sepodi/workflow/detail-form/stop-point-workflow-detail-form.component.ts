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
import {
  StopPointWorkflowDetailFormGroup,
  StopPointWorkflowDetailFormGroupBuilder
} from "./stop-point-workflow-detail-form-group";
import {ValidationService} from "../../../../core/validation/validation.service";

@Component({
  selector: 'stop-point-workflow-detail-form',
  templateUrl: './stop-point-workflow-detail-form.component.html',
  styleUrls: ['./stop-point-workflow-detail-form.component.scss'],
  viewProviders: [{provide: ControlContainer, useExisting: NgForm}],
})
export class StopPointWorkflowDetailFormComponent implements OnInit {

  readonly emailValidator = [AtlasCharsetsValidator.email, AtlasFieldLengthValidator.length_100];

  @Input() stopPoint!: ReadServicePointVersion;
  @Input() form!: FormGroup<StopPointWorkflowDetailFormGroup>;

  stopPointBusinessOrganisation?: BusinessOrganisationVersion;
  boDescription!: string;

  constructor(
    private businessOrganisationsService: BusinessOrganisationsService,
    private businessOrganisationLanguageService: BusinessOrganisationLanguageService,
  ) {
  }

  ngOnInit() {
    this.initBusinessOrganisation();
  }

  private initBusinessOrganisation() {
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

  addExaminant() {
    const examinantsControl = this.form.controls.examinants;
    ValidationService.validateForm(examinantsControl);
    if (examinantsControl.valid) {
      examinantsControl.push(StopPointWorkflowDetailFormGroupBuilder.buildExaminantFormGroup());
    }
  }

  removeExaminant(index: number) {
    this.form.controls.examinants.removeAt(index);
  }
}
