import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DetailWrapperComponent } from './detail-wrapper.component';
import { By } from '@angular/platform-browser';
import { DetailWrapperController } from './detail-wrapper-controller';
import { of } from 'rxjs';
import { AppTestingModule } from '../../../app.testing.module';
import { AuthService } from '../../auth/auth.service';
import { Role } from '../../auth/role';
import { Component, Input } from '@angular/core';
import { ApplicationType } from '../../../api';

@Component({
  selector: 'app-coverage',
  template: '<p>Mock Product Editor Component</p>',
})
class MockAppCoverageComponent {
  @Input() pageType!: any;
  @Input() currentRecord!: any;
}

describe('DetailWrapperComponent', () => {
  /*eslint-disable */
  let component: DetailWrapperComponent;
  let fixture: ComponentFixture<DetailWrapperComponent>;

  const authServiceMock: Partial<AuthService> = {
    claims: {
      name: 'Test (ITC)',
      email: 'test@test.ch',
      sbbuid: 'e123456',
      roles: ['lidi-admin', 'lidi-writer'],
    },
    logout: () => Promise.resolve(true),
    login: () => Promise.resolve(true),
    hasAnyRole(roles: Role[]): boolean {
      for (let role of roles) {
        if (this.claims?.roles.includes(role)) return true;
      }
      return false;
    },
    hasRole(role: Role): boolean {
      return this.claims!.roles.includes(role);
    },
    get isAdmin(): boolean {
      return true;
    },
    hasPermissionsToWrite(applicationType: ApplicationType, sboid: string | undefined): boolean {
      return true;
    },
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [DetailWrapperComponent, MockAppCoverageComponent],
      imports: [AppTestingModule],
      providers: [{ provide: AuthService, useValue: authServiceMock }],
    }).compileComponents();
  });

  function init(controller: DetailWrapperController<any>) {
    fixture = TestBed.createComponent(DetailWrapperComponent);
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
      const editButton = fixture.debugElement.query(By.css('.edit-section button:first-child'));
      editButton.nativeElement.click();

      expect(dummyController.toggleEdit).toHaveBeenCalled();
    });

    it('should delete record', () => {
      const deleteButton = fixture.debugElement.query(By.css('.edit-section button:last-child'));
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
      'delete',
      'getPageType',
      'getApplicationType',
      'disableUneditableFormFields',
    ],
    {
      heading: undefined,
      form: form,
      record: { id: 1 },
    }
  );
  dummyController.getId.and.callFake(DetailWrapperController.prototype.getId);
  dummyController.isNewRecord.and.callFake(DetailWrapperController.prototype.isNewRecord);
  dummyController.isExistingRecord.and.callFake(DetailWrapperController.prototype.isExistingRecord);
  dummyController.save.and.callFake(DetailWrapperController.prototype.save);
  dummyController.toggleEdit.and.callFake(DetailWrapperController.prototype.toggleEdit);
  dummyController.confirmLeave.and.returnValue(of(true));

  return dummyController;
}
