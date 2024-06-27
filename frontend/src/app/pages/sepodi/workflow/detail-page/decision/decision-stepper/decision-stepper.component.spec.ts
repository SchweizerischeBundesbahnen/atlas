import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DecisionStepperComponent } from './decision-stepper.component';
import {AppTestingModule} from "../../../../../app.testing.module";
import {MatDialogRef} from "@angular/material/dialog";

const dialogRefSpy = jasmine.createSpyObj(['close']);

describe('DecisionDialogComponent', () => {
  let component: DecisionStepperComponent;
  let fixture: ComponentFixture<DecisionStepperComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DecisionStepperComponent, AppTestingModule],
      providers:[{provide: MatDialogRef, useValue: dialogRefSpy}]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DecisionStepperComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
