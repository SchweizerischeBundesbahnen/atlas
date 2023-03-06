import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AtlasLableFieldComponent } from './atlas-lable-field.component';

describe('AtlasLableFieldComponent', () => {
  let component: AtlasLableFieldComponent;
  let fixture: ComponentFixture<AtlasLableFieldComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [AtlasLableFieldComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(AtlasLableFieldComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
