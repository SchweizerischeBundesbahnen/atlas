import { ComponentFixture, TestBed } from '@angular/core/testing';
import { beforeEach, describe, expect, it } from 'vitest';
import {
  HIDE_ORIGINAL_TEXT_LABEL,
  LOCK_ICON,
  SHOW_ORIGINAL_TEXT_LABEL,
  StatementTextComponent,
  UNLOCK_ICON,
} from './statement-text.component';
import { translateServiceProvider } from '../../../../app.testing.mocks';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { FormControl, FormGroup } from '@angular/forms';

const formGroup = new FormGroup({
  statementAnonymous: new FormControl(true),
  statement: new FormControl('Statement original text'),
  anonymousStatement: new FormControl(false),
});

describe('StatementText', () => {
  let component: StatementTextComponent;
  let fixture: ComponentFixture<StatementTextComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [StatementTextComponent],
      providers: [
        translateServiceProvider,
        provideHttpClient(),
        provideHttpClientTesting(),
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(StatementTextComponent);
    component = fixture.componentInstance;
    fixture.componentRef.setInput('isNew', false);
    fixture.componentRef.setInput('form', formGroup);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should uncheck StatementAnonymous', () => {
    //when
    component.checkStatementAnonymous(false);
    //then
    expect(component.statementText.checkboxStatementAnonymous).toBe(false);
    expect(component.statementText.showOriginalStatementText).toBe(true);
    expect(component.statementText.showOriginalTextButton).toBe(false);
    expect(component.statementText.currentAnonymousStatement).toBeUndefined();
  });

  it('should uncheck StatementAnonymous when currentAnonymusStatement is already set', () => {
    //when
    component.statementText.currentAnonymousStatement =
      'Statement original text';
    component.checkStatementAnonymous(false);
    //then
    expect(component.statementText.checkboxStatementAnonymous).toBe(false);
    expect(component.statementText.showOriginalStatementText).toBe(true);
    expect(component.statementText.showOriginalTextButton).toBe(false);
    expect(component.form().getRawValue().anonymousStatement).toEqual(
      'Statement original text'
    );
  });

  it('should check StatementAnonymous', () => {
    //when
    component.statementText.currentAnonymousStatement =
      'Statement original text';
    component.checkStatementAnonymous(true);
    //then
    expect(component.statementText.checkboxStatementAnonymous).toBe(true);
    expect(component.statementText.showOriginalStatementText).toBe(true);
    expect(component.statementText.showOriginalTextButton).toBe(false);
    expect(component.form().getRawValue().anonymousStatement).toBeNull();
  });

  it('should anonymizeStatement', () => {
    //when
    component.statementText.anonymizeStatementButtonActive = true;
    component.anonymizeStatement();
    fixture.detectChanges();
    //then
    expect(component.statementText.anonymizeStatementButtonActive).toBe(false);
    expect(component.form().controls.anonymousStatement.getRawValue()).toEqual(
      'Statement original text'
    );
    expect(component.form().dirty).toBe(true);
  });

  it('should uncheck anonymizeStatement', () => {
    //when
    component.statementText.anonymizeStatementButtonActive = false;
    component.anonymizeStatement();
    fixture.detectChanges();
    //then
    expect(component.statementText.anonymizeStatementButtonActive).toBe(true);
    expect(component.form().controls.anonymousStatement.getRawValue()).toEqual(
      'Statement original text'
    );
    expect(component.form().dirty).toBe(true);
  });

  it('should not show original text', () => {
    //when
    component.statementText.showOriginalStatementText = false;
    component.showOriginalText();
    //then
    expect(component.statementText.showOriginalStatementText).toBe(true);
    expect(component.statementText.originalTextButtonLabel).toBe(
      HIDE_ORIGINAL_TEXT_LABEL
    );
    expect(component.statementText.originalTextButtonIcon).toBe(UNLOCK_ICON);
  });

  it('should show original text', () => {
    //when
    component.statementText.showOriginalStatementText = true;
    component.showOriginalText();
    //then
    expect(component.statementText.showOriginalStatementText).toBe(false);
    expect(component.statementText.originalTextButtonLabel).toBe(
      SHOW_ORIGINAL_TEXT_LABEL
    );
    expect(component.statementText.originalTextButtonIcon).toBe(LOCK_ICON);
  });
});
