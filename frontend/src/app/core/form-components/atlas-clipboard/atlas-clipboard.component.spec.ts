import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AtlasClipboardComponent } from './atlas-clipboard.component';
import { FormModule } from '../../module/form.module';
import { TranslatePipe } from '@ngx-translate/core';
import { translateServiceProvider } from '../../../app.testing.mocks';
import { provideHttpClient } from '@angular/common/http';

describe('AtlasClipboardComponent', () => {
  let component: AtlasClipboardComponent;
  let fixture: ComponentFixture<AtlasClipboardComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FormModule, AtlasClipboardComponent],
      providers: [
        { provide: TranslatePipe },
        translateServiceProvider,
        provideHttpClient(),
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(AtlasClipboardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
