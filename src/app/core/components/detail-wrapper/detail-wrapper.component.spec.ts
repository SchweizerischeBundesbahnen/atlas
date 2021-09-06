import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DetailWrapperComponent } from './detail-wrapper.component';
import { MaterialModule } from '../../module/material.module';
import { TranslateFakeLoader, TranslateLoader, TranslateModule } from '@ngx-translate/core';

describe('DetailWrapperComponent', () => {
  /*eslint-disable */
  let component: DetailWrapperComponent<any>;
  let fixture: ComponentFixture<DetailWrapperComponent<any>>;
  /*eslint-enable */

  const form = { enabled: true };
  const dummyController = jasmine.createSpyObj('dummyController', ['isExistingRecord'], {
    heading: undefined,
    form: form,
  });

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [DetailWrapperComponent],
      imports: [
        MaterialModule,
        TranslateModule.forRoot({
          loader: { provide: TranslateLoader, useClass: TranslateFakeLoader },
        }),
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DetailWrapperComponent);
    component = fixture.componentInstance;
    component.controller = dummyController;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
