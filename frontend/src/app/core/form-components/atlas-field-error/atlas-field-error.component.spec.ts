import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AtlasFieldErrorComponent } from './atlas-field-error.component';

describe('AtlasFieldErrorComponent', () => {
  let component: AtlasFieldErrorComponent;
  let fixture: ComponentFixture<AtlasFieldErrorComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [AtlasFieldErrorComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(AtlasFieldErrorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
