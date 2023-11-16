import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import {
  LinesService,
  LineType,
  LineVersion,
  LineVersionSnapshot,
  PaymentType,
} from '../../../../api';
import { LineVersionSnapshotDetailComponent } from './line-version-snapshot-detail.component';
import { AppTestingModule, authServiceMock } from '../../../../app.testing.module';
import { ErrorNotificationComponent } from '../../../../core/notification/error/error-notification.component';
import { InfoIconComponent } from '../../../../core/form-components/info-icon/info-icon.component';
import {
  MockAppDetailWrapperComponent,
  MockAtlasButtonComponent,
  MockBoSelectComponent,
} from '../../../../app.testing.mocks';
import { AuthService } from '../../../../core/auth/auth.service';
import { LineDetailFormComponent } from '../../lines/detail/line-detail-form/line-detail-form.component';
import { LinkIconComponent } from '../../../../core/form-components/link-icon/link-icon.component';
import { of } from 'rxjs';
import { CommentComponent } from '../../../../core/form-components/comment/comment.component';
import { UserDetailInfoComponent } from '../../../../core/components/base-detail/user-edit-info/user-detail-info.component';
import { AtlasLabelFieldComponent } from '../../../../core/form-components/atlas-label-field/atlas-label-field.component';
import { AtlasFieldErrorComponent } from '../../../../core/form-components/atlas-field-error/atlas-field-error.component';
import { TranslatePipe } from '@ngx-translate/core';
import { TextFieldComponent } from '../../../../core/form-components/text-field/text-field.component';
import { InfoLinkDirective } from '../../../../core/form-components/info-icon/info-link.directive';
import { SelectComponent } from '../../../../core/form-components/select/select.component';
import { AtlasSpacerComponent } from '../../../../core/components/spacer/atlas-spacer.component';
import { DetailFooterComponent } from '../../../../core/components/detail-footer/detail-footer.component';
import { DetailPageContainerComponent } from '../../../../core/components/detail-page-container/detail-page-container.component';
import { DateRangeTextComponent } from '../../../../core/versioning/date-range-text/date-range-text.component';
import { DisplayDatePipe } from '../../../../core/pipe/display-date.pipe';

const lineVersionSnapsot: LineVersionSnapshot = {
  id: 1234,
  slnid: 'slnid',
  number: 'name',
  description: 'asdf',
  status: 'VALIDATED',
  validFrom: new Date('2021-06-01'),
  validTo: new Date('2029-06-01'),
  businessOrganisation: 'SBB',
  paymentType: PaymentType.None,
  lineType: LineType.Orderly,
  colorBackCmyk: '',
  colorBackRgb: '',
  colorFontCmyk: '',
  colorFontRgb: '',
  parentObjectId: 1234,
  workflowId: 1,
  workflowStatus: 'ADDED',
};

const lineVersion: LineVersion = {
  id: 1234,
  slnid: 'slnid',
  number: 'name',
  description: 'asdf',
  status: 'VALIDATED',
  validFrom: new Date('2021-06-01'),
  validTo: new Date('2029-06-01'),
  businessOrganisation: 'SBB',
  paymentType: PaymentType.None,
  lineType: LineType.Orderly,
  colorBackCmyk: '',
  colorBackRgb: '',
  colorFontCmyk: '',
  colorFontRgb: '',
  swissLineNumber: '13',
};

let component: LineVersionSnapshotDetailComponent;
let fixture: ComponentFixture<LineVersionSnapshotDetailComponent>;

describe('LineVersionSnapshotDetailComponent', () => {
  const mockLinesService = jasmine.createSpyObj('linesService', [
    'getLineVersionSnapshotById',
    'getLineVersions',
  ]);
  mockLinesService.getLineVersions.and.returnValue(of([lineVersion]));
  const mockData = {
    lineVersionSnapshot: lineVersionSnapsot,
  };

  beforeEach(() => {
    setupTestBed(mockLinesService, mockData);

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
  linesService: LinesService,
  data: { lineVersionSnapshot: string | LineVersionSnapshot },
) {
  TestBed.configureTestingModule({
    declarations: [
      LineVersionSnapshotDetailComponent,
      MockAppDetailWrapperComponent,
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
      DateRangeTextComponent,
      DisplayDatePipe,
    ],
    imports: [AppTestingModule],
    providers: [
      { provide: FormBuilder },
      { provide: LinesService, useValue: linesService },
      { provide: AuthService, useValue: authServiceMock },
      { provide: ActivatedRoute, useValue: { snapshot: { data: data } } },
      { provide: TranslatePipe },
    ],
  })
    .compileComponents()
    .then();
}
