import { ComponentFixture, TestBed } from '@angular/core/testing';
import { LoadingSpinnerComponent } from './loading-spinner.component';
import { By } from '@angular/platform-browser';
import { ChangeDetectionStrategy } from '@angular/core';

describe('LoadingSpinnerComponent', () => {
  let component: LoadingSpinnerComponent;
  let fixture: ComponentFixture<LoadingSpinnerComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [LoadingSpinnerComponent],
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

    expect(fixture.debugElement.query(By.css('.full-screen'))).toBeFalsy();
  });

  it('should create fullscreen spinning logo', () => {
    component.isLoading = true;
    component.fullScreen = true;
    fixture.detectChanges();

    expect(fixture.debugElement.query(By.css('.full-screen'))).toBeTruthy();
  });

  it('should do nothing if not loading', () => {
    component.isLoading = false;
    fixture.detectChanges();

    expect(component).toBeTruthy();

    const loadingSpinnerDiv = fixture.debugElement.query(By.css('.loading-spinner'));
    expect(loadingSpinnerDiv).toBeFalsy();
  });
});
