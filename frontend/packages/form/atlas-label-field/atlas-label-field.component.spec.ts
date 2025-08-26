import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AtlasLabelFieldComponent } from './atlas-label-field.component';
import { TranslatePipe } from '@ngx-translate/core';
import { translateServiceProvider } from '../../../src/app/app.testing.mocks';
import { provideHttpClient } from '@angular/common/http';

describe('AtlasLableFieldComponent', () => {
  let component: AtlasLabelFieldComponent;
  let fixture: ComponentFixture<AtlasLabelFieldComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AtlasLabelFieldComponent],
      providers: [TranslatePipe, translateServiceProvider, provideHttpClient()],
    }).compileComponents();

    fixture = TestBed.createComponent(AtlasLabelFieldComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
