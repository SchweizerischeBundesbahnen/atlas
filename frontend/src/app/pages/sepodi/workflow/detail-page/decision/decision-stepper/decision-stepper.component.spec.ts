import { ComponentFixture, TestBed } from '@angular/core/testing';
import { beforeEach, describe, expect, it, vi, type Mocked } from 'vitest';
import { DecisionStepperComponent } from './decision-stepper.component';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { AppTestingModule } from '../../../../../../app.testing.module';
import { DecisionFormComponent } from '../decision-form/decision-form.component';
import { CommentComponent } from '../../../../../../core/form-components/comment/comment.component';
import { AtlasFieldErrorComponent } from '../../../../../../core/form-components/atlas-field-error/atlas-field-error.component';
import { TextFieldComponent } from '../../../../../../core/form-components/text-field/text-field.component';
import { AtlasLabelFieldComponent } from '@atlas/form';
import { LoadingSpinnerComponent } from '../../../../../../core/components/loading-spinner/loading-spinner.component';
import { DialogContentComponent } from '../../../../../../core/components/dialog/content/dialog-content.component';
import { DialogCloseComponent } from '../../../../../../core/components/dialog/close/dialog-close.component';
import { of, throwError } from 'rxjs';
import { StopPointWorkflowService } from '../../../../../../api/service/workflow/stop-point-workflow.service';

describe('DecisionStepperComponent', () => {
  let component: DecisionStepperComponent;
  let fixture: ComponentFixture<DecisionStepperComponent>;

  let dialogRefSpy: Mocked<Pick<MatDialogRef<DecisionStepperComponent>, 'close'>>;
  let spWfServiceSpy: Mocked<Pick<StopPointWorkflowService, 'obtainOtp' | 'verifyOtp' | 'voteWorkflow'>>;

  beforeEach(async () => {
    dialogRefSpy = { close: vi.fn() };
    spWfServiceSpy = {
      obtainOtp: vi.fn(),
      verifyOtp: vi.fn(),
      voteWorkflow: vi.fn(),
    };

    await TestBed.configureTestingModule({
      imports: [
        AppTestingModule,
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
      spWfServiceSpy.obtainOtp.mockReturnValue(of('valid') as never);

      component.completeObtainOtpStep();

      fixture.detectChanges();

      expect(spWfServiceSpy.obtainOtp).toHaveBeenCalledExactlyOnceWith(1, {
        examinantMail: 'techsupport@atlas.ch',
      });
      expect(component.stepper?.selectedIndex).toEqual(1);
      expect(component.stepper?.selected?.completed).toBe(false);
    }

    function testVerifyPinStep() {
      component.pin.controls.pin.setValue('234313');
      spWfServiceSpy.verifyOtp.mockReturnValue(
        of({
          id: 50,
          firstName: 'first',
          lastName: 'last',
          organisation: 'sbb',
          personFunction: 'chef',
        }) as never
      );

      component.completeVerifyPinStep();
      fixture.detectChanges();

      expect(spWfServiceSpy.verifyOtp).toHaveBeenCalledExactlyOnceWith(1, {
        examinantMail: 'techsupport@atlas.ch',
        pinCode: '234313',
      });
      expect(component.stepper?.selectedIndex).toEqual(2);
      expect(component.stepper?.selected?.completed).toBe(false);
    }

    function testCompleteStep() {
      component.decision.patchValue({
        judgement: 'YES',
        motivation: 'cool',
      });
      spWfServiceSpy.voteWorkflow.mockReturnValue(of('voted') as never);

      component.completeDecision();
      fixture.detectChanges();

      expect(spWfServiceSpy.voteWorkflow).toHaveBeenCalledExactlyOnceWith(1, 50, {
        examinantMail: 'techsupport@atlas.ch',
        pinCode: '234313',
        judgement: 'YES',
        motivation: 'cool',
        firstName: 'first',
        lastName: 'last',
        organisation: 'sbb',
        personFunction: 'chef',
      });
      expect(dialogRefSpy.close).toHaveBeenCalledExactlyOnceWith(true);
      expect(component.stepper?.selected?.completed).toBe(true);
    }

    it('happy path', () => {
      testObtainOtpStep();
      testVerifyPinStep();
      testCompleteStep();
    });
  });

  it('should handle error on obtain otp step', () => {
    component.mail.controls.mail.setValue('techsupport@atlas.ch');
    spWfServiceSpy.obtainOtp.mockReturnValue(throwError(() => 'mail not found') as never);

    component.completeObtainOtpStep();
    fixture.detectChanges();

    expect(spWfServiceSpy.obtainOtp).toHaveBeenCalledExactlyOnceWith(1, {
      examinantMail: 'techsupport@atlas.ch',
    });
    expect(component.loading).toBe(false);
    expect(component.stepper?.selectedIndex).toEqual(0);
    expect(component.stepper?.selected?.completed).toBe(false);
  });

  it('should handle error on verify pin step', () => {
    component.isStepOneCompl$ = of(true);
    fixture.detectChanges();
    component.stepper?.next();

    component.pin.controls.pin.setValue('234313');
    spWfServiceSpy.verifyOtp.mockReturnValue(throwError(() => 'bad pin') as never);

    component.completeVerifyPinStep();
    fixture.detectChanges();

    expect(spWfServiceSpy.verifyOtp).toHaveBeenCalledExactlyOnceWith(1, {
      examinantMail: '',
      pinCode: '234313',
    });
    expect(component.loading).toBe(false);
    expect(component.stepper?.selectedIndex).toEqual(1);
    expect(component.stepper?.selected?.completed).toBe(false);
  });

  it('should handle error on complete decision', () => {
    component.isStepOneCompl$ = of(true);
    component.isStepTwoCompl$ = of(true);
    fixture.detectChanges();
    component.stepper?.next();
    component.stepper?.next();

    component['_verifiedExaminant'] = {
      id: 50,
      organisation: 'sbb',
      mail: 'atlas@sbb.ch',
    };
    component.decision.setValue({
      judgement: 'YES',
      motivation: 'cool',
      firstName: 'first',
      lastName: 'last',
      organisation: 'sbb',
      personFunction: 'chef',
    });
    spWfServiceSpy.voteWorkflow.mockReturnValue(throwError(() => 'bad vote') as never);

    component.completeDecision();
    fixture.detectChanges();

    expect(spWfServiceSpy.voteWorkflow).toHaveBeenCalledExactlyOnceWith(1, 50, {
      examinantMail: '',
      pinCode: '',
      judgement: 'YES',
      motivation: 'cool',
      firstName: 'first',
      lastName: 'last',
      organisation: 'sbb',
      personFunction: 'chef',
    });
    expect(component.loading).toBe(false);
    expect(dialogRefSpy.close).not.toHaveBeenCalled();
    expect(component.stepper?.selected?.completed).toBe(false);
  });

  it('should resend mail', () => {
    component.mail.controls.mail.setValue('resend@sbb.ch');
    spWfServiceSpy.obtainOtp.mockReturnValue(of('valid') as never);
    component.isStepOneCompl$ = of(true);
    fixture.detectChanges();
    component.stepper?.next();

    component.resendMail();
    fixture.detectChanges();

    expect(spWfServiceSpy.obtainOtp).toHaveBeenCalledExactlyOnceWith(1, {
      examinantMail: 'resend@sbb.ch',
    });
    expect(component.loading).toBe(false);
    expect(component.stepper?.selected?.completed).toBe(false);
  });

  it('should handle error on resend mail', () => {
    component.mail.controls.mail.setValue('resend@sbb.ch');
    spWfServiceSpy.obtainOtp.mockReturnValue(throwError(() => 'bad mail') as never);
    component.isStepOneCompl$ = of(true);
    fixture.detectChanges();
    component.stepper?.next();

    component.resendMail();
    fixture.detectChanges();

    expect(spWfServiceSpy.obtainOtp).toHaveBeenCalledExactlyOnceWith(1, {
      examinantMail: 'resend@sbb.ch',
    });
    expect(component.loading).toBe(false);
    expect(component.stepper?.selected?.completed).toBe(false);
  });

  it('should cancel (close dialog immediately) on step 1', () => {
    component.cancel();
    expect(dialogRefSpy.close).toHaveBeenCalledExactlyOnceWith();
  });
});
