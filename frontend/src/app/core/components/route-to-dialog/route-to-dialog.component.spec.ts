import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RouteToDialogComponent } from './route-to-dialog.component';
import { MAT_DIALOG_DATA, MatDialogModule } from '@angular/material/dialog';
import { RouterTestingModule } from '@angular/router/testing';

describe('RouteToDialogComponent', () => {
  let component: RouteToDialogComponent;
  let fixture: ComponentFixture<RouteToDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [RouteToDialogComponent],
      imports: [MatDialogModule, RouterTestingModule],
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
