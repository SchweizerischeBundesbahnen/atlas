import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DecisionDialogComponent } from './decision-dialog.component';

describe('DecisionDialogComponent', () => {
  let component: DecisionDialogComponent;
  let fixture: ComponentFixture<DecisionDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DecisionDialogComponent]
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
