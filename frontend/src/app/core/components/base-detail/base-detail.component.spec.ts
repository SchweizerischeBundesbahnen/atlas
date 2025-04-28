import { ComponentFixture, TestBed } from '@angular/core/testing';
import { BaseDetailComponent } from './base-detail.component';
import { By } from '@angular/platform-browser';
import { BaseDetailController } from './base-detail-controller';
import { of, Subject } from 'rxjs';
import { AppTestingModule } from '../../../app.testing.module';
import { Component, Input } from '@angular/core';
import { ApplicationType, Status } from '../../../api';
import {
  adminPermissionServiceMock,
  MockUserDetailInfoComponent,
} from '../../../app.testing.mocks';
import { AtlasButtonComponent } from '../button/atlas-button.component';
import { NotificationService } from '../../notification/notification.service';
import { DetailPageContainerComponent } from '../detail-page-container/detail-page-container.component';
import { DetailFooterComponent } from '../detail-footer/detail-footer.component';
import { DateRangeTextComponent } from '../../versioning/date-range-text/date-range-text.component';
import { Record } from './record';
import { Page } from '../../model/page';
import { DetailPageContentComponent } from '../detail-page-content/detail-page-content.component';
import { PermissionService } from '../../auth/permission/permission.service';

@Component({
    selector: 'app-coverage',
    template: '<p>Mock Product Editor Component</p>',
    imports: [AppTestingModule]
})
class MockAppCoverageComponent {
  @Input() pageType!: Record;
  @Input() currentRecord!: Page;
}

describe('BaseDetailComponent', () => {
  /*eslint-disable */
  let component: BaseDetailComponent;
  let fixture: ComponentFixture<BaseDetailComponent>;

  const notificationServiceSpy = jasmine.createSpyObj('NotificationService', ['success', 'error']);

  beforeEach(async () => {
    await TestBed.configureTestingModule({
    imports: [AppTestingModule, BaseDetailComponent,
        MockAppCoverageComponent,
        AtlasButtonComponent,
        MockUserDetailInfoComponent,
        DetailPageContainerComponent,
        DetailPageContentComponent,
        DetailFooterComponent,
        DateRangeTextComponent],
    providers: [
        { provide: PermissionService, useValue: adminPermissionServiceMock },
        { provide: NotificationService, useValue: notificationServiceSpy },
    ],
}).compileComponents();
  });

  function init(controller: BaseDetailController<any>) {
    fixture = TestBed.createComponent(BaseDetailComponent);
    component = fixture.componentInstance;
    component.controller = controller;
    fixture.detectChanges();
  }

  /*eslint-enable */

  describe('disabled', (dummyController = createDummyForm(false)) => {
    beforeEach(() => init(dummyController));

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should switch from disabled to enabled', () => {
      const editButton = fixture.debugElement.query(By.css('[data-cy=edit-item]'));
      editButton.nativeElement.click();

      expect(dummyController.toggleEdit).toHaveBeenCalled();
    });

    it('should revoke record', () => {
      const editButton = fixture.debugElement.query(By.css('[data-cy=revoke-item]'));
      editButton.nativeElement.click();

      expect(dummyController.revoke).toHaveBeenCalled();
    });

    it('should delete record', () => {
      const deleteButton = fixture.debugElement.query(By.css('[data-cy=delete-item]'));
      deleteButton.nativeElement.click();

      expect(dummyController.delete).toHaveBeenCalled();
    });
  });

  describe('enabled', (dummyController = createDummyForm(true)) => {
    beforeEach(() => init(dummyController));

    it('should save and disable form', () => {
      const submitButton = fixture.debugElement.query(By.css('[type=submit]'));
      submitButton.nativeElement.click();

      expect(dummyController.save).toHaveBeenCalled();
    });
  });
});

function createDummyForm(enabledForm: boolean) {
  const form = jasmine.createSpyObj('form', ['enable', 'disable'], {
    enabled: enabledForm,
    dirty: true,
    valid: true,
  });
  const selectedRecordChange = new Subject();
  const dummyController = jasmine.createSpyObj(
    'dummyController',
    [
      'isExistingRecord',
      'save',
      'toggleEdit',
      'isNewRecord',
      'getId',
      'updateRecord',
      'confirmLeave',
      'validateAllFormFields',
      'ngOnInit',
      'revoke',
      'delete',
      'getPageType',
      'getApplicationType',
      'disableUneditableFormFields',
      'confirmBoTransfer',
      'isWorkflowable',
    ],
    {
      heading: undefined,
      form: form,
      record: { id: 1, status: Status.Validated },
      selectedRecordChange: selectedRecordChange,
    },
  );
  dummyController.getId.and.callFake(BaseDetailController.prototype.getId);
  dummyController.isNewRecord.and.callFake(BaseDetailController.prototype.isNewRecord);
  dummyController.isExistingRecord.and.callFake(BaseDetailController.prototype.isExistingRecord);
  dummyController.confirmLeave.and.returnValue(of(true));
  dummyController.confirmBoTransfer.and.returnValue(of(true));
  dummyController.getApplicationType.and.returnValue(ApplicationType.Bodi);

  return dummyController;
}
