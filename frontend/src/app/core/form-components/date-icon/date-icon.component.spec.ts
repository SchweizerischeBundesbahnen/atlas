import { ComponentFixture, TestBed } from '@angular/core/testing';
import { DateIconComponent } from './date-icon.component';

describe('DateIconComponent', () => {
  let component: DateIconComponent;
  let fixture: ComponentFixture<DateIconComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DateIconComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(DateIconComponent);
    component = fixture.componentInstance;
    fixture.componentRef.setInput('enabled', true);
    fixture.detectChanges();
  });

  it('should be enabled', () => {
    expect(component.enabled()).toBeTrue();
  });
});
