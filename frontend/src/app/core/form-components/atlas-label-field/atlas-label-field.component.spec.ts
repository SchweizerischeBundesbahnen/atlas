import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AtlasLabelFieldComponent } from './atlas-label-field.component';

describe('AtlasLableFieldComponent', () => {
  let component: AtlasLabelFieldComponent;
  let fixture: ComponentFixture<AtlasLabelFieldComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [AtlasLabelFieldComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(AtlasLabelFieldComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
