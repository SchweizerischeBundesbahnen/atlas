import {
  HearingStatus,
  SwissCanton,
  TimetableHearingStatement,
  TimetableHearingStatementsService,
  TimetableHearingYear,
  TimetableHearingYearsService,
} from '../../../api';
import { ActivatedRoute, Router } from '@angular/router';
import { FormBuilder } from '@angular/forms';
import { of } from 'rxjs';
import { AuthService } from '../../../core/auth/auth.service';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ErrorNotificationComponent } from '../../../core/notification/error/error-notification.component';
import { InfoIconComponent } from '../../../core/form-components/info-icon/info-icon.component';
import { CommentComponent } from '../../../core/form-components/comment/comment.component';
import { LinkIconComponent } from '../../../core/form-components/link-icon/link-icon.component';
import { AppTestingModule, authServiceMock } from '../../../app.testing.module';
import { FormModule } from '../../../core/module/form.module';
import { TranslatePipe } from '@ngx-translate/core';
import { StatementDetailComponent } from './statement-detail.component';
import { AtlasSpacerComponent } from '../../../core/components/spacer/atlas-spacer.component';
import { DetailFooterComponent } from '../../../core/components/detail-footer/detail-footer.component';
import { DetailPageContainerComponent } from '../../../core/components/detail-page-container/detail-page-container.component';
import { MockAtlasButtonComponent, MockSelectComponent } from '../../../app.testing.mocks';
import { Component, Input } from '@angular/core';
import { CreationEditionRecord } from '../../../core/components/base-detail/user-edit-info/creation-edition-record';
import { By } from '@angular/platform-browser';
import { FileUploadComponent } from '../../../core/components/file-upload/file-upload.component';
import { FileSizePipe } from '../../../core/components/file-upload/file-size/file-size.pipe';
import { FileComponent } from '../../../core/components/file-upload/file/file.component';
import { LoadingSpinnerComponent } from '../../../core/components/loading-spinner/loading-spinner.component';

const existingStatement: TimetableHearingStatement = {
  id: 1,
  swissCanton: SwissCanton.Bern,
  statement: 'Luca isch am yb match gsi',
  statementSender: {
    email: 'luca@yb.ch',
  },
};

const years: TimetableHearingYear[] = [
  {
    timetableYear: 2024,
    hearingFrom: new Date('2023-05-1'),
    hearingTo: new Date('2023-05-31'),
  },
];

let component: StatementDetailComponent;
let fixture: ComponentFixture<StatementDetailComponent>;
let router: Router;

const mockTimetableHearingYearsService = jasmine.createSpyObj('timetableHearingYearsService', [
  'getHearingYears',
]);

const mockTimetableHearingStatementsService = jasmine.createSpyObj(
  'timetableHearingStatementsService',
  ['createStatement'],
);

@Component({
  selector: 'app-user-detail-info',
  template: '<p>MockUserDetailInfoComponent</p>',
})
class MockUserDetailInfoComponent {
  @Input() short = false;
  @Input() record?: CreationEditionRecord;
}

describe('StatementDetailComponent for existing statement', () => {
  beforeEach(() => {
    const mockRoute = {
      snapshot: {
        data: {
          statement: existingStatement,
        },
        params: {
          canton: 'be',
        },
      },
    };
    setupTestBed(mockRoute);

    fixture = TestBed.createComponent(StatementDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    router = TestBed.inject(Router);
  });

  it('should be created', () => {
    expect(component).toBeTruthy();
    expect(component.isNew).toBeFalse();
  });

  it('should load existing Statement form successfully', () => {
    expect(component.form.controls.statement.value).toBe(existingStatement.statement);
  });

  it('should not enable form when hearingStatus is Archived', () => {
    component.hearingStatus = HearingStatus.Archived;

    expect(component.form.enabled).toBeFalsy();
  });

  it('should not enable form when hearingStatus is Archived and clicking on toggleEdit', () => {
    //given
    component.hearingStatus = HearingStatus.Archived;

    //when
    component.toggleEdit();

    //then
    expect(component.form.enabled).toBeFalsy();
  });
});

describe('test editButton', () => {
  beforeEach(() => {
    const mockRoute = {
      snapshot: {
        data: {
          statement: existingStatement,
          hearingStatus: HearingStatus.Active,
        },
        params: {
          canton: 'be',
        },
      },
    };
    setupTestBed(mockRoute);

    mockTimetableHearingYearsService.getHearingYears.and.returnValue(
      of([
        {
          ...years[0],
          statementEditable: true,
        },
      ]),
    );

    fixture = TestBed.createComponent(StatementDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    router = TestBed.inject(Router);
  });

  it('should not show edit button when HearingStatus is Archived', () => {
    //given
    component.hearingStatus = HearingStatus.Archived;
    //when
    fixture.detectChanges();
    //then
    const buttons = fixture.debugElement.queryAll(By.css('atlas-button'));
    expect(buttons.length).toBe(4);
    const buttonsText = buttons.map(
      (button) => button.nativeElement.attributes['buttontext'].value,
    );
    expect(buttonsText).not.toContain('COMMON.EDIT');
  });

  it('should show edit button when HearingStatus is not Archived and statement is editable', () => {
    const buttons = fixture.debugElement.queryAll(By.css('atlas-button'));
    expect(buttons.length).toBe(5);
    const buttonsText = buttons.map(
      (button) => button.nativeElement.attributes['buttontext'].value,
    );
    expect(buttonsText).toContain('COMMON.EDIT');
  });
});

describe('StatementDetailComponent for new statement', () => {
  beforeEach(() => {
    const mockRoute = {
      snapshot: {
        data: {
          statement: undefined,
        },
        params: {
          canton: 'be',
        },
      },
    };
    setupTestBed(mockRoute);

    fixture = TestBed.createComponent(StatementDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    router = TestBed.inject(Router);
  });

  it('should create', () => {
    expect(component).toBeTruthy();
    expect(component.isNew).toBeTrue();
  });

  describe('create new statement', () => {
    it('successfully', () => {
      spyOn(router, 'navigate').and.returnValue(Promise.resolve(true));
      mockTimetableHearingStatementsService.createStatement.and.returnValue(of(existingStatement));

      component.form.controls.swissCanton.setValue(SwissCanton.Bern);
      component.form.controls.statement.setValue('my yb busses');
      component.form.controls.statementSender.controls.email.setValue('luca@yb.ch');
      fixture.detectChanges();

      component.save();
      expect(mockTimetableHearingStatementsService.createStatement).toHaveBeenCalled();

      fixture.detectChanges();

      const snackBarContainer =
        fixture.nativeElement.offsetParent.querySelector('mat-snack-bar-container');
      expect(snackBarContainer).toBeDefined();
      expect(snackBarContainer.textContent.trim()).toBe('TTH.STATEMENT.NOTIFICATION.ADD_SUCCESS');
      expect(snackBarContainer.classList).toContain('success');
      expect(router.navigate).toHaveBeenCalled();
    });
  });
});

function setupTestBed(activatedRoute: {
  snapshot: { data: { statement: undefined | TimetableHearingStatement } };
}) {
  mockTimetableHearingYearsService.getHearingYears.and.returnValue(of(years));

  TestBed.configureTestingModule({
    declarations: [
      LoadingSpinnerComponent,
      StatementDetailComponent,
      ErrorNotificationComponent,
      AtlasSpacerComponent,
      DetailFooterComponent,
      DetailPageContainerComponent,
      InfoIconComponent,
      CommentComponent,
      LinkIconComponent,
      MockAtlasButtonComponent,
      MockSelectComponent,
      MockUserDetailInfoComponent,
      FileUploadComponent,
      FileSizePipe,
      FileComponent,
    ],
    imports: [AppTestingModule, FormModule],
    providers: [
      { provide: FormBuilder },
      { provide: TimetableHearingYearsService, useValue: mockTimetableHearingYearsService },
      {
        provide: TimetableHearingStatementsService,
        useValue: mockTimetableHearingStatementsService,
      },
      { provide: AuthService, useValue: authServiceMock },
      { provide: TranslatePipe },
      {
        provide: ActivatedRoute,
        useValue: activatedRoute,
      },
    ],
  })
    .compileComponents()
    .then();
}
