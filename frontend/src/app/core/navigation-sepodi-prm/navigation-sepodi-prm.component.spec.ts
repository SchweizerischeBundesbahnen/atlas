import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NavigationSepodiPrmComponent } from './navigation-sepodi-prm.component';
import {AppTestingModule} from "../../app.testing.module";

describe('NavigationSepodiPrmComponent', () => {
  let component: NavigationSepodiPrmComponent;
  let fixture: ComponentFixture<NavigationSepodiPrmComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [NavigationSepodiPrmComponent],
      imports: [AppTestingModule]
    })
    .compileComponents();

    fixture = TestBed.createComponent(NavigationSepodiPrmComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
