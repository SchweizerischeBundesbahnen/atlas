package ch.sbb.exportservice.reader;

import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;

@Slf4j
public class ServicePointVersionSwissFullReader extends BaseServicePointVersionReader {

  @Value("#{jobParameters[exportType]}")
  public String exportType;

  public ServicePointVersionSwissFullReader(@Autowired
  @Qualifier("servicePointDataSource") DataSource dataSource) {
    super(dataSource);
    log.info("aaaaaaaaa: {}", exportType);
  }

  @Override
  protected String sqlWhereClause() {
    return "WHERE spv.country IN('SWITZERLAND','GERMANY_BUS','AUSTRIA_BUS','ITALY_BUS','FRANCE_BUS') ";
  }

}
