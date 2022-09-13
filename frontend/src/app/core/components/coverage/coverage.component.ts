import { Component, Input, OnChanges, OnInit } from '@angular/core';
import { Coverage, CoverageType, LinesService, SublinesService } from '../../../api';
import { Pages } from '../../../pages/pages';
import { Record } from '../base-detail/record';
import { Page } from '../../model/page';

const LINE_SUBLINE_HEADING = 'COMMON.COVERAGE.LINE_SUBLINE';

@Component({
  selector: 'app-coverage',
  templateUrl: './coverage.component.html',
  styleUrls: ['./coverage.component.scss'],
})
export class CoverageComponent implements OnInit, OnChanges {
  @Input() currentRecord!: Record;
  @Input() pageType!: Page;
  coverage!: Coverage;
  heading!: string;

  constructor(private linesService: LinesService, private sublineService: SublinesService) {}

  ngOnInit(): void {
    this.displayCoverage();
  }

  displayCoverage() {
    if (this.pageType === Pages.LINES) {
      if (this.currentRecord.slnid != null) {
        this.linesService.getLineCoverage(this.currentRecord.slnid).subscribe((value) => {
          this.coverage = value;
          this.heading = LINE_SUBLINE_HEADING;
        });
      }
    }
    if (this.pageType === Pages.SUBLINES) {
      if (this.currentRecord.slnid != null) {
        this.sublineService.getSublineCoverage(this.currentRecord.slnid).subscribe((value) => {
          this.coverage = value;
          this.heading = LINE_SUBLINE_HEADING;
        });
      }
    }
  }

  getIcon(coverageType: CoverageType) {
    if (coverageType === CoverageType.Complete) {
      return 'bi bi-check-circle-fill';
    }
    return 'bi bi-exclamation-triangle-fill';
  }

  ngOnChanges(): void {
    this.displayCoverage();
  }
}
