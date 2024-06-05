import {ComponentFixture, TestBed} from "@angular/core/testing";
import {AppTestingModule} from "../../../../app.testing.module";
import {FormModule} from "../../../../core/module/form.module";
import {StopPointWorkflowDetailFormComponent} from "./stop-point-workflow-detail-form.component";
import {DisplayDatePipe} from "../../../../core/pipe/display-date.pipe";
import {SplitServicePointNumberPipe} from "../../../../core/search-service-point/split-service-point-number.pipe";
import {BERN_WYLEREGG} from "../../../../../test/data/service-point";
import {StopPointWorkflowDetailFormGroupBuilder} from "./stop-point-workflow-detail-form-group";
import {ReadStopPointWorkflow} from "../../../../api";
import {TranslatePipe} from "@ngx-translate/core";
import {StringListComponent} from "../../../../core/form-components/string-list/string-list.component";
import {MockAtlasButtonComponent} from "../../../../app.testing.mocks";

const workflow: ReadStopPointWorkflow = {
  versionId: 1,
  sboid: 'sboid',
  designationOfficial: 'New Name',
};

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
      ],
      imports: [AppTestingModule, FormModule],
      providers: [
        { provide: TranslatePipe },
      ],
    }).compileComponents().then();

    fixture = TestBed.createComponent(StopPointWorkflowDetailFormComponent);
    component = fixture.componentInstance;

    component.stopPoint = BERN_WYLEREGG;
    component.form = StopPointWorkflowDetailFormGroupBuilder.buildFormGroup(workflow);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
