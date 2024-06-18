import { ComponentFixture, TestBed } from '@angular/core/testing';
import { DialogFooterComponent } from './dialog-footer.component';
import {AppTestingModule} from "../../../../app.testing.module";

let component: DialogFooterComponent;
let fixture: ComponentFixture<DialogFooterComponent>;

describe('DialogFooterComponent', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [DialogFooterComponent],
      imports: [AppTestingModule],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DialogFooterComponent);
    component = fixture.componentInstance;

    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
