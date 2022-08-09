const csvToJson = require("convert-csv-to-json");
const axios = require("axios").default;
const fs = require("fs");

let outputFilePath = "";

console.log("======================== Started ========================\n");
main(process.argv.slice(2))
  .then(([failedImports, numberOfElements]) => {
    const finalMessage = `Failed Imports: ${failedImports} of ${numberOfElements}`;
    fs.appendFileSync(outputFilePath, finalMessage);
    console.log("======================== Finished ========================");
    console.log(finalMessage);
  })
  .catch((e) => console.log(e));

async function main(args) {
  let failedImports = 0;
  let token = "";
  let url = "";
  let csv = "";
  for (let i = 0; i < args.length; i += 2) {
    if (args[i] === "--token") {
      token = args[i + 1];
    }
    if (args[i] === "--url") {
      url = args[i + 1];
    }
    if (args[i] === "--csv") {
      csv = args[i + 1];
    }
  }
  if (!(token && url && csv)) {
    throw "Error: Not all required parameters (url, token, csv) passed";
  }
  const fileName = csv.split("\\").pop().split("/").pop().split(".")[0];
  outputFilePath = `output/${fileName}.txt`;
  if (!fs.existsSync("output")) {
    fs.mkdirSync("output");
  }
  fs.writeFileSync(outputFilePath, "");
  const json = convertCsv(csv);
  // Format validFrom and validTo. Ex. 25.01.2021 -> 2021-01-25
  // Use '$$' in csv file as placeholder for ';', because it's used as delimiter and it can not be escaped.
  // Here '$$' will be replaced with ';' in json-object values.
  json.forEach((obj) => {
    obj.validFrom = getFormattedDate(obj.validFrom);
    obj.validTo = getFormattedDate(obj.validTo);
    // if it's a transport-company-relation import
    if (obj.sboid){
      obj.sboid = 'ch:1:sboid:' + obj.sboid;
    }
    for (let key in obj) {
      obj[key] = obj[key].replaceAll("$$", ";");
    }
  });
  for (let i = 0; i < json.length; i++) {
    if (url.includes("sublines")) {
      const slnidResult = await getSlnidForSubline(
        token,
        url,
        json[i].mainLineSwissLineNumber
      );
      if (slnidResult.status !== 200) {
        logToFile(slnidResult, i + 2);
        failedImports++;
        continue;
      }
      if (slnidResult.data.totalCount === 0) {
        logToFile(
          null,
          i + 2,
          `[ERROR] Not found a line with swissLineNumber: ${json[i].mainLineSwissLineNumber}`
        );
        console.log(
          `[ERROR] Not found a line with swissLineNumber: ${json[i].mainLineSwissLineNumber}\n`
        );
        failedImports++;
        continue;
      }
      if (slnidResult.data.totalCount > 1) {
        const filteredLine = slnidResult.data.objects.filter(
          (value) => value.swissLineNumber === json[i].mainLineSwissLineNumber
        );
        if (filteredLine.length === 1) {
          json[i].mainlineSlnid = filteredLine[0].slnid;
          delete json[i].mainLineSwissLineNumber;
          logToFile(
            null,
            i + 2,
            `[WARNING] Get-Request for mainlineSlnid search answered with more than 1 result --> ${JSON.stringify(
              filteredLine[0]
            )} is used`
          );
        } else {
          logToFile(
            null,
            i + 2,
            "[ERROR] Get-Request for mainlineSlnid search answered with more than 1 result --> not found a matching line"
          );
          console.log(
            "[ERROR] Get-Request for mainlineSlnid search answered with more than 1 result --> not found a matching line\n"
          );
          failedImports++;
          continue;
        }
      }
      if (slnidResult.data.totalCount === 1) {
        json[i].mainlineSlnid = slnidResult.data.objects[0].slnid;
        delete json[i].mainLineSwissLineNumber;
      }
    }
    if (url.includes('transport-company-relations')) {
      json[i].transportCompanyId = await getTransportCompanyId(url, token,
        json[i].number);
    }
    const apiResult = await postData(url, json[i], token);
    if (apiResult.status !== 201) {
      logToFile(apiResult, i + 2);
      failedImports++;
    }
  }
  return [failedImports, json.length];
}

async function getTransportCompanyId(url, token, number) {
  const index = url.indexOf("transport-company-relations");
  const hostUrl = url.substring(0, index);
  number = number.replaceAll("#", "%23");
  try {
    const response = await axios.get(
      `${hostUrl}transport-companies?searchCriteria=${number}&sort=number,ASC`,
      {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      }
    );
    return response.data.objects[0].id;
  } catch (e) {
    const customError = {
      status: e.response.status,
      message: e.response.data.message ?? e.response.data,
    };
    console.log(
      `Get-Request for transportCompanyId failed: ${JSON.stringify(customError)}\n`
    );
    return customError;
  }
}

function getFormattedDate(dateString) {
  const dateSplit = dateString.split(".");
  if (dateSplit.length !== 3) {
    return dateString;
  }
  return [dateSplit[2], dateSplit[1], dateSplit[0]].join("-");
}

function convertCsv(csvFilePath) {
  return csvToJson.getJsonFromCsv(csvFilePath);
}

async function getSlnidForSubline(token, url, mainLineSwissLineNumber) {
  const index = url.indexOf("sublines");
  const hostUrl = url.substring(0, index);
  try {
    const response = await axios.get(
      `${hostUrl}lines?swissLineNumber=${mainLineSwissLineNumber}`,
      {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      }
    );
    return {
      status: response.status,
      data: response.data,
    };
  } catch (e) {
    const customError = {
      status: e.response.status,
      message: e.response.data.message ?? e.response.data,
    };
    console.log(
      `Get-Request for mainlineSlnid failed: ${JSON.stringify(customError)}\n`
    );
    return customError;
  }
}

async function postData(url, body, token) {
  try {
    return await axios.post(url, body, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });
  } catch (e) {
    const responseError = e.response;
    if (!responseError) {
      console.log(
        `Did not receive response with body: ${JSON.stringify(
          body
        )}, catchedError: ${JSON.stringify(e)}\n`
      );
      return { status: 500, catchedError: JSON.stringify(e) };
    }
    const customError = {
      status: responseError.status,
      message: responseError.data.message ?? responseError.data,
      details: responseError.data.details?.map((value) => {
        return {
          message: value.message,
        };
      }),
    };
    console.log(
      `Response: ${customError.status}, data: ${JSON.stringify(customError)}\n`
    );
    return customError;
  }
}

function logToFile(apiResult, lineNumber, message = "") {
  if (message) {
    fs.appendFileSync(
      outputFilePath,
      `Line Number ${lineNumber}: ${message}\n`
    );
  } else {
    const defaultMessage = `Line Number ${lineNumber}: Status Code: ${
      apiResult.status
    } and Response: ${JSON.stringify(apiResult.data ?? apiResult)}`;
    fs.appendFileSync(outputFilePath, defaultMessage + "\n");
  }
}
