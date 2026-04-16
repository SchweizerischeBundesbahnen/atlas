import { ComponentFixture, TestBed } from '@angular/core/testing';
import { beforeEach, describe, expect, it, vi, type Mocked } from 'vitest';

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
import { DetailDialogHelperService } from '../../../../../core/detail/detail-dialog-helper.service';
import { Router } from '@angular/router';
import { ValidationService } from '../../../../../core/validation/validation.service';
import { StringListComponent } from '../../../../../core/form-components/string-list/string-list.component';
import { StopPointWorkflowService } from '../../../../../api/service/workflow/stop-point-workflow.service';

describe('AddExaminantsComponent', () => {
  let component: AddExaminantsComponent;
  let fixture: ComponentFixture<AddExaminantsComponent>;

  let dialogRefSpy: Mocked<Pick<MatDialogRef<AddExaminantsComponent>, 'close'>>;
  let notificationServiceSpy: Mocked<Pick<NotificationService, 'success'>>;
  let router: Mocked<Pick<Router, 'navigate'>>;
  let detailHelperService: Mocked<Pick<DetailDialogHelperService, 'confirmLeaveDirtyForm'>>;
  let stopPointWorkflowService: Mocked<Pick<StopPointWorkflowService, 'addExaminantsToStopPointWorkflow'>>;

  const workflowDialogData: AddExaminantsDialogData = {
    title: '',
    message: '',
    workflowId: 5,
  };

  beforeEach(async () => {
    dialogRefSpy = { close: vi.fn() };
    notificationServiceSpy = { success: vi.fn() };
    router = {
      navigate: vi.fn().mockReturnValue(Promise.resolve()),
    };
    detailHelperService = {
      confirmLeaveDirtyForm: vi.fn().mockReturnValue(of(true)),
    };
    stopPointWorkflowService = {
      addExaminantsToStopPointWorkflow: vi.fn().mockReturnValue(EMPTY),
    };

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
        { provide: DetailDialogHelperService, useValue: detailHelperService },
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
    vi.spyOn(ValidationService, 'validateForm');

    const firstExaminant = component.form.controls.examinants.at(0);
    firstExaminant.controls.firstName.setValue('');
    firstExaminant.controls.lastName.setValue('');
    firstExaminant.controls.personFunction.setValue('personFunction');
    firstExaminant.controls.organisation.setValue('organisation');
    firstExaminant.controls.mail.setValue('mail@sbb.ch');

    component.form.controls.ccEmails.setValue(['miri@yb.ch']);

    component.addExaminants();

    expect(stopPointWorkflowService.addExaminantsToStopPointWorkflow).toHaveBeenCalled();
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
    expect(component.form.valid).toBe(true);

    component.form.controls.examinants.removeAt(0);
    expect(component.form.valid).toBe(false);

    component.form.controls.ccEmails.setValue(['winnetou@apache.usa']);
    expect(component.form.valid).toBe(true);
  });
});
