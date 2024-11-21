const ACTIVE_YEAR_MAX_COUNT = 1;
describe('TTH: Create a statement with several email-addresses', {testIsolation: false}, () => {

  function tmp() {
    // When: Creating a year...
    const body = {
      timetableYear: 2024,
      hearingFrom: "2023-05-01",
      hearingTo: "2023-05-31",
      statementCreatableExternal: true,
      statementCreatableInternal: true,
      statementEditable: true
    }
    post('/line-directory/v1/timetable-hearing/years/', body).then((response) => {
      expect(response.status).to.eq(200);
    });
  }

  function get(url: string) {
    return cy.request({
      method: 'GET', url: Cypress.env('API_URL') + url, headers: {
        Authorization: `Bearer ${window.sessionStorage.getItem('access_token')}`
      }
    });
  }

  function post(url: string, body: object) {
    return cy.request({
      method: 'POST', url: Cypress.env('API_URL') + url, body: body, headers: {
        Authorization: `Bearer ${window.sessionStorage.getItem('access_token')}`
      }
    });
  }

  // ----------------------------------------------------------------------------------------------------------------

  it('Step-1: Login on ATLAS', () => {
    cy.atlasLogin();
  });

  it('Step-2: Close active year, if available', () => {
    const yearStatus = 'ACTIVE';
    const currentYear = new Date().getFullYear();

    get('/line-directory/v1/timetable-hearing/years?statusChoices=' + yearStatus).then((response) => {
      expect(response.status).to.eq(200);
      expect(response.body).to.be.an('array');

      // When: There is a timetable hearing year active...
      if (response.body.length > 0) {
        expect(response.body.length).to.eq(ACTIVE_YEAR_MAX_COUNT);
        expect(response.body[0].timetableYear).to.be.greaterThan(currentYear);

        // Then: ... close the active year
        post('/line-directory/v1/timetable-hearing/years/' + response.body[0].timetableYear + '/close', {}).then((response) => {
          expect(response.status).to.eq(200);
        });
      }
    });
  });

  it('Step-3: Create and start a timetable hearing year', () => {
    // Given: There is no active timetable hearing year
    const allStatusChoices = 'ACTIVE,PLANNED,ARCHIVED';
    get('/line-directory/v1/timetable-hearing/years?statusChoices=' + allStatusChoices).then((response) => {
      expect(response.status).to.eq(200);
      expect(response.body).to.be.an('array');

      const tthYears = response.body
      const activeYears = tthYears.filter(year => year.hearingStatus === "ACTIVE").map(year => year.timetableYear);
      expect(activeYears.length, "There shouldn't be any active year.").to.eq(0);

      const usedYears = tthYears.filter(year =>
        (year.hearingStatus === "PLANNED" ||
          year.hearingStatus === "ARCHIVED")).map(year => year.timetableYear);

      // The TTH-years are always one year later than the current one.
      let nextUnoccupiedYear = new Date().getFullYear() + 1;

      // When: There is another next unused year...
      while (usedYears.includes(nextUnoccupiedYear)) {
        nextUnoccupiedYear++; // Increment the year until we find one that's not used
      }

      // Then: Create and start the next year

    });
  });
});
