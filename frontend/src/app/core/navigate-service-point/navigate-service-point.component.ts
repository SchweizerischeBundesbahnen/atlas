import { Component, inject, input, viewChild } from '@angular/core';
import { TranslatePipe } from '@ngx-translate/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { LocationService } from '../../api/service/location/location.service';
import { SloidLocationModel } from '../../api/model/sloidLocationModel';
import { NgSelectComponent } from '@ng-select/ng-select';
import { SearchNavigationType } from '../search-service-point-panel/search-service-point-panel.component';
import { ServicePointSearch, ServicePointSearchType } from '../search-service-point/service-point-search';
import { ActivatedRoute, Router } from '@angular/router';
import { ServicePointNavigationHelper } from './service-point-navigation.helper';
import { InfoIconComponent } from '@atlas/form';

export const NOTFOUND_LABEL = 'COMMON.NODATAFOUND';
export const NAVIGATION_PLACEHOLDER = 'SEPODI.SERVICE_POINTS.NAVIGATION_PLACEHOLDER';

@Component({
  selector: 'atlas-navigate-service-point',
  imports: [TranslatePipe, ReactiveFormsModule, FormsModule, NgSelectComponent, InfoIconComponent],
  providers: [TranslatePipe],
  templateUrl: './navigate-service-point.component.html',
  styleUrls: ['./navigate-service-point.component.scss'],
})
export class NavigateServicePointComponent {
  readonly searchNavigationType = input.required<SearchNavigationType>();
  readonly searchType = input.required<ServicePointSearchType>();
  ngSelect = viewChild.required(NgSelectComponent);

  private _resultMsg = NAVIGATION_PLACEHOLDER;
  items: SloidLocationModel[] = [];

  get resultMsg(): string {
    return this._resultMsg;
  }

  set resultMsg(value: string) {
    this._resultMsg = value;
  }

  private readonly locationService = inject(LocationService);
  private readonly router = inject(Router);
  private readonly route = inject(ActivatedRoute);

  onSearch(): void {
    const searchTerm = this.ngSelect().searchTerm;
    this.doSearch(searchTerm);
  }

  doSearch(searchTerm: string) {
    if (searchTerm) {
      this.locationService.getSloidLocationModel(searchTerm).subscribe((sloidLocations) => {
        if (sloidLocations.length === 0 || sloidLocations.length > 1) {
          this.resultMsg = NOTFOUND_LABEL;
        } else {
          this.navigateTo(sloidLocations[0]);
        }
      });
    }
  }

  navigateTo(sloidLocation: SloidLocationModel): void {
    if (this.searchType() === ServicePointSearch.PRM) {
      this.prmNavigateTo(sloidLocation);
    }
    if (this.searchType() === ServicePointSearch.SePoDi) {
      this.sepodiNavigateTo(sloidLocation);
    }
  }

  prmNavigateTo(model: SloidLocationModel) {
    const commands = ServicePointNavigationHelper.buildPrmNavigation(model, this.searchType().navigationPath);
    this.doNavigate(commands);
  }

  sepodiNavigateTo(model: SloidLocationModel) {
    const commands = ServicePointNavigationHelper.buildSepodiNavigation(model, this.searchType().navigationPath);
    this.doNavigate(commands);
  }

  private doNavigate(commands: string[]) {
    if (commands.length === 0) {
      this.resultMsg = NOTFOUND_LABEL;
    } else {
      this.router.navigate(commands, { relativeTo: this.route }).then(() => {
        this.resultMsg = NAVIGATION_PLACEHOLDER;
        this.ngSelect().close();
      });
    }
  }
}
