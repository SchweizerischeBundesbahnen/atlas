import { ComponentFixture, TestBed } from '@angular/core/testing';
import { DecisionStepperComponent } from './decision-stepper.component';
import { MatDialogRef } from '@angular/material/dialog';
import { AppTestingModule } from '../../../../../../app.testing.module';

describe('DecisionDialogComponent', () => {
  let component: DecisionStepperComponent;
  let fixture: ComponentFixture<DecisionStepperComponent>;

  let dialogRefSpy = jasmine.createSpyObj(['close']);

  beforeEach(async () => {
    dialogRefSpy = jasmine.createSpyObj(['close']);

    await TestBed.configureTestingModule({
      imports: [DecisionStepperComponent, AppTestingModule],
      providers: [{ provide: MatDialogRef, useValue: dialogRefSpy }],
    }).compileComponents();

    fixture = TestBed.createComponent(DecisionStepperComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should complete obtain otp step', (done) => {
    component.mail.controls.mail.setValue('techsupport@atlas.ch');
    component.obtainOtp.subscribe((stepData) => {
      expect(stepData.mail.value).toEqual('techsupport@atlas.ch');
      expect(stepData.continue).toBeDefined();
      expect(stepData.swapLoading).toBeDefined();
      done();
    });

    component.completeObtainOtpStep();
  });

  it('should complete verify pin step', (done) => {
    component.pin.controls.pin.setValue('234313');
    component.verifyPin.subscribe((stepData) => {
      expect(stepData.mail.value).toEqual('');
      expect(stepData.pin.value).toEqual('234313');
      expect(stepData.continue).toBeDefined();
      expect(stepData.swapLoading).toBeDefined();
      done();
    });
    component.completeVerifyPinStep();
  });

  it('should complete decision', (done) => {
    component.decision.setValue({
      firstName: 'atlas',
      lastName: 'atlas',
      organisation: 'test',
      personFunction: 'chef',
      judgement: 'YES',
      motivation: 'cool',
    });
    component.sendDecision.subscribe((stepData) => {
      expect(stepData.decision).toEqual({
        examinantMail: '',
        pinCode: '',
        judgement: 'YES',
        motivation: 'cool',
        firstName: 'atlas',
        lastName: 'atlas',
        organisation: 'test',
        personFunction: 'chef',
      });
      expect(stepData.verifiedExaminant).toBeUndefined();
      expect(stepData.swapLoading).toBeDefined();
      done();
    });
    component.completeDecision();
  });

  it('should resend mail', (done) => {
    component.obtainOtp.subscribe((stepData) => {
      expect(stepData.mail.value).toEqual('');
      expect(stepData.continue).toBeDefined();
      expect(stepData.swapLoading).toBeDefined();
      done();
    });
    component.resendMail();
  });

  it('should cancel (close dialog immediately) on step 1', () => {
    component.cancel();
    expect(dialogRefSpy.close).toHaveBeenCalledOnceWith();
  });
});
