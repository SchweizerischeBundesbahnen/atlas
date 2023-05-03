import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root',
})
export class TabService {
  getCurrentTabIndex(url: string, tab: Array<Tab>): number {
    let currentTabPath = '';
    if (url.includes('?')) {
      currentTabPath = url.substring(url.lastIndexOf('/') + 1, url.indexOf('?'));
    } else {
      currentTabPath = url.slice(url.lastIndexOf('/') + 1);
    }
    return tab.findIndex((obj) => obj.link === currentTabPath);
  }
}

export interface Tab {
  link: string;
  title: string;
}
