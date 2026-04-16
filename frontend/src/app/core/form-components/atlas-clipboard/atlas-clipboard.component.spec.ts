import { ComponentFixture, TestBed } from '@angular/core/testing';
import { beforeEach, describe, expect, it } from 'vitest';
import { AtlasClipboardComponent } from './atlas-clipboard.component';
import { translateServiceProvider } from '../../../app.testing.mocks';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';

describe('AtlasClipboardComponent', () => {
  let component: AtlasClipboardComponent;
  let fixture: ComponentFixture<AtlasClipboardComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [translateServiceProvider, provideHttpClient(), provideHttpClientTesting()],
    });

    fixture = TestBed.createComponent(AtlasClipboardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
