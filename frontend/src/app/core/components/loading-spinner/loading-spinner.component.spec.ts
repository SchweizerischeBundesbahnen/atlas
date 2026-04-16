import { ComponentFixture, TestBed } from '@angular/core/testing';
import { beforeEach, describe, expect, it } from 'vitest';
import { LoadingSpinnerComponent } from './loading-spinner.component';
import { By } from '@angular/platform-browser';

describe('LoadingSpinnerComponent', () => {
  let fixture: ComponentFixture<LoadingSpinnerComponent>;

  beforeEach(() => {
    fixture = TestBed.createComponent(LoadingSpinnerComponent);
    fixture.detectChanges();
  });

  it('should create spinning logo', () => {
    fixture.componentRef.setInput('isLoading', true);
    fixture.detectChanges();

    const loadingSpinnerDiv = fixture.debugElement.query(By.css('.loading-spinner'));
    expect(loadingSpinnerDiv).toBeTruthy();
  });

  it('should do nothing if not loading', () => {
    const loadingSpinnerDiv = fixture.debugElement.query(By.css('.loading-spinner'));
    expect(loadingSpinnerDiv).toBeFalsy();
  });
});
