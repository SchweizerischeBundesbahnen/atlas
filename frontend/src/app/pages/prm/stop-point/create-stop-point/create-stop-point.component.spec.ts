import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CreateStopPointComponent } from './create-stop-point.component';
import { TranslateFakeLoader, TranslateLoader, TranslateModule } from '@ngx-translate/core';

describe('CreateStopPointComponent', () => {
  let component: CreateStopPointComponent;
  let fixture: ComponentFixture<CreateStopPointComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [CreateStopPointComponent],
      imports: [
        TranslateModule.forRoot({
          loader: { provide: TranslateLoader, useClass: TranslateFakeLoader },
        }),
      ],
    });
    fixture = TestBed.createComponent(CreateStopPointComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
