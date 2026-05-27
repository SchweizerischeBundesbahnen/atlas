import { Language } from '../model/language';

export function mapToLanguageModel(lang: string){
  return Object.values(Language).find(l => l === lang.toUpperCase());
}
