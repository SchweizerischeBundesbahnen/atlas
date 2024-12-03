package ch.sbb.importservice.sepodi.writer;

import ch.sbb.importservice.client.ServicePointBulkImportClient;
import ch.sbb.importservice.service.sepodi.service.point.create.ServicePointCreateWriter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.core.StepExecution;

@ExtendWith(MockitoExtension.class)
class ServicePointCreateWriterTest {

    @Mock
    private ServicePointBulkImportClient servicePointBulkImportClient;

    @Mock
    private StepExecution stepExecution;

    @InjectMocks
    private ServicePointCreateWriter servicePointCreateWriter;


    @Test
    void shouldWriter() {

    }
}
