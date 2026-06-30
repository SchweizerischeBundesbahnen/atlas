import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AtlasFieldErrorSfComponent } from './atlas-field-error-sf.component';

describe('AtlasFieldErrorSfComponent', () => {
  let component: AtlasFieldErrorSfComponent;
  let fixture: ComponentFixture<AtlasFieldErrorSfComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AtlasFieldErrorSfComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(AtlasFieldErrorSfComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
