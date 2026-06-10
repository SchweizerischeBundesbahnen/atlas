import { ComponentFixture, TestBed } from '@angular/core/testing';
import { beforeEach, describe, expect, it } from 'vitest';

import { StopPointWorkflowBasicInfo } from './stop-point-workflow-basic-info';
import { TranslatePipe } from '@ngx-translate/core';
import { BoSelectionDisplayPipe } from '../../../../core/form-components/bo-select/bo-selection-display.pipe';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { BERN_WYLEREGG } from '../../../../../test/data/service-point';

describe('StopPointWorkflowBasicInfo', () => {
  let component: StopPointWorkflowBasicInfo;
  let fixture: ComponentFixture<StopPointWorkflowBasicInfo>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [StopPointWorkflowBasicInfo],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: TranslatePipe },
        { provide: BoSelectionDisplayPipe },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(StopPointWorkflowBasicInfo);
    component = fixture.componentInstance;
    fixture.componentRef.setInput('stopPoint', BERN_WYLEREGG);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
