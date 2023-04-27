import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FileComponent } from './file.component';
import { AppTestingModule } from '../../../../app.testing.module';
import { FileSizePipe } from '../file-size/file-size.pipe';

describe('FileComponent', () => {
  let component: FileComponent;
  let fixture: ComponentFixture<FileComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [FileComponent, FileSizePipe],
      imports: [AppTestingModule],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(FileComponent);
    component = fixture.componentInstance;
    component.file = {
      name: 'filename.pdf',
      size: 10,
      type: 'application/pdf',
    } as File;
    fixture.detectChanges();
  });

  describe('Component Rendering', () => {
    it('should create', () => {
      expect(component).toBeTruthy();
    });
  });
});
