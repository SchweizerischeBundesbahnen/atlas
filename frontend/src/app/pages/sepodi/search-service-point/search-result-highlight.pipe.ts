import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'searchResultHighlight',
})
export class SearchResultHighlightPipe implements PipeTransform {
  transform(value: string, search: string): unknown {
    if (!value) {
      return search;
    }

    if (!search) {
      return value;
    }
    const regex = new RegExp(search, 'gi');
    return value.replace(regex, (match) => `<b>${match}</b>`);
  }
}
