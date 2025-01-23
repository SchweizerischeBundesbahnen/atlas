import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { of, throwError } from 'rxjs';
import {
  LinesService,
  LineType,
  LineVersionV2,
  LineVersionWorkflow,
  Status,
  WorkflowProcessingStatus,
} from '../../../../api';
import { LineDetailComponent } from './line-detail.component';
import { HttpErrorResponse } from '@angular/common/http';
import { AppTestingModule } from '../../../../app.testing.module';
import { ErrorNotificationComponent } from '../../../../core/notification/error/error-notification.component';
import { InfoIconComponent } from '../../../../core/form-components/info-icon/info-icon.component';
import { adminPermissionServiceMock } from '../../../../app.testing.mocks';
import { LineDetailFormComponent } from './line-detail-form/line-detail-form.component';
import { CommentComponent } from '../../../../core/form-components/comment/comment.component';
import { LinkIconComponent } from '../../../../core/form-components/link-icon/link-icon.component';
import { FormModule } from '../../../../core/module/form.module';
import { TranslatePipe } from '@ngx-translate/core';
import { PermissionService } from '../../../../core/auth/permission/permission.service';
import { SublineDetailComponent } from '../../sublines/detail/subline-detail.component';
import { ValidityService } from '../../../sepodi/validity/validity.service';
import moment from 'moment';
import { AtlasLabelFieldComponent } from '../../../../core/form-components/atlas-label-field/atlas-label-field.component';
import { AtlasFieldErrorComponent } from '../../../../core/form-components/atlas-field-error/atlas-field-error.component';
import { TextFieldComponent } from '../../../../core/form-components/text-field/text-field.component';
import { SelectComponent } from '../../../../core/form-components/select/select.component';
import { AtlasSpacerComponent } from '../../../../core/components/spacer/atlas-spacer.component';
import { DetailPageContainerComponent } from '../../../../core/components/detail-page-container/detail-page-container.component';
import { DetailPageContentComponent } from '../../../../core/components/detail-page-content/detail-page-content.component';
import { DetailFooterComponent } from '../../../../core/components/detail-footer/detail-footer.component';
import { AtlasButtonComponent } from '../../../../core/components/button/atlas-button.component';
import { UserDetailInfoComponent } from '../../../../core/components/base-detail/user-edit-info/user-detail-info.component';
import { SwitchVersionComponent } from '../../../../core/components/switch-version/switch-version.component';
import { DateRangeComponent } from '../../../../core/form-components/date-range/date-range.component';
import { DateRangeTextComponent } from '../../../../core/versioning/date-range-text/date-range-text.component';
import { DateIconComponent } from '../../../../core/form-components/date-icon/date-icon.component';
import { DisplayDatePipe } from '../../../../core/pipe/display-date.pipe';
import { Component, Input } from '@angular/core';
import { Record } from '../../../../core/components/base-detail/record';
import { Page } from '../../../../core/model/page';
import {DialogService} from "../../../../core/components/dialog/dialog.service";
import {WorkflowComponent} from "../../../../core/workflow/workflow.component";

@Component({
  selector: 'app-coverage',
  template: '<p>Mock Product Editor Component</p>',
})
class MockAppCoverageComponent {
  @Input() pageType!: Record;
  @Input() currentRecord!: Page;
}

@Component({
  selector: 'app-subline-table',
  template: '<p>Mock subline table Component</p>',
})
export class MockSublineTableComponent {
  @Input() mainLineSlnid!: string;
}

const lineVersion: LineVersionV2 = {
  lineConcessionType: 'CANTONALLY_APPROVED_LINE',
  offerCategory: 'ASC',
  id: 1234,
  slnid: 'slnid',
  number: 'name',
  description: 'asdf',
  status: 'VALIDATED',
  validFrom: new Date('2021-06-01'),
  validTo: new Date('2029-06-01'),
  businessOrganisation: 'SBB',
  swissLineNumber: 'L1',
  lineType: LineType.Orderly,
  lineVersionWorkflows: new Set<LineVersionWorkflow>(),
};

const error = new HttpErrorResponse({
  status: 404,
  error: {
    message: 'Not found',
    details: [
      {
        message:
          'Number 111 already taken from 2020-12-12 to 2026-12-12 by ch:1:ttfnid:1001720',
        field: 'number',
        displayInfo: {
          code: 'TTFN.CONFLICT.NUMBER',
          parameters: [
            {
              key: 'number',
              value: '111',
            },
            {
              key: 'validFrom',
              value: '2020-12-12',
            },
            {
              key: 'validTo',
              value: '2026-12-12',
            },
            {
              key: 'ttfnid',
              value: 'ch:1:ttfnid:1001720',
            },
          ],
        },
      },
    ],
  },
});

let component: LineDetailComponent;
let fixture: ComponentFixture<LineDetailComponent>;
let router: Router;

const validityService = jasmine.createSpyObj<ValidityService>([
  'initValidity',
  'updateValidity',
  'validate',
]);
validityService.validate.and.returnValue(of(true));

const dialogService = jasmine.createSpyObj<DialogService>('DialogService', {confirm: of(true)});

describe('LineDetailComponent for existing lineVersion', () => {
  const mockLinesService = jasmine.createSpyObj('linesService', [
    'updateLineVersion',
    'deleteLines',
  ]);
  const mockData = {
    lineDetail: [lineVersion],
  };

  beforeEach(() => {
    setupTestBed(mockLinesService, mockData);
    fixture = TestBed.createComponent(LineDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    router = TestBed.inject(Router);
  });

  it('should be created', () => {
    expect(component).toBeTruthy();
  });

  it('should disable form parts when in review', () => {
    lineVersion.status = Status.InReview;
    fixture.detectChanges();

    expect(component.form.enabled).toBeFalse();
    component.toggleEdit();
    expect(component.form.enabled).toBeTrue();

    expect(component.form.controls.validFrom.enabled).toBeFalse();
    expect(component.form.controls.validTo.enabled).toBeFalse();
    expect(component.form.controls.lineType.enabled).toBeFalse();
  });

  it('should not disable form parts when in draft/validated', () => {
    lineVersion.status = Status.Draft;
    fixture.detectChanges();

    component.toggleEdit();

    expect(component.form.controls.validFrom.enabled).toBeTrue();
    expect(component.form.controls.validTo.enabled).toBeTrue();
    expect(component.form.controls.lineType.enabled).toBeTrue();
  });

  it('should update LineVersion successfully', () => {
    mockLinesService.updateLineVersion.and.returnValue(of(lineVersion));
    spyOn(router, 'navigate').and.returnValue(Promise.resolve(true));

    component.toggleEdit();
    component.form.controls.description.setValue("UpdatedDescription");

    component.save();
    fixture.detectChanges();

    const snackBarContainer = fixture.nativeElement.offsetParent.querySelector(
      'mat-snack-bar-container'
    );
    expect(snackBarContainer).toBeDefined();
    expect(snackBarContainer.textContent.trim()).toBe(
      'LIDI.LINE.NOTIFICATION.EDIT_SUCCESS'
    );
    expect(snackBarContainer.classList).toContain('success');
    expect(router.navigate).toHaveBeenCalled();
  });

  it('should not update Version', () => {
    mockLinesService.updateLineVersion.and.returnValue(throwError(() => error));

    component.toggleEdit();
    component.form.controls.description.setValue("UpdatedDescription");

    component.save();
    fixture.detectChanges();

    expect(component.form.enabled).toBeTrue();
  });

  it('should delete LineVersion successfully', () => {
    mockLinesService.deleteLines.and.returnValue(of({}));
    spyOn(router, 'navigate').and.returnValue(Promise.resolve(true));

    component.delete();
    fixture.detectChanges();

    const snackBarContainer = fixture.nativeElement.offsetParent.querySelector(
      'mat-snack-bar-container'
    );
    expect(snackBarContainer).toBeDefined();
    expect(snackBarContainer.textContent.trim()).toBe(
      'LIDI.LINE.NOTIFICATION.DELETE_SUCCESS'
    );
    expect(snackBarContainer.classList).toContain('success');
    expect(router.navigate).toHaveBeenCalled();
  });
});

describe('LineDetailComponent for new lineVersion', () => {
  const mockLinesService = jasmine.createSpyObj('linesService', [
    'createLineVersionV2',
  ]);
  const mockData = {
    lineDetail: [],
  };

  beforeEach(() => {
    setupTestBed(mockLinesService, mockData);

    fixture = TestBed.createComponent(LineDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    router = TestBed.inject(Router);
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('create new Version', () => {
    it('successfully', () => {
      spyOn(router, 'navigate').and.returnValue(Promise.resolve(true));
      mockLinesService.createLineVersionV2.and.returnValue(of(lineVersion));

      component.form.patchValue({
        lineConcessionType: 'CANTONALLY_APPROVED_LINE',
        offerCategory: 'ASC',
        number: 'name',
        description: 'asdf',
        validFrom: moment(),
        validTo: moment(),
        businessOrganisation: 'SBB',
        swissLineNumber: 'L1',
        lineType: LineType.Orderly,
      });

      component.save();
      fixture.detectChanges();

      const snackBarContainer =
        fixture.nativeElement.offsetParent.querySelector(
          'mat-snack-bar-container'
        );
      expect(snackBarContainer).toBeDefined();
      expect(snackBarContainer.textContent.trim()).toBe(
        'LIDI.LINE.NOTIFICATION.ADD_SUCCESS'
      );
      expect(snackBarContainer.classList).toContain('success');
      expect(router.navigate).toHaveBeenCalled();
    });

    it('displaying error', () => {
      mockLinesService.createLineVersionV2.and.returnValue(
        throwError(() => error)
      );
      component.save();
      fixture.detectChanges();

      expect(component.form.enabled).toBeTrue();
    });
  });

  describe('Show snapshot history', () => {
    it('Should show snapshot history without workflow but orderly and validated', () => {
      //given
      lineVersion.status = Status.Validated;
      lineVersion.lineType = LineType.Orderly;
      lineVersion.lineVersionWorkflows?.clear();
      fixture.componentInstance.selectedVersion = lineVersion;
      //when
      const result = fixture.componentInstance.showSnapshotHistoryLink();
      //then
      expect(result).toBeTruthy();
    });

    it('Should not show snapshot history without workflow when Temporary and validated', () => {
      //given
      lineVersion.status = Status.Validated;
      lineVersion.lineType = LineType.Temporary;
      lineVersion.lineVersionWorkflows?.clear();
      fixture.componentInstance.selectedVersion = lineVersion;
      //when
      const result = fixture.componentInstance.showSnapshotHistoryLink();
      //then
      expect(result).toBeFalsy();
    });

    it('Should not show snapshot history without workflow when Operational and validated', () => {
      //given
      lineVersion.status = Status.Validated;
      lineVersion.lineType = LineType.Operational;
      lineVersion.lineVersionWorkflows?.clear();
      fixture.componentInstance.selectedVersion = lineVersion;
      //when
      const result = fixture.componentInstance.showSnapshotHistoryLink();
      //then
      expect(result).toBeFalsy();
    });

    it('Should show snapshot history with workflow evaluated', () => {
      //given
      const lineWorkflow: LineVersionWorkflow = {
        workflowId: 1,
        workflowProcessingStatus: WorkflowProcessingStatus.Evaluated,
      };
      lineVersion.lineVersionWorkflows?.add(lineWorkflow);
      fixture.componentInstance.selectedVersion = lineVersion;

      //when
      const result = fixture.componentInstance.showSnapshotHistoryLink();
      //then
      expect(result).toBeTruthy();
    });

    it('Should show snapshot history with workflow in progress', () => {
      //given
      const lineWorkflow: LineVersionWorkflow = {
        workflowId: 1,
        workflowProcessingStatus: WorkflowProcessingStatus.InProgress,
      };
      lineVersion.lineVersionWorkflows?.add(lineWorkflow);
      fixture.componentInstance.selectedVersion = lineVersion;

      //when
      const result = fixture.componentInstance.showSnapshotHistoryLink();
      //then
      expect(result).toBeTruthy();
    });
  });
});

function setupTestBed(
  linesService: LinesService,
  data: { lineDetail: string | LineVersionV2[] }
) {
  TestBed.configureTestingModule({
    declarations: [
      LineDetailComponent,
      LineDetailFormComponent,
      ErrorNotificationComponent,
      InfoIconComponent,
      CommentComponent,
      LinkIconComponent,
      AtlasLabelFieldComponent,
      AtlasFieldErrorComponent,
      TextFieldComponent,
      SelectComponent,
      AtlasSpacerComponent,
      DetailPageContainerComponent,
      DetailPageContentComponent,
      DetailFooterComponent,
      AtlasButtonComponent,
      UserDetailInfoComponent,
      SwitchVersionComponent,
      MockAppCoverageComponent,
      DateRangeComponent,
      DateRangeTextComponent,
      DateIconComponent,
      DisplayDatePipe,
      WorkflowComponent,
      MockSublineTableComponent,
    ],
    imports: [AppTestingModule, FormModule],
    providers: [
      { provide: FormBuilder },
      { provide: LinesService, useValue: linesService },
      { provide: DialogService, useValue: dialogService },
      { provide: PermissionService, useValue: adminPermissionServiceMock },
      { provide: ActivatedRoute, useValue: { snapshot: { data: data } } },
      { provide: TranslatePipe },
    ],
  })
    .overrideComponent(SublineDetailComponent, {
      set: {
        providers: [{ provide: ValidityService, useValue: validityService }],
      },
    })
    .compileComponents()
    .then();
}
