import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { LineType, LineVersionSnapshot, LineVersionV2 } from '../../../../api';
import { LineVersionSnapshotDetailComponent } from './line-version-snapshot-detail.component';
import { AppTestingModule } from '../../../../app.testing.module';
import { ErrorNotificationComponent } from '../../../../core/notification/error/error-notification.component';
import {
  adminPermissionServiceMock,
  MockAtlasButtonComponent,
  MockBoSelectComponent,
} from '../../../../app.testing.mocks';
import { LineDetailFormComponent } from '../../lines/detail/line-detail-form/line-detail-form.component';
import { LinkIconComponent } from '../../../../core/form-components/link-icon/link-icon.component';
import { EMPTY, of } from 'rxjs';
import { CommentComponent } from '../../../../core/form-components/comment/comment.component';
import { UserDetailInfoComponent } from '../../../../core/components/user-edit-info/user-detail-info.component';
import { AtlasLabelFieldComponent, InfoIconComponent, InfoLinkDirective } from '@atlas/form';
import { AtlasFieldErrorComponent } from '../../../../core/form-components/atlas-field-error/atlas-field-error.component';
import { TranslatePipe } from '@ngx-translate/core';
import { TextFieldComponent } from '../../../../core/form-components/text-field/text-field.component';
import { SelectComponent } from '../../../../core/form-components/select/select.component';
import { AtlasSpacerComponent } from '../../../../core/components/spacer/atlas-spacer.component';
import { DetailFooterComponent } from '../../../../core/components/detail-footer/detail-footer.component';
import { DetailPageContainerComponent } from '../../../../core/components/detail-page-container/detail-page-container.component';
import { DateRangeTextComponent } from '../../../../core/versioning/date-range-text/date-range-text.component';
import { DisplayDatePipe } from '../../../../core/pipe/display-date.pipe';
import { DetailPageContentComponent } from '../../../../core/components/detail-page-content/detail-page-content.component';
import { PermissionService } from '../../../../core/auth/permission/permission.service';
import { NgOptimizedImage } from '@angular/common';
import { DateRangeComponent } from '../../../../core/form-components/date-range/date-range.component';
import { DateIconComponent } from '../../../../core/form-components/date-icon/date-icon.component';
import { LineService } from '../../../../api/service/lidi/line.service';
import { LineWorkflowService } from '../../../../api/service/workflow/line-workflow.service';
import { beforeEach, describe, expect, it, type Mocked, vi } from 'vitest';

const lineVersionSnapshot: LineVersionSnapshot = {
  id: 1234,
  slnid: 'slnid',
  number: 'name',
  description: 'asdf',
  status: 'VALIDATED',
  validFrom: new Date('2021-06-01'),
  validTo: new Date('2029-06-01'),
  businessOrganisation: 'SBB',
  lineType: LineType.Orderly,
  parentObjectId: 1234,
  workflowId: 1,
  workflowStatus: 'ADDED',
  shortNumber: 'short',
  lineConcessionType: 'LINE_OF_A_ZONE_CONCESSION',
  offerCategory: 'ASC',
};

const lineVersion: LineVersionV2 = {
  id: 1234,
  slnid: 'slnid',
  number: 'name',
  description: 'asdf',
  status: 'VALIDATED',
  validFrom: new Date('2021-06-01'),
  validTo: new Date('2029-06-01'),
  businessOrganisation: 'SBB',
  lineType: LineType.Orderly,
  swissLineNumber: '13',
};

const mockData = {
  lineVersionSnapshot: lineVersionSnapshot,
};

describe('LineVersionSnapshotDetailComponent', () => {
  let component: LineVersionSnapshotDetailComponent;
  let fixture: ComponentFixture<LineVersionSnapshotDetailComponent>;
  let lineService: Mocked<Pick<LineService, 'getLineVersionsV2'>>;
  let lineWorkflowService: Mocked<Pick<LineWorkflowService, 'getWorkflow'>>;

  beforeEach(() => {
    lineService = {
      getLineVersionsV2: vi.fn(),
    };
    lineService.getLineVersionsV2.mockReturnValue(of([lineVersion]));

    lineWorkflowService = {
      getWorkflow: vi.fn(),
    };
    lineWorkflowService.getWorkflow.mockReturnValue(EMPTY);

    setupTestBed(lineService, lineWorkflowService, mockData);

    fixture = TestBed.createComponent(LineVersionSnapshotDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should be created', () => {
    expect(component).toBeTruthy();
  });

  it('should match line version and snapshot version', () => {
    fixture.detectChanges();
    expect(component.versionAlreadyExists).toBeTruthy();
  });
});

function setupTestBed(
  lineService: Partial<LineService>,
  lineWorkflowService: Partial<LineWorkflowService>,
  data: { lineVersionSnapshot: string | LineVersionSnapshot }
) {
  TestBed.configureTestingModule({
    imports: [
      AppTestingModule,
      NgOptimizedImage,
      LineVersionSnapshotDetailComponent,
      LineDetailFormComponent,
      MockBoSelectComponent,
      ErrorNotificationComponent,
      CommentComponent,
      UserDetailInfoComponent,
      AtlasLabelFieldComponent,
      AtlasFieldErrorComponent,
      TextFieldComponent,
      SelectComponent,
      AtlasSpacerComponent,
      InfoIconComponent,
      LinkIconComponent,
      InfoLinkDirective,
      MockAtlasButtonComponent,
      DetailFooterComponent,
      DetailPageContainerComponent,
      DetailPageContentComponent,
      DateRangeTextComponent,
      DisplayDatePipe,
      DateRangeComponent,
      DateIconComponent,
    ],
    providers: [
      { provide: FormBuilder },
      { provide: LineService, useValue: lineService },
      { provide: LineWorkflowService, useValue: lineWorkflowService },
      { provide: PermissionService, useValue: adminPermissionServiceMock },
      { provide: ActivatedRoute, useValue: { snapshot: { data: data } } },
      { provide: TranslatePipe },
    ],
  })
    .compileComponents()
    .then();
}
