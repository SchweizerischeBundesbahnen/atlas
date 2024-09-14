import {ComponentFixture, TestBed} from '@angular/core/testing';

import {DownloadIconComponent} from './download-icon.component';

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

  it('should return #d3d3d3 when disabled is true', () => {
    component.disabled = true;
    fixture.detectChanges();

    expect(component.fill).toBe('#d3d3d3');
  });

  it('should return #2B2B2B when readonly is true and disabled is false', () => {
    component.disabled = false;
    component.readonly = true;
    fixture.detectChanges();

    expect(component.fill).toBe('#2B2B2B');
  });

  it('should return #adb5bd when readonly and disabled are both false', () => {
    component.disabled = false;
    component.readonly = false;
    fixture.detectChanges();

    expect(component.fill).toBe('#adb5bd');
  });
});
