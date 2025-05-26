import { ComponentFixture, TestBed } from '@angular/core/testing';

import { StopPointTerminationInfoComponent } from './stop-point-termination-info.component';
import { TranslateModule, TranslatePipe } from '@ngx-translate/core';

describe('StopPointTerminationInfoComponent', () => {
  let component: StopPointTerminationInfoComponent;
  let fixture: ComponentFixture<StopPointTerminationInfoComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [StopPointTerminationInfoComponent, TranslateModule.forRoot()],
      providers: [{ provide: TranslatePipe }],
    }).compileComponents();

    fixture = TestBed.createComponent(StopPointTerminationInfoComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
