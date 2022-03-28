import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DetailWrapperComponent } from './detail-wrapper.component';
import { By } from '@angular/platform-browser';
import { DetailWrapperController } from './detail-wrapper-controller';
import { of } from 'rxjs';
import { AppTestingModule } from '../../../app.testing.module';
import { AuthService } from '../../auth/auth.service';
import { Role } from '../../auth/role';

describe('DetailWrapperComponent', () => {
  /*eslint-disable */
  let component: DetailWrapperComponent<any>;
  let fixture: ComponentFixture<DetailWrapperComponent<any>>;

  const authServiceMock: Partial<AuthService> = {
    claims: { name: 'Test (ITC)', email: 'test@test.ch', roles: ['lidi-admin', 'lidi-writer'] },
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
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [DetailWrapperComponent],
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

    describe('role lidi-writer', (roles = ['lidi-writer']) => {
      beforeAll(() => (authServiceMock.claims!.roles = roles));
      afterAll(() => (authServiceMock.claims!.roles = ['lidi-admin', 'lidi-writer']));

      it('edit-button should be visible', () => {
        const editButton = fixture.debugElement.query(By.css('.edit-section button:first-child'));
        expect(editButton).toBeDefined();
        expect(editButton.nativeElement.textContent.includes('EDIT')).toBeTrue();
      });

      it('delete-button should not be visible', () => {
        const buttons = fixture.debugElement.queryAll(By.css('.edit-section button'));
        expect(buttons.length).toBe(1);
      });
    });

    describe('role lidi-admin', (roles = ['lidi-admin']) => {
      beforeAll(() => (authServiceMock.claims!.roles = roles));
      afterAll(() => (authServiceMock.claims!.roles = ['lidi-admin', 'lidi-writer']));

      it('edit-button should be visible', () => {
        const editButton = fixture.debugElement.query(By.css('.edit-section button:first-child'));
        expect(editButton).toBeDefined();
        expect(editButton.nativeElement.textContent.includes('EDIT')).toBeTrue();
      });

      it('delete-button should be visible', () => {
        const buttons = fixture.debugElement.queryAll(By.css('.edit-section button'));
        expect(buttons.length).toBe(2);
        const deleteButton = fixture.debugElement.query(By.css('.edit-section button:last-child'));
        expect(deleteButton).toBeDefined();
      });
    });

    describe('empty roles', (roles = []) => {
      beforeAll(() => (authServiceMock.claims!.roles = roles));
      afterAll(() => (authServiceMock.claims!.roles = ['lidi-admin', 'lidi-writer']));

      it('no buttons should be visible', () => {
        const buttons = fixture.debugElement.queryAll(By.css('.edit-section button'));
        expect(buttons.length).toBe(0);
      });
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
