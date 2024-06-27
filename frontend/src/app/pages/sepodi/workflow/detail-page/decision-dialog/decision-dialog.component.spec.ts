import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DecisionDialogComponent } from './decision-dialog.component';
import {AppTestingModule} from "../../../../../app.testing.module";
import {MatDialogRef} from "@angular/material/dialog";

const dialogRefSpy = jasmine.createSpyObj(['close']);

describe('DecisionDialogComponent', () => {
  let component: DecisionDialogComponent;
  let fixture: ComponentFixture<DecisionDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DecisionDialogComponent, AppTestingModule],
      providers:[{provide: MatDialogRef, useValue: dialogRefSpy}]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DecisionDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
