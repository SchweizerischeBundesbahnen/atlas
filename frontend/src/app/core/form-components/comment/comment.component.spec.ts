import { ComponentFixture, TestBed } from '@angular/core/testing';
import { beforeEach, describe, expect, it } from 'vitest';
import { CommentComponent } from './comment.component';
import { FormControl, FormGroup } from '@angular/forms';
import { inputBinding } from '@angular/core';
import { translateServiceProvider } from '../../../app.testing.mocks';

describe('CommentComponent', () => {
  let component: CommentComponent;
  let fixture: ComponentFixture<CommentComponent>;

  let formGroupInput: FormGroup;

  beforeEach(() => {
    formGroupInput = new FormGroup({
      comment: new FormControl('test'),
    });

    TestBed.configureTestingModule({
      providers: [translateServiceProvider],
    });

    const formGroupInputName: keyof CommentComponent = 'formGroup';
    fixture = TestBed.createComponent(CommentComponent, {
      bindings: [inputBinding(formGroupInputName, () => formGroupInput)],
    });
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
    expect(component.formGroup().value).toEqual({ comment: 'test' });
  });
});
