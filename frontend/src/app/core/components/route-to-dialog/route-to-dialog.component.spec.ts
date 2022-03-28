import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RouteToDialogComponent } from './route-to-dialog.component';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';
import { AppTestingModule } from '../../../app.testing.module';

describe('RouteToDialogComponent', () => {
  let component: RouteToDialogComponent;
  let fixture: ComponentFixture<RouteToDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [RouteToDialogComponent],
      imports: [AppTestingModule],
      providers: [
        {
          provide: MAT_DIALOG_DATA,
          useValue: {},
        },
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(RouteToDialogComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
