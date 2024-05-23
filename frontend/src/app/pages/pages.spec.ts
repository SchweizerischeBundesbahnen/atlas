import {Pages} from "./pages";
import {environment} from "../../environments/environment";

describe('Pages', () => {

  it('should not return submenu when sepodiWorkflowEnabled is false', () => {
    environment.sepodiWorkflowEnabled = false;

    Pages.viewablePages = [Pages.SEPODI];

    const result = Pages.enabledPages;

    expect(result[0].subpages!.length).toBe(0);
  });

  it('should return submenu when sepodiWorkflowEnabled is true', () => {
    environment.sepodiWorkflowEnabled = true;
    Pages.viewablePages = [Pages.SEPODI];

    const result = Pages.enabledPages;

    expect(result[0].subpages!.length).toBe(1);
    expect(result[0].subpages![0].title).toBe('PAGES.WORKFLOW.TITLE_HEADER');
  });
});
