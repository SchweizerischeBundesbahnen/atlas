import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DecisionOverrideComponent } from './decision-override.component';

describe('DecisionDialogComponent', () => {
  let component: DecisionOverrideComponent;
  let fixture: ComponentFixture<DecisionOverrideComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DecisionOverrideComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DecisionOverrideComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
