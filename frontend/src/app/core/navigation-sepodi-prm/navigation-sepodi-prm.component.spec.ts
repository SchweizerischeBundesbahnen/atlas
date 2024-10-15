import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NavigationSepodiPrmComponent } from './navigation-sepodi-prm.component';
import {AppTestingModule} from "../../app.testing.module";
import SpyObj = jasmine.SpyObj;
import {Router} from "@angular/router";
import {Pages} from "../../pages/pages";

describe('NavigationSepodiPrmComponent', () => {
  let component: NavigationSepodiPrmComponent;
  let fixture: ComponentFixture<NavigationSepodiPrmComponent>;
  let routerSpy: SpyObj<Router>;

  beforeEach(async () => {
    routerSpy = jasmine.createSpyObj(['navigate']);

    await TestBed.configureTestingModule({
      declarations: [NavigationSepodiPrmComponent],
      imports: [AppTestingModule],
      providers: [
        {provide: Router, useValue: routerSpy},
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(NavigationSepodiPrmComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should navigate to the correct traffic point elements URL', () => {
    routerSpy.navigate.and.returnValue(Promise.resolve(true));
    const url = [
      Pages.SEPODI.path,
      Pages.SERVICE_POINTS.path,
      '8589008',
      Pages.TRAFFIC_POINT_ELEMENTS_PLATFORM.path
    ]
    component.navigate(url);

    expect(routerSpy.navigate).toHaveBeenCalledWith([
      Pages.SEPODI.path,
      Pages.SERVICE_POINTS.path,
      '8589008',
      Pages.TRAFFIC_POINT_ELEMENTS_PLATFORM.path,
    ]);
  });
});
