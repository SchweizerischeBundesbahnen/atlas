import {Pipe, PipeTransform} from '@angular/core';

@Pipe({
    name: 'searchResultHighlight',
    standalone: false
})
export class SearchResultHighlightPipe implements PipeTransform {
  transform(value: string, search: string): string {
    if (!value) {
      return search;
    }

    if (!search) {
      return value;
    }
    const regex = new RegExp(this.escapeRegExp(search), 'gi');
    return value.replace(regex, (match) => {
      return `<b>${match}</b>`;
    });
  }

  //RegExp Escaping Proposal https://tc39.es/proposal-regex-escaping/
  escapeRegExp(text: string) {
    return text.replace(/[-[\]{}()*+?.,\\^$|#\s]/g, '\\$&');
  }
}
