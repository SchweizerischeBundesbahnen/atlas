import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterModule } from '@angular/router';
import { HomeComponent } from './home.component';
import { By } from '@angular/platform-browser';
import { pageServiceMock, translateServiceProvider } from '../../app.testing.mocks';
import { PageService } from '../../core/pages/page.service';
import { beforeEach, describe, expect, it } from 'vitest';

describe('HomeComponent', () => {
  let component: HomeComponent;
  let fixture: ComponentFixture<HomeComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterModule.forRoot([]), HomeComponent],
      providers: [translateServiceProvider, { provide: PageService, useValue: pageServiceMock }],
    });

    fixture = TestBed.createComponent(HomeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should create 4 cards', () => {
    const cards = fixture.debugElement.queryAll(By.css('.card'));
    expect(cards.length).toBe(4);
  });
});
