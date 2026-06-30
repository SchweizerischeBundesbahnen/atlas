import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TextFieldSfComponent } from './text-field-sf.component';

describe('TextFieldSfComponent', () => {
  let component: TextFieldSfComponent;
  let fixture: ComponentFixture<TextFieldSfComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TextFieldSfComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(TextFieldSfComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
