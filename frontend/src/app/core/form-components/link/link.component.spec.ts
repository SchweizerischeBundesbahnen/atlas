import { ComponentFixture, TestBed } from '@angular/core/testing';
import { beforeEach, describe, expect, it } from 'vitest';
import { LinkComponent } from './link.component';
import { translateServiceProvider } from '../../../app.testing.mocks';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { inputBinding } from '@angular/core';

describe('LinkComponent', () => {
  let component: LinkComponent;
  let fixture: ComponentFixture<LinkComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting(), translateServiceProvider],
    });

    const labelInputName: keyof LinkComponent = 'label';
    fixture = TestBed.createComponent(LinkComponent, {
      bindings: [inputBinding(labelInputName, () => 'test label')],
    });
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
