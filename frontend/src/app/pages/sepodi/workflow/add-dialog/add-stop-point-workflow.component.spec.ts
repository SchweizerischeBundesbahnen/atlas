import {ComponentFixture, TestBed} from '@angular/core/testing';

import {AddStopPointWorkflowComponent} from './add-stop-point-workflow.component';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {TranslatePipe} from '@ngx-translate/core';
import {WorkflowFormComponent} from "../../../../core/workflow/workflow-form/workflow-form.component";
import {WorkflowCheckFormComponent} from "../../../../core/workflow/workflow-check-form/workflow-check-form.component";
import {CommentComponent} from "../../../../core/form-components/comment/comment.component";
import {ErrorNotificationComponent} from "../../../../core/notification/error/error-notification.component";
import {MockAtlasButtonComponent} from "../../../../app.testing.mocks";
import {AppTestingModule} from "../../../../app.testing.module";
import {NotificationService} from "../../../../core/notification/notification.service";
import {FormModule} from "../../../../core/module/form.module";
import {AddStopPointWorkflowDialogData} from "./add-stop-point-workflow-dialog-data";
import {BERN_WYLEREGG} from "../../../../../test/data/service-point";
import {StopPointWorkflowDetailFormComponent} from "../detail-form/stop-point-workflow-detail-form.component";
import {StringListComponent} from "../../../../core/form-components/string-list/string-list.component";
import {DisplayDatePipe} from "../../../../core/pipe/display-date.pipe";
import {SplitServicePointNumberPipe} from "../../../../core/search-service-point/split-service-point-number.pipe";
import {DetailPageContentComponent} from "../../../../core/components/detail-page-content/detail-page-content.component";
import {DetailPageContainerComponent} from "../../../../core/components/detail-page-container/detail-page-container.component";
import {DetailFooterComponent} from "../../../../core/components/detail-footer/detail-footer.component";

const dialogRefSpy = jasmine.createSpyObj(['close']);
const notificationServiceSpy = jasmine.createSpyObj(['success']);

const workflowDialogData: AddStopPointWorkflowDialogData = {
  title: '',
  message: '',
  stopPoint: BERN_WYLEREGG,
}

describe('AddStopPointWorkflowComponent', () => {
  let component: AddStopPointWorkflowComponent;
  let fixture: ComponentFixture<AddStopPointWorkflowComponent>;

  beforeEach(async () => {
    TestBed.configureTestingModule({
      declarations: [
        AddStopPointWorkflowComponent,
        WorkflowFormComponent,
        WorkflowCheckFormComponent,
        CommentComponent,
        ErrorNotificationComponent,
        MockAtlasButtonComponent,
        StopPointWorkflowDetailFormComponent,
        StringListComponent,
        MockAtlasButtonComponent,
        DisplayDatePipe,
        SplitServicePointNumberPipe,
        DetailPageContentComponent,
        DetailPageContainerComponent,
        DetailFooterComponent,
      ],
      imports: [AppTestingModule, FormModule],
      providers: [
        {
          provide: MAT_DIALOG_DATA,
          useValue: workflowDialogData,
        },
        {provide: MatDialogRef, useValue: dialogRefSpy},
        {provide: NotificationService, useValue: notificationServiceSpy},
        {provide: TranslatePipe},
      ],
    })
      .compileComponents()
      .then();

    fixture = TestBed.createComponent(AddStopPointWorkflowComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

});
