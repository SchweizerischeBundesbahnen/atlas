import { Observable } from 'rxjs';

export interface SearchByString<T> {
  searchByString(searchQuery: string): Observable<T[]>;
}
