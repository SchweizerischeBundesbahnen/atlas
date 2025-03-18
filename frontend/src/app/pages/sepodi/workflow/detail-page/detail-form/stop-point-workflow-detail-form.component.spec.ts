import {ComponentFixture, TestBed} from "@angular/core/testing";
import {Router} from "@angular/router";
import {ReadStopPointWorkflow, StopPointPerson, StopPointWorkflowService} from "../../../../../api";
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
import {StopPointWorkflowExaminantsTableComponent} from "../examinant-table/stop-point-workflow-examinants-table.component";
import {of} from "rxjs";

const workflow: ReadStopPointWorkflow = {
  versionId: 1,
  sloid: 'ch:1:sloid:8000',
  workflowComment: "No comment"
};

let router: Router;

const defaultExaminants: StopPointPerson[]=[
  {
    organisation: "SKI",
    personFunction: "Fachstelle atlas",
    mail: "atlas@sbb.ch",
    defaultExaminant: true
  }
]

const stopPointWorkflowService = jasmine.createSpyObj('stopPointWorkflowService', {
  getExaminants: of(defaultExaminants)
})

describe('StopPointWorkflowDetailFormComponent', () => {
  let component: StopPointWorkflowDetailFormComponent;
  let fixture: ComponentFixture<StopPointWorkflowDetailFormComponent>;

  beforeEach(async () => {
    TestBed.configureTestingModule({
    imports: [AppTestingModule, FormModule, StopPointWorkflowDetailFormComponent,
        StopPointWorkflowExaminantsTableComponent,
        StringListComponent,
        MockAtlasButtonComponent,
        DisplayDatePipe,
        SplitServicePointNumberPipe,
        AtlasSpacerComponent],
    providers: [
        { provide: TranslatePipe },
        { provide: StopPointWorkflowService, useValue: stopPointWorkflowService },
    ],
}).compileComponents().then();

    fixture = TestBed.createComponent(StopPointWorkflowDetailFormComponent);
    component = fixture.componentInstance;

    component.stopPoint = BERN_WYLEREGG;
    component.form = StopPointWorkflowDetailFormGroupBuilder.buildFormGroup(workflow);
    fixture.detectChanges();
    router = TestBed.inject(Router);
  });

  it('should create without currentWorkflow', () => {
    expect(component).toBeTruthy();
    expect(component.currentWorkflow).toBeUndefined();

    expect(stopPointWorkflowService.getExaminants).toHaveBeenCalled();

    // Default examinant disabled state
    expect(component.form.controls.examinants.length).toBe(2);
    expect(component.form.controls.examinants.at(0).controls.mail.value).toBe("atlas@sbb.ch");
    expect(component.form.controls.examinants.at(0).enabled).toBe(false);

    // Default empty row enabled
    expect(component.form.controls.examinants.at(1).controls.mail.value).toBeFalsy();
    expect(component.form.controls.examinants.at(1).enabled).toBe(true);
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
