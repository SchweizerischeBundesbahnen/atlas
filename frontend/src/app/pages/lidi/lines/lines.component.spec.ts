import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { LinesComponent } from './lines.component';
import { ContainerLine, LinesService, LineType } from '../../../api';
import { AppTestingModule } from '../../../app.testing.module';
import { TranslatePipe } from '@ngx-translate/core';
import { MockTableComponent } from '../../../app.testing.mocks';

const versionContainer: ContainerLine = {
  objects: [
    {
      slnid: 'slnid',
      description: 'asdf',
      status: 'VALIDATED',
      validFrom: new Date('2021-06-01'),
      validTo: new Date('2029-06-01'),
      businessOrganisation: 'SBB',
      swissLineNumber: 'L1',
      lineType: LineType.Orderly,
    },
  ],
  totalCount: 1,
};

describe('LinesComponent', () => {
  let component: LinesComponent;
  let fixture: ComponentFixture<LinesComponent>;

  // With Spy
  const linesService = jasmine.createSpyObj('linesService', ['getLines']);
  linesService.getLines.and.returnValue(of(versionContainer));

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [LinesComponent, MockTableComponent],
      imports: [AppTestingModule],
      providers: [{ provide: LinesService, useValue: linesService }, TranslatePipe],
    }).compileComponents();

    fixture = TestBed.createComponent(LinesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
