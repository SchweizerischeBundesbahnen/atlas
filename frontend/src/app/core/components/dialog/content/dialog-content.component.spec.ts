import { ComponentFixture, TestBed } from '@angular/core/testing';
import { DialogContentComponent } from './dialog-content.component';
import {AppTestingModule} from "../../../../app.testing.module";

let component: DialogContentComponent;
let fixture: ComponentFixture<DialogContentComponent>;

describe('DialogContentComponent', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [DialogContentComponent],
      imports: [AppTestingModule],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DialogContentComponent);
    component = fixture.componentInstance;

    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
