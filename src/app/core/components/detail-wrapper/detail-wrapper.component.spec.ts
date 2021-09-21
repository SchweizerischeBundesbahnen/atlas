import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DetailWrapperComponent } from './detail-wrapper.component';
import { MaterialModule } from '../../module/material.module';
import { TranslateFakeLoader, TranslateLoader, TranslateModule } from '@ngx-translate/core';
import { By } from '@angular/platform-browser';
import { DetailWrapperController } from './detail-wrapper-controller';
import { of } from 'rxjs';

describe('DetailWrapperComponent', () => {
  /*eslint-disable */
  let component: DetailWrapperComponent<any>;
  let fixture: ComponentFixture<DetailWrapperComponent<any>>;
  /*eslint-enable */

  const form = jasmine.createSpyObj('form', ['enable', 'disable'], {
    enabled: true,
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

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [DetailWrapperComponent],
      imports: [
        MaterialModule,
        TranslateModule.forRoot({
          loader: { provide: TranslateLoader, useClass: TranslateFakeLoader },
        }),
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DetailWrapperComponent);
    component = fixture.componentInstance;
    component.controller = dummyController;
    component.canEdit = true;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should switch from disabled to enabled', () => {
    const editButton = fixture.debugElement.query(By.css('.bi-pencil-fill'));
    editButton.nativeElement.click();

    expect(dummyController.toggleEdit).toHaveBeenCalled();
  });

  it('should save and disable form', () => {
    const submitButton = fixture.debugElement.query(By.css('[type=submit]'));
    submitButton.nativeElement.click();

    expect(dummyController.save).toHaveBeenCalled();
  });
});
