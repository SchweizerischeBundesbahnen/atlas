import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UploadIconComponent } from './upload-icon.component';

describe('UploadIconComponent', () => {
  let component: UploadIconComponent;
  let fixture: ComponentFixture<UploadIconComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [UploadIconComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(UploadIconComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
