import {ComponentFixture, TestBed} from '@angular/core/testing';
import {StopPointWorkflowDetailComponent} from "./stop-point-workflow-detail.component";
import {AppTestingModule} from "../../../../app.testing.module";
import {FormModule} from "../../../../core/module/form.module";
import {ActivatedRoute} from "@angular/router";
import {BERN_WYLEREGG} from "../../../../../test/data/service-point";
import {ReadStopPointWorkflow} from "../../../../api";
import {StopPointWorkflowDetailFormComponent} from "../detail-form/stop-point-workflow-detail-form.component";
import {StringListComponent} from "../../../../core/form-components/string-list/string-list.component";
import {MockAtlasButtonComponent} from "../../../../app.testing.mocks";
import {DisplayDatePipe} from "../../../../core/pipe/display-date.pipe";
import {SplitServicePointNumberPipe} from "../../../../core/search-service-point/split-service-point-number.pipe";
import {TranslatePipe} from "@ngx-translate/core";
import {DetailPageContentComponent} from "../../../../core/components/detail-page-content/detail-page-content.component";
import {DetailPageContainerComponent} from "../../../../core/components/detail-page-container/detail-page-container.component";
import {DetailFooterComponent} from "../../../../core/components/detail-footer/detail-footer.component";
import {AtlasSpacerComponent} from "../../../../core/components/spacer/atlas-spacer.component";
import {StopPointWorkflowDetailData} from "./stop-point-workflow-detail-resolver.service";
import {UserDetailInfoComponent} from "../../../../core/components/base-detail/user-edit-info/user-detail-info.component";

const workflow:ReadStopPointWorkflow = {
  versionId: 1000,
};
const workflowData: StopPointWorkflowDetailData={
  workflow: workflow,
  servicePoint: [BERN_WYLEREGG]
}

const activatedRoute = {
  snapshot: {
    data: {
      workflow: workflowData,
    },
  },
};

describe('StopPointWorkflowDetailComponent', () => {
  let component: StopPointWorkflowDetailComponent;
  let fixture: ComponentFixture<StopPointWorkflowDetailComponent>;

  beforeEach(async () => {
    TestBed.configureTestingModule({
      declarations: [
        StopPointWorkflowDetailComponent,
        StopPointWorkflowDetailFormComponent,
        StringListComponent,
        MockAtlasButtonComponent,
        DisplayDatePipe,
        SplitServicePointNumberPipe,
        DetailPageContentComponent,
        DetailPageContainerComponent,
        DetailFooterComponent,
        AtlasSpacerComponent,
        UserDetailInfoComponent,
      ],
      imports: [AppTestingModule, FormModule],
      providers: [
        {provide: ActivatedRoute, useValue: activatedRoute},
        {provide: TranslatePipe}
      ]
    }).compileComponents().then();

    fixture = TestBed.createComponent(StopPointWorkflowDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

});
