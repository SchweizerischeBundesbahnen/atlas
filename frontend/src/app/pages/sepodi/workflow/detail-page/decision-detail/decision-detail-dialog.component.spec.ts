import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DecisionDetailDialogComponent } from './decision-detail-dialog.component';

describe('DecisionDialogComponent', () => {
  let component: DecisionDetailDialogComponent;
  let fixture: ComponentFixture<DecisionDetailDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DecisionDetailDialogComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DecisionDetailDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
