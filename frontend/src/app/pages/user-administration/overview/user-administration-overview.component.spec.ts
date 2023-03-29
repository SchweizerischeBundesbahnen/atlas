import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UserAdministrationOverviewComponent } from './user-administration-overview.component';
import { TranslateFakeLoader, TranslateLoader, TranslateModule } from '@ngx-translate/core';
import { RouterTestingModule } from '@angular/router/testing';
import { MaterialModule } from '../../../core/module/material.module';
import { FormsModule } from '@angular/forms';
import { MockAtlasButtonComponent } from '../../../app.testing.mocks';

describe('UserAdministrationOverviewComponent', () => {
  let component: UserAdministrationOverviewComponent;
  let fixture: ComponentFixture<UserAdministrationOverviewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [UserAdministrationOverviewComponent, MockAtlasButtonComponent],
      imports: [
        TranslateModule.forRoot({
          loader: { provide: TranslateLoader, useClass: TranslateFakeLoader },
        }),
        RouterTestingModule,
        MaterialModule,
        FormsModule,
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(UserAdministrationOverviewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
    expect(component.TABS.length).toBe(2);
  });
});
