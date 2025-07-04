import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AtlasSlideToggleComponent } from './atlas-slide-toggle.component';
import { FormControl, FormGroup } from '@angular/forms';

describe('AtlasSlideToggleComponent', () => {
  let component: AtlasSlideToggleComponent;
  let fixture: ComponentFixture<AtlasSlideToggleComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AtlasSlideToggleComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(AtlasSlideToggleComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should be created with form', () => {
    component.formGroup = new FormGroup({
      value: new FormControl(false),
    });
    component.controlName = 'value';
    fixture.detectChanges();
    expect(component.formControl).toBeTruthy();

    component.handleToggleClick();

    expect(component.formControl?.value).toBeTrue();
    expect(component.formControl?.dirty).toBeTrue();
  });
});
