import {
  ContainerTimetableHearingYear,
  SwissCanton,
  TimetableHearingService,
  TimetableHearingStatement,
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
import { MockAtlasButtonComponent } from '../../../app.testing.mocks';

const existingStatement: TimetableHearingStatement = {
  id: 1,
  swissCanton: SwissCanton.Bern,
  statement: 'Luca is am yb match gsi',
  statementSender: {
    email: 'luca@yb.ch',
  },
};

const years: ContainerTimetableHearingYear = {
  objects: [
    {
      timetableYear: 2024,
      hearingFrom: new Date('2023-05-1'),
      hearingTo: new Date('2023-05-31'),
    },
  ],
};

let component: StatementDetailComponent;
let fixture: ComponentFixture<StatementDetailComponent>;
let router: Router;

const mockTimetableHearingService = jasmine.createSpyObj('timetableHearingService', [
  'getHearingYears',
  'getResponsibleTransportCompanies',
  'createStatement',
  'updateHearingStatement',
]);

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
      mockTimetableHearingService.createStatement.and.returnValue(of(existingStatement));

      component.form.controls.swissCanton.setValue(SwissCanton.Bern);
      component.form.controls.statement.setValue('my yb busses');
      component.form.controls.statementSender.controls.email.setValue('luca@yb.ch');
      fixture.detectChanges();

      component.save();
      expect(mockTimetableHearingService.createStatement).toHaveBeenCalled();

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
  mockTimetableHearingService.getHearingYears.and.returnValue(of(years));

  TestBed.configureTestingModule({
    declarations: [
      StatementDetailComponent,
      ErrorNotificationComponent,
      AtlasSpacerComponent,
      DetailFooterComponent,
      DetailPageContainerComponent,
      InfoIconComponent,
      CommentComponent,
      LinkIconComponent,
      MockAtlasButtonComponent,
    ],
    imports: [AppTestingModule, FormModule],
    providers: [
      { provide: FormBuilder },
      { provide: TimetableHearingService, useValue: mockTimetableHearingService },
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
