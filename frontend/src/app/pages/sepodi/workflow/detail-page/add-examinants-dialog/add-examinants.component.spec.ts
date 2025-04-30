import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AddExaminantsComponent } from './add-examinants.component';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { TranslatePipe } from '@ngx-translate/core';
import { AtlasButtonComponent } from '../../../../../core/components/button/atlas-button.component';
import { AddExaminantsDialogData } from './add-examinants-dialog-data';
import { EMPTY, of } from 'rxjs';
import { StopPointWorkflowExaminantsTableComponent } from '../examinant-table/stop-point-workflow-examinants-table.component';
import { DialogCloseComponent } from '../../../../../core/components/dialog/close/dialog-close.component';
import { DialogFooterComponent } from '../../../../../core/components/dialog/footer/dialog-footer.component';
import { DialogContentComponent } from '../../../../../core/components/dialog/content/dialog-content.component';
import { AppTestingModule } from '../../../../../app.testing.module';
import { FormModule } from '../../../../../core/module/form.module';
import { NotificationService } from '../../../../../core/notification/notification.service';
import { DetailHelperService } from '../../../../../core/detail/detail-helper.service';
import { StopPointWorkflowService } from '../../../../../api';
import { Router } from '@angular/router';
import { ValidationService } from '../../../../../core/validation/validation.service';
import { StringListComponent } from '../../../../../core/form-components/string-list/string-list.component';

const dialogRefSpy = jasmine.createSpyObj('MatDialogRef', ['close']);
const notificationServiceSpy = jasmine.createSpyObj('NotificationService', [
  'success',
]);
const router = jasmine.createSpyObj({
  navigate: Promise.resolve(),
});
const detailHelperService = jasmine.createSpyObj({
  confirmLeaveDirtyForm: of(true),
});
const stopPointWorkflowService = jasmine.createSpyObj(
  'StopPointWorkflowService',
  { addExaminantsToStopPointWorkflow: EMPTY }
);

const workflowDialogData: AddExaminantsDialogData = {
  title: '',
  message: '',
  workflowId: 5,
};

describe('AddExaminantsComponent', () => {
  let component: AddExaminantsComponent;
  let fixture: ComponentFixture<AddExaminantsComponent>;

  beforeEach(async () => {
    TestBed.configureTestingModule({
      imports: [
        AppTestingModule,
        FormModule,
        AddExaminantsComponent,
        AtlasButtonComponent,
        StopPointWorkflowExaminantsTableComponent,
        StringListComponent,
        DialogCloseComponent,
        DialogFooterComponent,
        DialogContentComponent,
      ],
      providers: [
        {
          provide: MAT_DIALOG_DATA,
          useValue: workflowDialogData,
        },
        { provide: MatDialogRef, useValue: dialogRefSpy },
        { provide: NotificationService, useValue: notificationServiceSpy },
        { provide: DetailHelperService, useValue: detailHelperService },
        {
          provide: StopPointWorkflowService,
          useValue: stopPointWorkflowService,
        },
        { provide: Router, useValue: router },
        { provide: TranslatePipe },
      ],
    })
      .compileComponents()
      .then();

    fixture = TestBed.createComponent(AddExaminantsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should cancel workflow creation', () => {
    component.cancel();

    expect(detailHelperService.confirmLeaveDirtyForm).toHaveBeenCalled();
    expect(dialogRefSpy.close).toHaveBeenCalled();
  });

  it('should add examinants via service', () => {
    spyOn(ValidationService, 'validateForm').and.callThrough();

    const firstExaminant = component.form.controls.examinants.at(0);
    firstExaminant.controls.firstName.setValue('');
    firstExaminant.controls.lastName.setValue('');
    firstExaminant.controls.personFunction.setValue('personFunction');
    firstExaminant.controls.organisation.setValue('organisation');
    firstExaminant.controls.mail.setValue('mail@sbb.ch');

    component.form.controls.ccEmails.setValue(['miri@yb.ch']);

    component.addExaminants();

    expect(
      stopPointWorkflowService.addExaminantsToStopPointWorkflow
    ).toHaveBeenCalled();
  });

  it('should have combined validation', () => {
    component.form.controls.examinants.patchValue([
      {
        organisation: 'Organisation',
        mail: 'bestmail@sbb.ch',
      },
    ]);
    expect(component.form.controls.examinants.length).toBe(1);
    expect(component.form.controls.ccEmails.value?.length ?? 0).toBe(0);
    expect(component.form.valid).toBeTrue();

    component.form.controls.examinants.removeAt(0);
    expect(component.form.valid).toBeFalse();

    component.form.controls.ccEmails.setValue(['winnetou@apache.usa']);
    expect(component.form.valid).toBeTrue();
  });
});
