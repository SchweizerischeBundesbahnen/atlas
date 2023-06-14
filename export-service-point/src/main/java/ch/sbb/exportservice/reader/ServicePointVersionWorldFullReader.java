package ch.sbb.exportservice.reader;

import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;

@Slf4j
public class ServicePointVersionWorldFullReader extends BaseServicePointVersionReader {

  @Value("#{jobParameters[exportType]}")
  public String exportType;

  public ServicePointVersionWorldFullReader(@Autowired
  @Qualifier("servicePointDataSource") DataSource dataSource) {
    super(dataSource);

    log.info("aaaaaaaaa: {}", exportType);
  }

  @Override
  protected String sqlWhereClause() {
    return null;
  }

}
