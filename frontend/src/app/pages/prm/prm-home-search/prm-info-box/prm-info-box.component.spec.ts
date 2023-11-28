import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PrmInfoBoxComponent } from './prm-info-box.component';
import { AppTestingModule } from '../../../../app.testing.module';
import { TranslatePipe } from '@ngx-translate/core';

describe('PrmInfoBoxComponent', () => {
  let component: PrmInfoBoxComponent;
  let fixture: ComponentFixture<PrmInfoBoxComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [PrmInfoBoxComponent],
      imports: [AppTestingModule],
      providers: [{ provide: TranslatePipe }],
    }).compileComponents();

    fixture = TestBed.createComponent(PrmInfoBoxComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
