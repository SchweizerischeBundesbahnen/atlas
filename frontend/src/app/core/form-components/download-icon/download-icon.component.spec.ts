import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DownloadIconComponent } from './download-icon.component';

describe('DownloadIconComponent', () => {
  let component: DownloadIconComponent;
  let fixture: ComponentFixture<DownloadIconComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [DownloadIconComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(DownloadIconComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
