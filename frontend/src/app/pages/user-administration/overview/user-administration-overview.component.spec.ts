import { ComponentFixture, TestBed } from '@angular/core/testing';
import { beforeEach, describe, expect, it } from 'vitest';
import { UserAdministrationOverviewComponent } from './user-administration-overview.component';
import { TranslatePipe } from '@ngx-translate/core';
import { ActivatedRoute } from '@angular/router';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { translateServiceProvider } from '../../../app.testing.mocks';

describe('UserAdministrationOverviewComponent', () => {
  let component: UserAdministrationOverviewComponent;
  let fixture: ComponentFixture<UserAdministrationOverviewComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [UserAdministrationOverviewComponent],
      providers: [
        translateServiceProvider,
        TranslatePipe,
        {
          provide: ActivatedRoute,
          useValue: { snapshot: { data: { user: {} } } },
        },
        provideHttpClient(),
        provideHttpClientTesting(),
      ],
    });

    fixture = TestBed.createComponent(UserAdministrationOverviewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
    expect(component.TABS.length).toBe(2);
  });
});
