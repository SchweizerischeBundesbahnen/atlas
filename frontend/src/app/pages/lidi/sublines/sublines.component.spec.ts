import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { TableComponent } from '../../../core/components/table/table.component';
import { LoadingSpinnerComponent } from '../../../core/components/loading-spinner/loading-spinner.component';
import { ContainerSubline, Status, SublinesService, SublineType } from '../../../api';
import { SublinesComponent } from './sublines.component';
import { AppTestingModule } from '../../../app.testing.module';
import { TranslatePipe } from '@ngx-translate/core';
import { Component, Input, TemplateRef } from '@angular/core';
import { MockAppTableSearchComponent } from '../../../app.testing.mocks';

const versionContainer: ContainerSubline = {
  objects: [
    {
      slnid: 'slnid',
      description: 'asdf',
      validFrom: new Date('2021-06-01'),
      validTo: new Date('2029-06-01'),
      status: Status.Validated,
      businessOrganisation: 'SBB',
      swissSublineNumber: 'L1:2',
      sublineType: SublineType.Technical,
    },
  ],
  totalCount: 1,
};

describe('SublinesComponent', () => {
  let component: SublinesComponent;
  let fixture: ComponentFixture<SublinesComponent>;

  // With Spy
  const sublinesService = jasmine.createSpyObj('linesService', ['getSublines']);
  sublinesService.getSublines.and.returnValue(of(versionContainer));
  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [
        SublinesComponent,
        TableComponent,
        LoadingSpinnerComponent,
        MockAppTableSearchComponent,
      ],
      imports: [AppTestingModule],
      providers: [{ provide: SublinesService, useValue: sublinesService }, TranslatePipe],
    }).compileComponents();

    fixture = TestBed.createComponent(SublinesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(sublinesService.getSublines).toHaveBeenCalledOnceWith(
      undefined,
      undefined,
      undefined,
      undefined,
      undefined,
      0,
      10,
      ['number,ASC', 'slnid,ASC']
    );
    expect(component.sublines.length).toBe(1);
    expect(component.totalCount$).toBe(1);
  });
});
