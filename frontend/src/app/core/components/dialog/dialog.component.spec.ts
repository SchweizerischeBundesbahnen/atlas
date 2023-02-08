import { ComponentFixture, TestBed } from '@angular/core/testing';
import {
  MAT_LEGACY_DIALOG_DATA as MAT_DIALOG_DATA,
  MatLegacyDialogRef as MatDialogRef,
} from '@angular/material/legacy-dialog';
import { DialogComponent } from './dialog.component';
import { By } from '@angular/platform-browser';
import { AppTestingModule } from '../../../app.testing.module';

describe('DialogComponent', () => {
  let component: DialogComponent;
  let fixture: ComponentFixture<DialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [DialogComponent],
      imports: [AppTestingModule],
      providers: [
        { provide: MAT_DIALOG_DATA, useValue: { title: 'Title', message: 'message' } },
        { provide: MatDialogRef, useValue: {} },
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create with title and text', () => {
    expect(component).toBeTruthy();

    const title = fixture.debugElement.query(By.css('h1'));
    expect(title.nativeElement.innerText).toBe('Title');

    const content = fixture.debugElement.query(By.css('mat-dialog-content'));
    expect(content.nativeElement.innerText).toBe('message');
  });
});
