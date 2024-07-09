import { ComponentFixture, TestBed } from '@angular/core/testing';
import { DecisionStepperComponent } from './decision-stepper.component';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { AppTestingModule } from '../../../../../../app.testing.module';
import { DecisionFormComponent } from '../decision-form/decision-form.component';
import { CommentComponent } from '../../../../../../core/form-components/comment/comment.component';
import { AtlasFieldErrorComponent } from '../../../../../../core/form-components/atlas-field-error/atlas-field-error.component';
import { TextFieldComponent } from '../../../../../../core/form-components/text-field/text-field.component';
import { AtlasLabelFieldComponent } from '../../../../../../core/form-components/atlas-label-field/atlas-label-field.component';
import { LoadingSpinnerComponent } from '../../../../../../core/components/loading-spinner/loading-spinner.component';
import { DialogContentComponent } from '../../../../../../core/components/dialog/content/dialog-content.component';
import { DialogCloseComponent } from '../../../../../../core/components/dialog/close/dialog-close.component';
import { StopPointWorkflowService } from '../../../../../../api';
import { of } from 'rxjs';

describe('DecisionStepperComponent', () => {
  let component: DecisionStepperComponent;
  let fixture: ComponentFixture<DecisionStepperComponent>;

  let dialogRefSpy = jasmine.createSpyObj(['close']);
  let spWfServiceSpy = jasmine.createSpyObj(['obtainOtp', 'verifyOtp', 'voteWorkflow']);

  beforeEach(async () => {
    dialogRefSpy = jasmine.createSpyObj(['close']);
    spWfServiceSpy = jasmine.createSpyObj(['obtainOtp', 'verifyOtp', 'voteWorkflow']);

    await TestBed.configureTestingModule({
      declarations: [
        DecisionStepperComponent,
        DecisionFormComponent,
        CommentComponent,
        AtlasFieldErrorComponent,
        TextFieldComponent,
        AtlasLabelFieldComponent,
        LoadingSpinnerComponent,
        DialogContentComponent,
        DialogCloseComponent,
      ],
      imports: [AppTestingModule],
      providers: [
        { provide: MatDialogRef, useValue: dialogRefSpy },
        {
          provide: StopPointWorkflowService,
          useValue: spWfServiceSpy,
        },
        {
          provide: MAT_DIALOG_DATA,
          useValue: 1,
        },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(DecisionStepperComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('stepper', () => {
    function testObtainOtpStep() {
      component.mail.controls.mail.setValue('techsupport@atlas.ch');
      spWfServiceSpy.obtainOtp.and.returnValue(of('valid'));

      component.completeObtainOtpStep();

      fixture.detectChanges();

      expect(spWfServiceSpy.obtainOtp).toHaveBeenCalledOnceWith(1, {
        examinantMail: 'techsupport@atlas.ch',
      });
      expect(component.stepper?.selectedIndex).toEqual(1);
      expect(component.stepper?.selected?.completed).toBeFalse();
    }

    function testVerifyPinStep() {
      component.pin.controls.pin.setValue('234313');
      spWfServiceSpy.verifyOtp.and.returnValue(
        of({
          id: 50,
          firstName: 'first',
          lastName: 'last',
          organisation: 'sbb',
          personFunction: 'chef',
        }),
      );

      component.completeVerifyPinStep();
      fixture.detectChanges();

      expect(spWfServiceSpy.verifyOtp).toHaveBeenCalledOnceWith(1, {
        examinantMail: 'techsupport@atlas.ch',
        pinCode: '234313',
      });
      expect(component.stepper?.selectedIndex).toEqual(2);
      expect(component.stepper?.selected?.completed).toBeFalse();
    }

    function testCompleteStep() {
      component.decision.patchValue({
        judgement: 'YES',
        motivation: 'cool',
      });
      spWfServiceSpy.voteWorkflow.and.returnValue(of('voted'));

      component.completeDecision();
      fixture.detectChanges();

      expect(spWfServiceSpy.voteWorkflow).toHaveBeenCalledOnceWith(1, 50, {
        examinantMail: 'techsupport@atlas.ch',
        pinCode: '234313',
        judgement: 'YES',
        motivation: 'cool',
        firstName: 'first',
        lastName: 'last',
        organisation: 'sbb',
        personFunction: 'chef',
      });
      expect(dialogRefSpy.close).toHaveBeenCalledOnceWith(true);
      expect(component.stepper?.selected?.completed).toBeTrue();
    }

    it('happy path', () => {
      testObtainOtpStep();
      testVerifyPinStep();
      testCompleteStep();
    });
  });

  it('should resend mail', () => {
    component.mail.controls.mail.setValue('resend@sbb.ch');
    spWfServiceSpy.obtainOtp.and.returnValue(of('valid'));

    component.resendMail();

    expect(spWfServiceSpy.obtainOtp).toHaveBeenCalledOnceWith(1, {
      examinantMail: 'resend@sbb.ch',
    });
  });

  it('should cancel (close dialog immediately) on step 1', () => {
    component.cancel();
    expect(dialogRefSpy.close).toHaveBeenCalledOnceWith();
  });
});
