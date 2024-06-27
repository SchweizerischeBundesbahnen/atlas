import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DecisionOverrideComponent } from './decision-override.component';
import {AppTestingModule} from "../../../../../../app.testing.module";

describe('DecisionOverrideComponent', () => {
  let component: DecisionOverrideComponent;
  let fixture: ComponentFixture<DecisionOverrideComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DecisionOverrideComponent, AppTestingModule]
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
