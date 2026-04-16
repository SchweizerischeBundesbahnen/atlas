import { ComponentFixture, TestBed } from '@angular/core/testing';
import { beforeEach, describe, expect, it } from 'vitest';
import { StatementPersonalInformationComponent } from './statement-personal-information.component';
import { translateServiceProvider } from '../../../../app.testing.mocks';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { statement, statementFormGroup } from '../statement-test-util';

describe('StatementPersonalInformation', () => {
  let component: StatementPersonalInformationComponent;
  let fixture: ComponentFixture<StatementPersonalInformationComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [translateServiceProvider, provideHttpClient(), provideHttpClientTesting()],
    });

    fixture = TestBed.createComponent(StatementPersonalInformationComponent);
    component = fixture.componentInstance;
    fixture.componentRef.setInput('form', statementFormGroup);
    fixture.componentRef.setInput('statement', statement);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should get email', () => {
    expect(component.emails).toEqual('fan@yb.ch\n' + 'fan@nap.ch');
  });
});
