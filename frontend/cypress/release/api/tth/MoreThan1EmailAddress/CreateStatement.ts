import {getXHRResponse} from "rxjs/internal/ajax/getXHRResponse";

describe('TTH: Create a statement with several email-addresses', {testIsolation: false}, () => {

  const ACTIVE_YEAR_MAX_COUNT = 1;
  const status = Object.freeze({
    ACTIVE: 'ACTIVE',
    PLANNED: 'PLANNED',
    ARCHIVED: 'ARCHIVED'
  });
  const allStatusChoices = [status.PLANNED, status.ACTIVE, status.ARCHIVED].join(',');

  let nextUnoccupiedYear = 0;
  let sboid = "";
  let ttfnid = "";

  const today = new Date();
  const tomorrow = new Date();
  tomorrow.setDate(today.getDate() + 1);

  const get = (url: string) => {
    return cy.request({
      method: 'GET',
      url: Cypress.env('API_URL') + url,
      headers: {
        Authorization: `Bearer ${window.sessionStorage.getItem('access_token')}`
      }
    });
  }

  const post = (url: string, body: object = {}) => {
    return cy.request({
      method: 'POST',
      url: Cypress.env('API_URL') + url,
      body: body,
      headers: {
        Authorization: `Bearer ${window.sessionStorage.getItem('access_token')}`
      }
    });
  }

  const postStatement = (url: string, body: object) => {
    const statement = new FormData();
    statement.append("statement", new Blob([JSON.stringify(body)], { type: "application/json" }), "statement");

     return cy.request({
        method: "POST",
        url: Cypress.env('API_URL') + url,
        headers: {
          Authorization: `Bearer ${window.sessionStorage.getItem('access_token')}`,
          "Content-Type": "multipart/form-data",
        },
        body: statement,
      })
  }

  it('Step-1: Login on ATLAS', () => {
    cy.atlasLogin();
  });

  it('Step-2: Close active year, if available', () => {
    const yearStatus = 'ACTIVE';
    const currentYear = new Date().getFullYear();

    get('/line-directory/v1/timetable-hearing/years?statusChoices=' + yearStatus).then((response) => {
      expect(response.status).to.eq(200);
      expect(response).property('body').to.be.an('array');

      // When: There is a timetable hearing year active...
      if (response.body.length > 0) {
        expect(response.body.length).to.eq(ACTIVE_YEAR_MAX_COUNT);
        expect(response.body[0].timetableYear).to.be.greaterThan(currentYear);

        // Then: ... close the active year
        post('/line-directory/v1/timetable-hearing/years/' + response.body[0].timetableYear + '/close', {}).then((response) => {
          expect(response).property('status').to.equal(200);
        });
      }
    });
  });

  it('Step-3: Create a timetable hearing year', () => {
    const createTthYear = () => {
      const hearingYear = nextUnoccupiedYear - 1;
      post('/line-directory/v1/timetable-hearing/years', {
        timetableYear: nextUnoccupiedYear,
        hearingFrom: `${hearingYear}-05-01`,
        hearingTo: `${hearingYear}-05-31`,
        statementCreatableExternal: true,
        statementCreatableInternal: true,
        statementEditable: true
      }).then((response) => {
        expect(response).property('status').to.equal(201);
        expect(response).property('body').property('hearingStatus').to.eq(status.PLANNED);
      });
    }

    // Given: There is no active timetable hearing year
    get('/line-directory/v1/timetable-hearing/years?statusChoices=' + allStatusChoices).then((response) => {
      expect(response).property('status').to.equal(200);
      expect(response.body).to.be.an('array');

      const tthYears = response.body
      const activeYears = tthYears.filter(year => year.hearingStatus === status.ACTIVE).map(year => year.timetableYear);
      expect(activeYears.length, "There shouldn't be any active year.").to.eq(0);

      const usedYears = tthYears.filter(year =>
        (year.hearingStatus === status.PLANNED ||
          year.hearingStatus === status.ARCHIVED)).map(year => year.timetableYear);

      // The TTH-years are always one year later than the current one.
      nextUnoccupiedYear = new Date().getFullYear() + 1;

      // When: There is another next unused year...
      while (usedYears.includes(nextUnoccupiedYear)) {
        nextUnoccupiedYear++; // Increment the year until we find one that's not used
      }
      console.log(nextUnoccupiedYear)
      // Then: Create the next year
      createTthYear();
    });
  });


  it('Step-4: Start the timetable hearing year', () => {
    post(`/line-directory/v1/timetable-hearing/years/${nextUnoccupiedYear}/start`).then((response) => {
      expect(response).property('status').to.equal(200);
      expect(response).property('body').property('hearingStatus').to.eq(status.ACTIVE);
      console.log(nextUnoccupiedYear)
    });
  });

  it('Step-5: Create dependent Business Organisation', () => {
    const generateRandomString = (length) => {
      const characters = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz';
      return Array.from({ length }, () => characters.charAt(Math.floor(Math.random() * characters.length))).join('');
    };

    post("/business-organisation-directory/v1/business-organisations/versions", {
      descriptionDe: generateRandomString(10),
      descriptionFr: generateRandomString(10),
      descriptionIt: generateRandomString(10),
      descriptionEn: generateRandomString(10),
      abbreviationDe: generateRandomString(3),
      abbreviationFr: generateRandomString(3),
      abbreviationIt: generateRandomString(3),
      abbreviationEn: generateRandomString(3),
      organisationNumber: Cypress._.random(10000, 99999).toString(),
      validFrom: today.toISOString().split('T')[0],
      validTo: tomorrow.toISOString().split('T')[0]
    }).then((response) => {
      expect(response).property('status').to.equal(201);

      expect(response).property('body').property('sboid').to.exist;
      sboid = response.body.sboid;
    })
  });

  it('Step-5: Create dependent Time Table Field Number', () => {
    post("/line-directory/v1/field-numbers/versions", {
      swissTimetableFieldNumber: Cypress._.random(10000, 99999).toString(),
      validFrom: today.toISOString().split('T')[0],
      validTo: tomorrow.toISOString().split('T')[0],
      businessOrganisation: sboid,
      number: Cypress._.random(10000, 99999).toString(),
    }).then((response) => {
      expect(response).property('status').to.equal(201);
      expect(response).property('body').property('ttfnid').to.exist;
      ttfnid = response.body.ttfnid;
    })
  });

  it('Step-7: Create statement in active timetable hearing year', () => {
    postStatement("/line-directory/v2/timetable-hearing/statements", {
      statementStatus: "JUNK",
      ttfnid: ttfnid,
      statementSender: {
        emails: [
          "tmp1@sbb.ch",
          "tmp2@sbb.ch",
          "tmp3@sbb.ch",
          "tmp4@sbb.ch",
          "tmp5@sbb.ch",
          "tmp6@sbb.ch",
          "tmp7@sbb.ch",
          "tmp8@sbb.ch",
          "tmp9@sbb.ch",
          "tmp10@sbb.ch"
        ]
      },
      swissCanton: "BERN",
      statement: "I need some more busses please.",
      timetableYear: nextUnoccupiedYear,
    }).then((response) => {
      expect(response).property('status').to.equal(201);
    })
  });

  it('Step-8: Close the timetable hearing year', () => {
    post(`/line-directory/v1/timetable-hearing/years/${nextUnoccupiedYear}/close`).then((response) => {
      expect(response).property('status').to.equal(200);
      expect(response).property('body').property('hearingStatus').to.eq(status.ARCHIVED);
    });
  });
});
