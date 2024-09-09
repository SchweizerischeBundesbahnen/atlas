import {ComponentFixture, TestBed} from "@angular/core/testing";
import {Router} from "@angular/router";
import {BusinessOrganisationsService, ReadStopPointWorkflow} from "../../../../../api";
import {StopPointWorkflowDetailFormComponent} from "./stop-point-workflow-detail-form.component";
import {StringListComponent} from "../../../../../core/form-components/string-list/string-list.component";
import {MockAtlasButtonComponent} from "../../../../../app.testing.mocks";
import {DisplayDatePipe} from "../../../../../core/pipe/display-date.pipe";
import {SplitServicePointNumberPipe} from "../../../../../core/search-service-point/split-service-point-number.pipe";
import {AtlasSpacerComponent} from "../../../../../core/components/spacer/atlas-spacer.component";
import {AppTestingModule} from "../../../../../app.testing.module";
import {TranslatePipe} from "@ngx-translate/core";
import {BERN_WYLEREGG} from "../../../../../../test/data/service-point";
import {StopPointWorkflowDetailFormGroupBuilder} from "./stop-point-workflow-detail-form-group";
import {FormModule} from "../../../../../core/module/form.module";

const workflow: ReadStopPointWorkflow = {
  versionId: 1,
  sloid: 'ch:1:sloid:8000',
  workflowComment: "No comment"
};

let router: Router;

describe('StopPointWorkflowDetailFormComponent', () => {
  let component: StopPointWorkflowDetailFormComponent;
  let fixture: ComponentFixture<StopPointWorkflowDetailFormComponent>;

  beforeEach(async () => {
    TestBed.configureTestingModule({
      declarations: [
        StopPointWorkflowDetailFormComponent,
        StringListComponent,
        MockAtlasButtonComponent,
        DisplayDatePipe,
        SplitServicePointNumberPipe,
        AtlasSpacerComponent,
      ],
      imports: [AppTestingModule, FormModule],
      providers: [
        {provide: TranslatePipe},
        {provide: BusinessOrganisationsService},
      ],
    }).compileComponents().then();

    fixture = TestBed.createComponent(StopPointWorkflowDetailFormComponent);
    component = fixture.componentInstance;

    component.stopPoint = BERN_WYLEREGG;
    component.form = StopPointWorkflowDetailFormGroupBuilder.buildFormGroup(workflow);
    fixture.detectChanges();
    router = TestBed.inject(Router);
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should have one examinant per default', () => {
    expect(component.form.controls.examinants.length).toBe(1);
  });

  it('should add second examinant', () => {
    const firstExaminant = component.form.controls.examinants.at(0);
    firstExaminant.controls.firstName.setValue('firstName');
    firstExaminant.controls.lastName.setValue('lastName');
    firstExaminant.controls.personFunction.setValue('personFunction');
    firstExaminant.controls.organisation.setValue('organisation');
    firstExaminant.controls.mail.setValue('mail@sbb.ch');

    component.addExaminant();
    expect(component.form.controls.examinants.length).toBe(2);
  });

  it('should remove examinant', () => {
    const firstExaminant = component.form.controls.examinants.at(0);
    firstExaminant.controls.firstName.setValue('firstName');
    firstExaminant.controls.lastName.setValue('lastName');
    firstExaminant.controls.personFunction.setValue('personFunction');
    firstExaminant.controls.organisation.setValue('organisation');
    firstExaminant.controls.mail.setValue('mail@sbb.ch');

    component.addExaminant();
    expect(component.form.controls.examinants.length).toBe(2);

    component.form.controls.examinants.at(0).disable();
    component.form.controls.examinants.at(1).disable();
    component.removeExaminant(0);
    expect(component.form.controls.examinants.length).toBe(1);
    component.removeExaminant(0);
    expect(component.form.controls.examinants.length).toBe(0);
  });

  it('should go to swisstopo', () => {
    spyOn(window, 'open');

    component.goToSwissTopo();
    expect(window.open).toHaveBeenCalledWith('https://map.geo.admin.ch/?lang=de&topic=ech&bgLayer=ch.swisstopo.pixelkarte-farbe&layers=ch.swisstopo.swissboundaries3d-gemeinde-flaeche.fill,ch.swisstopo-vd.ortschaftenverzeichnis_plz,ch.swisstopo.amtliches-strassenverzeichnis,ch.bav.haltestellen-oev&layers_opacity=1,0.75,0.85,1&layers_timestamp=2024,,,&E=2600783&N=1201099&zoom=10&layers_visibility=false,true,false,true&crosshair=marker&E=2600783&N=1201099', '_blank');
  });

  it('should go to atlas', () => {
    const url = 'http://localhost:4200/service-point-directory/service-points/8500039/service-point?id=1085';
    spyOn(window, 'open');
    spyOn(router, 'serializeUrl').and.returnValue(url);

    component.goToAtlasStopPoint();
    expect(router.serializeUrl).toHaveBeenCalled();
    expect(window.open).toHaveBeenCalledWith(url, '_blank');

  });
});
