import {Component, Input} from '@angular/core';
import {ReadServicePointVersion, ReadStopPointWorkflow} from "../../../../api";
import {ControlContainer, FormGroup, NgForm} from "@angular/forms";
import {AtlasCharsetsValidator} from "../../../../core/validation/charsets/atlas-charsets-validator";
import {AtlasFieldLengthValidator} from "../../../../core/validation/field-lengths/atlas-field-length-validator";
import {StopPointWorkflowDetailFormGroup, StopPointWorkflowDetailFormGroupBuilder} from "./stop-point-workflow-detail-form-group";
import {ValidationService} from "../../../../core/validation/validation.service";
import {Pages} from "../../../pages";

@Component({
  selector: 'stop-point-workflow-detail-form',
  templateUrl: './stop-point-workflow-detail-form.component.html',
  styleUrls: ['./stop-point-workflow-detail-form.component.scss'],
  viewProviders: [{provide: ControlContainer, useExisting: NgForm}],
})
export class StopPointWorkflowDetailFormComponent {

  readonly emailValidator = [AtlasCharsetsValidator.email, AtlasFieldLengthValidator.length_100];

  @Input() stopPoint!: ReadServicePointVersion;
  @Input() oldDesignation?: string;
  @Input() form!: FormGroup<StopPointWorkflowDetailFormGroup>;
  @Input() currentWorkflow?: ReadStopPointWorkflow;

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

  goToSwissTopo() {
    const n = this.stopPoint.servicePointGeolocation!.lv95.north!;
    const e = this.stopPoint.servicePointGeolocation!.lv95.east!;
    window.open(`https://map.geo.admin.ch/?lang=de&topic=ech&bgLayer=ch.swisstopo.pixelkarte-farbe&layers=ch.swisstopo.swissboundaries3d-gemeinde-flaeche.fill,ch.swisstopo-vd.ortschaftenverzeichnis_plz,ch.swisstopo.amtliches-strassenverzeichnis,ch.bav.haltestellen-oev&layers_opacity=1,0.75,0.85,1&layers_timestamp=2024,,,&E=${e}&N=${n}&zoom=10&layers_visibility=false,true,false,true&crosshair=marker&E=${e}&N=${n}`, "_blank");
  }

  goToAtlasStopPoint() {
    window.open(Pages.SEPODI.path + '/' + Pages.SERVICE_POINTS.path + '/' + this.stopPoint?.number.number, "_blank");
  }
}
