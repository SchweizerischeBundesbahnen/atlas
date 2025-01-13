export default class ReleaseApiUtils {

  static FIRST_ATLAS_DATE = "1700-01-01";
  static LAST_ATLAS_DATE = "9999-12-31";

  /**
   * Give back a day in the format 'DD-MM-YYYY'
   * with today as the default day.
   * If needed dayIncrement can be supplied which can be positive or negative.
   * @param dayIncrement Number of days added to today.
   */
  static atlasDay(dayIncrement: number = 0) {
    return ReleaseApiUtils.date(dayIncrement).toISOString().split('T')[0]; // TODO: Use new Date().toLocaleDateString('en-CA'); ?

  }

  static date(dayIncrement: number = 0) {
    const date = new Date();
    date.setDate(date.getDate() + dayIncrement);
    return date;
  }

  static today() {
    return ReleaseApiUtils.date();
  }

  static tomorrow() {
    return ReleaseApiUtils.date(1);
  }

  static todayAsAtlasString() {
    return ReleaseApiUtils.atlasDay();
  }

  static tomorrowAsAtlasString() {
    return ReleaseApiUtils.atlasDay(1)
  }

  static makeCommonChecks = (response: Cypress.Response<any>, slnid: string, lineVersionId: number) => {
    // Check the status code
    expect(response.status).to.equal(200);

    // Check if the response is an array
    const lineVersions = response.body;
    expect(Array.isArray(lineVersions)).to.be.true; // Verify that it is an array
    expect(lineVersions.length).to.equal(1); // Verify the length of the array

    const lineVersionsFirst = lineVersions[0];

    // Check the values of the first element in the array
    expect(lineVersionsFirst).to.have.property('slnid').that.equals(slnid);
    expect(lineVersionsFirst).to.have.property('id').that.is.a('number').and.equals(lineVersionId);
    return lineVersionsFirst;
  }

  static getPrmObjectById = (body, prmId: number, arePrmObjectsInBody: boolean) => {
    let objects;
    if (arePrmObjectsInBody) {
      expect(body).is.an('array');
      objects = body;
    } else {
      expect(body).to.have.property('objects').that.is.an('array');
      objects = body.objects;
    }
    expect(objects.length).to.be.greaterThan(0);
    return objects.find(obj => obj.id === prmId);
  }
}
