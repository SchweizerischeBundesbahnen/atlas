import { ComponentFixture, TestBed } from '@angular/core/testing';
import { LoadingSpinnerComponent } from './loading-spinner.component';
import { By } from '@angular/platform-browser';
import { ChangeDetectionStrategy } from '@angular/core';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';

describe('LoadingSpinnerComponent', () => {
  let component: LoadingSpinnerComponent;
  let fixture: ComponentFixture<LoadingSpinnerComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [LoadingSpinnerComponent],
      imports: [BrowserAnimationsModule],
    })
      .overrideComponent(LoadingSpinnerComponent, {
        set: { changeDetection: ChangeDetectionStrategy.Default },
      })
      .compileComponents();

    fixture = TestBed.createComponent(LoadingSpinnerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create spinning logo', () => {
    component.isLoading = true;
    fixture.detectChanges();

    expect(component).toBeTruthy();

    const loadingSpinnerDiv = fixture.debugElement.query(By.css('.loading-spinner'));
    expect(loadingSpinnerDiv).toBeTruthy();
  });

  it('should do nothing if not loading', () => {
    component.isLoading = false;
    fixture.detectChanges();

    expect(component).toBeTruthy();

    const loadingSpinnerDiv = fixture.debugElement.query(By.css('.loading-spinner'));
    expect(loadingSpinnerDiv).toBeFalsy();
  });
});
