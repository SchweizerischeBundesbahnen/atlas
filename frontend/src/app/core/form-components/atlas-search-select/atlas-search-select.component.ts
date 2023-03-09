import {
  Attribute,
  Component,
  ContentChild,
  EventEmitter,
  Inject,
  InjectionToken,
  Input,
  OnDestroy,
  OnInit,
  Output,
  PipeTransform,
  TemplateRef,
} from '@angular/core';
import { debounce, distinctUntilChanged, interval, mergeMap, Observable, of, Subject } from 'rxjs';
import { map, takeUntil } from 'rxjs/operators';
import { SearchByString } from './search-by-string';
import { AbstractControl } from '@angular/forms';
import { TemplateStore } from '../../../template-store';
import { BusinessOrganisationSearchService } from '../../service/business-organisation-search.service';
import { UserService } from '../../../pages/user-administration/service/user.service';
import { BoSelectionDisplayPipe } from '../../components/table-search/bo-selection-display.pipe';
import { UserSelectFormatPipe } from '../../../pages/user-administration/pipes/user-select-format.pipe';
import {
  BusinessOrganisationsService,
  UserAdministrationService,
  UserInformationService,
} from '../../../api';
import { BusinessOrganisationLanguageService } from '../../service/business-organisation-language.service';

export const searchableServiceToken = new InjectionToken('SEARCHABLE_SERVICE');
export const itemDisplayPipeToken = new InjectionToken('ITEM_DISPLAY_PIPE');

function searchableServiceFactory(
  type: string,
  businessOrganisationService: BusinessOrganisationsService,
  userAdministrationService: UserAdministrationService,
  userInformationService: UserInformationService
) {
  if (type === 'businessOrganisation') {
    return new BusinessOrganisationSearchService(businessOrganisationService);
  } else if (type === 'userAdministration') {
    return new UserService(userAdministrationService, userInformationService);
  }
  return null;
}

function itemDisplayPipeFactory(type: string, boLangService: BusinessOrganisationLanguageService) {
  if (type === 'businessOrganisation') {
    return new BoSelectionDisplayPipe(boLangService);
  } else if (type === 'userAdministration') {
    return new UserSelectFormatPipe();
  }
  return null;
}

@Component({
  selector: 'atlas-search-select',
  templateUrl: './atlas-search-select.component.html',
  styleUrls: ['atlas-search-select.component.scss'],
  providers: [
    {
      provide: searchableServiceToken,
      deps: [
        new Attribute('type'),
        BusinessOrganisationsService,
        UserAdministrationService,
        UserInformationService,
      ],
      useFactory: searchableServiceFactory,
    },
    {
      provide: itemDisplayPipeToken,
      deps: [new Attribute('type'), BusinessOrganisationLanguageService],
      useFactory: itemDisplayPipeFactory,
    },
  ],
})
export class AtlasSearchSelectComponent<T> implements OnInit, OnDestroy {
  @Input() selectedItem?: T;
  @Input() formCtrl?: AbstractControl | null;

  @Output() selectionChange: EventEmitter<T> = new EventEmitter<T>();
  @ContentChild(TemplateRef) dropdownElementTemplateRef!: TemplateRef<unknown>;

  constructor(
    @Inject(searchableServiceToken) private readonly searchableService: SearchByString<T>,
    @Inject(itemDisplayPipeToken) readonly itemDisplayPipe: PipeTransform,
    readonly formTemplateStore: TemplateStore,
    @Attribute('formTemplateLabelKey') readonly formTemplateLabelKey: string
  ) {}

  searchResults$: Observable<T[]> = of([]);

  loadingStarted$ = new Subject<boolean>();

  displayDropdownElements = false;
  keyUpEvent = new Subject<string>();
  ngOnDestroyEvent = new Subject();

  get displayedItem(): string {
    return this.selectedItem ? this.itemDisplayPipe.transform(this.selectedItem) : '';
  }

  ngOnInit() {
    this.searchResults$ = this.keyUpEvent.pipe(
      takeUntil(this.ngOnDestroyEvent),
      distinctUntilChanged(),
      map((query) => {
        if (query.length > 1) {
          this.loadingStarted$.next(true);
          return query;
        }
        return '';
      }),
      debounce((value) => (value !== '' ? interval(300) : interval(0))),
      mergeMap((query) => {
        return (query !== '' ? this.searchableService.searchByString(query) : of([])).pipe(
          map((result) => {
            this.loadingStarted$.next(false);
            return result;
          })
        );
      })
    );
  }

  ngOnDestroy() {
    this.ngOnDestroyEvent.complete();
  }

  onInputBlur(): void {
    setTimeout(() => (this.displayDropdownElements = false), 100);
  }
}
