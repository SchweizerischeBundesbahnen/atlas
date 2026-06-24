import { ComponentFixture, TestBed } from '@angular/core/testing';
import { beforeEach, describe, expect, it } from 'vitest';
import { StatementTextComponent } from './statement-text.component';
import { translateServiceProvider } from '../../../../app.testing.mocks';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { FormControl, FormGroup } from '@angular/forms';

const formGroup = new FormGroup({
  statementAnonymous: new FormControl(true),
  statement: new FormControl('Statement original text'),
  anonymousStatement: new FormControl('Anonymous statement'),
});

describe('StatementText', () => {
  let component: StatementTextComponent;
  let fixture: ComponentFixture<StatementTextComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [StatementTextComponent],
      providers: [translateServiceProvider, provideHttpClient(), provideHttpClientTesting()],
    });

    fixture = TestBed.createComponent(StatementTextComponent);
    component = fixture.componentInstance;
    fixture.componentRef.setInput('isNew', false);
    fixture.componentRef.setInput('form', formGroup);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should not show original text if anonymous text is present', () => {
    expect(component.hasAnonymousText).toBe(true);
    expect(component.showOriginalText).toBe(false);
  });

  it('should toggle showing of original text', () => {
    expect(component.showOriginalText).toBe(false);

    component.toggleOriginalText();
    expect(component.showOriginalText).toBe(true);

    component.toggleOriginalText();
    expect(component.showOriginalText).toBe(false);
  });
});
