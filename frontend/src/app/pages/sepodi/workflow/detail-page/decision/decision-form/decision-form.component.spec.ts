import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DecisionFormComponent } from './decision-form.component';
import { AppTestingModule } from '../../../../../app.testing.module';
import { DecisionFormGroupBuilder } from './decision-form-group';

describe('DecisionFormComponent', () => {
  let component: DecisionFormComponent;
  let fixture: ComponentFixture<DecisionFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DecisionFormComponent, AppTestingModule],
    }).compileComponents();

    fixture = TestBed.createComponent(DecisionFormComponent);
    component = fixture.componentInstance;
    component.form = DecisionFormGroupBuilder.buildFormGroup();

    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
